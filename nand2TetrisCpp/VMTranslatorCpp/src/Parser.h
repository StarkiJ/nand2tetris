#pragma once
#include <optional>
#include <string>
#include <vector>

// Simple VM file parser for Nand2Tetris
// Reads one VM command at a time and exposes command type & arguments.
class Parser {
public:
  explicit Parser(const std::string &file_path);

  // Return whether there is another command to process.
  bool hasMoreCommands() const;

  // Advance to the next command.
  void advance();

  // Type of the current command as a string:
  // C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN,
  // C_CALL
  std::string commandType() const;

  // First argument:
  //  - for C_ARITHMETIC: the command itself (e.g., "add")
  //  - for other commands that have an arg1: the symbol or segment name
  std::string arg1() const;

  // Second argument (only for: C_PUSH, C_POP, C_FUNCTION, C_CALL).
  int arg2() const;

  // Name of the file this parser reads from (basename, without extension).
  std::string moduleName() const;

private:
  std::string file_path_;
  std::string module_name_;
  std::vector<std::string> lines_;
  size_t index_ = 0;
  std::vector<std::string> current_;

  static std::string trim(const std::string &s);
  static std::string stripComment(const std::string &s);
  static std::vector<std::string> tokenize(const std::string &s);
};
