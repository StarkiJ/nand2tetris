#pragma once
#include <fstream>
#include <string>

// VMWriter: emits VM commands to a .vm file
class VMWriter {
public:
  explicit VMWriter(const std::string &file_name);
  ~VMWriter();

  void writePush(const std::string &segment, int index);
  void writePop(const std::string &segment, int index);
  void writeArithmetic(
      const std::string &command); // add, sub, neg, eq, gt, lt, and, or, not
  void writeLabel(const std::string &label);
  void writeGoto(const std::string &label);
  void writeIf(const std::string &label);
  void writeCall(const std::string &name, int nArgs);
  void writeFunction(const std::string &name, int nLocals);
  void writeReturn();
  void close();

private:
  std::ofstream out_;
};
