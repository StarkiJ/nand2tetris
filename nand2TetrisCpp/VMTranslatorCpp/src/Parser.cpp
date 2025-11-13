#include "Parser.h"
#include <cctype>
#include <filesystem>
#include <fstream>
#include <sstream>
#include <stdexcept>

using namespace std;

static std::string toLower(std::string s) {
  for (char &c : s) c = (char)tolower((unsigned char)c);
  return s;
}

Parser::Parser(const std::string &file_path) : file_path_(file_path) {
  // module name = filename without extension
  std::filesystem::path p(file_path);
  module_name_ = p.stem().string();
  /*todo*/ // 读取文件、去注释/空白并填充 lines_
}

bool Parser::hasMoreCommands() const { /*todo */ return false; }

void Parser::advance() { /*todo */ }

std::string Parser::commandType() const {
  /*todo */ return ""; // 根据 current_ 判定类型
}

std::string Parser::arg1() const {
  /*todo */ return ""; // arithmetic 返回操作符，其它返回第一个参数
}

int Parser::arg2() const {
  /*todo */ return 0; // 仅在 PUSH/POP/FUNCTION/CALL 可用
}

std::string Parser::moduleName() const { return module_name_; }

std::string Parser::trim(const std::string &s) { /*todo */ return s; }
std::string Parser::stripComment(const std::string &s) { /*todo */ return s; }
std::vector<std::string> Parser::tokenize(const std::string &s) {
  /*todo */ return {}; }