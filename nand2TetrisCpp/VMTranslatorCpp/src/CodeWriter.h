#pragma once
#include <fstream>
#include <string>

// Emits Hack assembly code for VM commands.
class CodeWriter {
public:
  explicit CodeWriter(const std::string &out_path);

  // Inform code writer that we are translating a new VM file (affects static
  // variables naming).
  void setFileName(const std::string &module_name);

  // Bootstrap code (SP=256; call Sys.init).
  void writeInit();

  // Arithmetic/logical
  void writeArithmetic(const std::string &command);

  // push/pop
  void writePushPop(const std::string &ctype, const std::string &segment,
                    int index);

  // program flow
  void writeLabel(const std::string &label);
  void writeGoto(const std::string &label);
  void writeIf(const std::string &label);

  // functions
  void writeFunction(const std::string &functionName, int numLocals);
  void writeCall(const std::string &functionName, int numArgs);
  void writeReturn();

  void close();

private:
  std::ofstream out_;
  std::string module_name_;
  int label_id_ = 0;

  void emit(const std::string &s) { out_ << s << "\n"; }
  void pushD();
  void popToD();
  void segToD(const std::string &segment, int index);
};
