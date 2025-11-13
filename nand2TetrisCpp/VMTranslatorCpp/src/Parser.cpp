#include "Parser.h"
#include <cctype>
#include <filesystem>
#include <fstream>
#include <sstream>
#include <stdexcept>

using namespace std;

static string toLower(string s) {
  for (char &c : s)
    c = (char)tolower((unsigned char)c);
  return s;
}

Parser::Parser(const std::string &file_path) : file_path_(file_path) {
  // module name = filename without extension
  std::filesystem::path p(file_path);
  module_name_ = p.stem().string();

  std::ifstream in(file_path);
  if (!in.is_open()) {
    throw std::runtime_error("Failed to open " + file_path);
  }
  std::string line;
  while (std::getline(in, line)) {
    auto s = stripComment(trim(line));
    if (!s.empty()) {
      lines_.push_back(s);
    }
  }
  index_ = 0;
  if (!lines_.empty()) {
    current_ = tokenize(lines_[0]);
  }
}

bool Parser::hasMoreCommands() const { return index_ < lines_.size(); }

void Parser::advance() {
  if (index_ + 1 < lines_.size()) {
    ++index_;
    current_ = tokenize(lines_[index_]);
  } else {
    index_ = lines_.size();
    current_.clear();
  }
}

std::string Parser::commandType() const {
  if (current_.empty())
    return "";
  const string &c = current_[0];
  if (c == "push")
    return "C_PUSH";
  if (c == "pop")
    return "C_POP";
  if (c == "label")
    return "C_LABEL";
  if (c == "goto")
    return "C_GOTO";
  if (c == "if-goto")
    return "C_IF";
  if (c == "function")
    return "C_FUNCTION";
  if (c == "return")
    return "C_RETURN";
  if (c == "call")
    return "C_CALL";
  // arithmetic or logical
  return "C_ARITHMETIC";
}

std::string Parser::arg1() const {
  auto type = commandType();
  if (type == "")
    return "";
  if (type == "C_ARITHMETIC") {
    return current_[0];
  }
  if (type == "C_RETURN") {
    throw std::runtime_error("arg1() should not be called for C_RETURN");
  }
  if (current_.size() < 2)
    return "";
  return current_[1];
}

int Parser::arg2() const {
  auto type = commandType();
  if (!(type == "C_PUSH" || type == "C_POP" || type == "C_FUNCTION" ||
        type == "C_CALL")) {
    throw std::runtime_error(
        "arg2() called on command without a second argument");
  }
  if (current_.size() < 3) {
    throw std::runtime_error("Missing arg2");
  }
  return stoi(current_[2]);
}

std::string Parser::moduleName() const { return module_name_; }

std::string Parser::trim(const std::string &s) {
  size_t i = 0, j = s.size();
  while (i < j && std::isspace((unsigned char)s[i]))
    ++i;
  while (j > i && std::isspace((unsigned char)s[j - 1]))
    --j;
  return s.substr(i, j - i);
}

std::string Parser::stripComment(const std::string &s) {
  auto pos = s.find("//");
  if (pos != std::string::npos)
    return s.substr(0, pos);
  return s;
}

std::vector<std::string> Parser::tokenize(const std::string &s) {
  std::istringstream iss(s);
  std::vector<std::string> out;
  std::string tok;
  while (iss >> tok)
    out.push_back(tok);
  return out;
}
