#include "JackTokenizer.hpp"
#include <algorithm>
#include <cctype>
#include <fstream>
#include <sstream>
#include <stdexcept>
#include <unordered_map>

static inline std::string trim_right(const std::string &s) {
  size_t i = s.find_last_not_of(" \t\r\n");
  return i == std::string::npos ? std::string() : s.substr(0, i + 1);
}

JackTokenizer::JackTokenizer(const std::string &file_path)
    : file_path_(file_path) {
  // Seed first line
  ensureLine();
}

bool JackTokenizer::ensureLine() {
  if (cursor_ < current_line_.size())
    return true;
  // load next line from file stream (keeping a persistent ifstream is more
  // boilerplate; for simplicity, reopen and seek each time here is expensive.
  // We'll instead read all once.
  static std::unordered_map<std::string, std::vector<std::string>> cache;
  if (!cache.count(file_path_)) {
    std::ifstream in(file_path_);
    if (!in)
      throw std::runtime_error("Cannot open: " + file_path_);
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(in, line)) {
      // Normalize to LF; keep content
      lines.push_back(line);
    }
    cache[file_path_] = std::move(lines);
    in.close();
  }
  static std::unordered_map<std::string, size_t> line_idx;
  if (!line_idx.count(file_path_))
    line_idx[file_path_] = 0;
  auto &vec = cache[file_path_];
  auto &idx = line_idx[file_path_];
  if (idx >= vec.size())
    return false;
  current_line_ = vec[idx++] + '\n';
  cursor_ = 0;
  return true;
}

const std::unordered_set<std::string> &JackTokenizer::keywords() {
  static const std::unordered_set<std::string> k = {
      "class", "constructor", "function", "method",  "field", "static",
      "var",   "int",         "char",     "boolean", "void",  "true",
      "false", "null",        "this",     "let",     "do",    "if",
      "else",  "while",       "return"};
  return k;
}
const std::unordered_set<std::string> &JackTokenizer::symbols() {
  static const std::unordered_set<std::string> s = {
      "{", "}", "(", ")", "[", "]", ".", ",", ";", "+",
      "-", "*", "/", "&", "|", "<", ">", "=", "~"};
  return s;
}

bool JackTokenizer::hasMoreTokens() {
  if (!q_.empty())
    return true;
  if (cursor_ < current_line_.size())
    return true;
  // see if more lines
  std::string old = current_line_;
  size_t oldc = cursor_;
  bool ok = ensureLine();
  current_line_ = old;
  cursor_ = oldc;
  return ok;
}

void JackTokenizer::advancePre() {
  // produce next token into currentToken_
  currentToken_.clear();
  while (true) {
    if (!ensureLine())
      return; // no more
    if (cursor_ >= current_line_.size())
      continue;
    std::string ch(1, current_line_[cursor_++]);
    switch (state_) {
    case NORMAL: {
      if (ch == " " || ch == "\t" || ch == "\r") {
        if (!currentToken_.empty())
          return;
        else
          continue;
      }
      if (ch == "\n") {
        if (!currentToken_.empty())
          return;
        else
          continue;
      }
      if (ch == "/") {
        // could be // or /* or a symbol '/'
        if (cursor_ < current_line_.size()) {
          char nxt = current_line_[cursor_];
          if (nxt == '/') { // line comment
            // consume until end of line
            currentToken_.clear();
            current_line_.clear();
            cursor_ = 0;
            return advancePre();
          } else if (nxt == '*') {
            // block comment
            state_ = IN_COMMENT;
            // consume '*' after '/'
            cursor_++;
            continue;
          }
        }
        // else it's division symbol
        if (currentToken_.empty()) {
          currentToken_ = "/";
          return;
        } else {
          // previous token ready; step back one char for '/'
          cursor_--;
          return;
        }
      }
      if (ch == "\"") { // string const
        if (currentToken_.empty()) {
          state_ = IN_QUOTE;
          currentToken_ = "\"";
          continue;
        } else {
          // end earlier token first; step back
          cursor_--;
          return;
        }
      }
      // symbols single char
      if (symbols().count(ch)) {
        if (currentToken_.empty()) {
          currentToken_ = ch;
          return;
        } else {
          cursor_--;
          return;
        }
      }
      // otherwise part of identifier/int
      currentToken_ += ch;
      break;
    }
    case IN_QUOTE: {
      currentToken_ += ch;
      if (ch == "\"") {
        state_ = NORMAL;
        return;
      }
      break;
    }
    case IN_COMMENT: {
      // find closing */
      if (ch == "*" && cursor_ < current_line_.size() &&
          current_line_[cursor_] == '/') {
        state_ = NORMAL;
        cursor_++; // consume '/'
      }
      // keep skipping
      break;
    }
    }
  }
}

void JackTokenizer::advance() {
  if (!q_.empty()) {
    currentToken_ = q_.front();
    q_.pop();
    return;
  }
  // If queue empty, tokenize now
  if (!hasMoreTokens())
    return;
  advancePre();
  q_.push(currentToken_);
  currentToken_.clear();
  // now pop
  currentToken_ = q_.front();
  q_.pop();
  // record token (already current)
  if (record_tokens_) {
    std::string t = tokenType();
    std::string v = (t == "STRING_CONST")
                        ? stringVal()
                        : (t == "SYMBOL" ? symbol() : currentToken_);
    tokens_record_.push_back({t, v});
  }
}

const std::string *JackTokenizer::peekNextToken() const {
  if (q_.empty())
    return nullptr;
  return &q_.front();
}

std::string JackTokenizer::tokenType() const {
  if (keywords().count(currentToken_))
    return "KEYWORD";
  if (symbols().count(currentToken_))
    return "SYMBOL";
  if (!currentToken_.empty() && currentToken_[0] == '"')
    return "STRING_CONST";
  bool all_digit =
      !currentToken_.empty() &&
      std::all_of(currentToken_.begin(), currentToken_.end(), ::isdigit);
  if (all_digit)
    return "INT_CONST";
  return "IDENTIFIER";
}
std::string JackTokenizer::keyword() const { return currentToken_; }
std::string JackTokenizer::symbol() const {
  if (currentToken_ == "<")
    return "&lt;";
  if (currentToken_ == ">")
    return "&gt;";
  if (currentToken_ == "&")
    return "&amp;";
  return currentToken_;
}
std::string JackTokenizer::identifier() const { return currentToken_; }
int JackTokenizer::intVal() const { return std::stoi(currentToken_); }
std::string JackTokenizer::stringVal() const {
  if (currentToken_.size() >= 2 && currentToken_.front() == '"' &&
      currentToken_.back() == '"')
    return currentToken_.substr(1, currentToken_.size() - 2);
  return currentToken_;
}

void JackTokenizer::dumpTokensXml(const std::string &out_base) const {
  if (!record_tokens_)
    return;
  std::ofstream out(out_base + "T.xml");
  if (!out)
    throw std::runtime_error("Cannot open tokens XML: " + out_base + "T.xml");
  out << "<tokens>\n";
  for (const auto &rt : tokens_record_) {
    std::string tag = rt.type;
    for (char &c : tag)
      if (c >= 'A' && c <= 'Z')
        c = char(c - 'A' + 'a');
    if (tag == "int_const")
      tag = "integerConstant";
    else if (tag == "string_const")
      tag = "stringConstant";
    out << "  <" << tag << "> " << rt.value << " </" << tag << ">\n";
  }
  out << "</tokens>\n";
}
