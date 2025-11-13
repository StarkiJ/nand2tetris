#include "JackTokenizer.hpp"
#include <algorithm>
#include <cctype>
#include <fstream>
#include <sstream>
#include <stdexcept>
#include <unordered_map>

// NOTE: This file is a student skeleton. Fill all TODOs.
// We keep the keyword/symbol tables to help development.

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
  // TODO: detect if more tokens are available
  throw std::logic_error("TODO(JackTokenizer::hasMoreTokens)");
}

void JackTokenizer::advance() {
  // TODO: read next token and update currentToken_
  throw std::logic_error("TODO(JackTokenizer::advance)");
}

std::string JackTokenizer::tokenType() const {
  // TODO: return one of KEYWORD|SYMBOL|IDENTIFIER|INT_CONST|STRING_CONST
  throw std::logic_error("TODO(JackTokenizer::tokenType)");
}

std::string JackTokenizer::keyword() const {
  // TODO: only valid if tokenType() == KEYWORD
  throw std::logic_error("TODO(JackTokenizer::keyword)");
}

std::string JackTokenizer::symbol() const {
  // TODO: return symbol with XML escaping for < > &
  throw std::logic_error("TODO(JackTokenizer::symbol)");
}

std::string JackTokenizer::identifier() const {
  // TODO
  throw std::logic_error("TODO(JackTokenizer::identifier)");
}

int JackTokenizer::intVal() const {
  // TODO
  throw std::logic_error("TODO(JackTokenizer::intVal)");
}

std::string JackTokenizer::stringVal() const {
  // TODO: without the surrounding quotes
  throw std::logic_error("TODO(JackTokenizer::stringVal)");
}

void JackTokenizer::dumpTokensXml(const std::string &out_base) const {
  // (optional for XML debug) TODO: emit <tokens>...</tokens> list
  throw std::logic_error("TODO(JackTokenizer::dumpTokensXml)");
}
