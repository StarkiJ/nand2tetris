#include "CodeWriter.h"
#include <stdexcept>

using namespace std;

CodeWriter::CodeWriter(const std::string &out_path) : out_(out_path) {
  if (!out_.is_open()) {
    throw std::runtime_error("Failed to open output file: " + out_path);
  }
}

void CodeWriter::setFileName(const std::string &module_name) {
  module_name_ = module_name;
}

void CodeWriter::writeInit() {
  emit("// bootstrap");
  // SP = 256
  emit("@256");
  emit("D=A");
  emit("@SP");
  emit("M=D");
  // call Sys.init 0
  /*todo*/ // 引导调用
}

void CodeWriter::pushD() {
  emit("@SP");
  emit("A=M");
  emit("M=D");
  emit("@SP");
  emit("M=M+1");
}

void CodeWriter::popToD() {
  emit("@SP");
  emit("AM=M-1");
  emit("D=M");
}

void CodeWriter::segToD(const std::string &segment, int index) {
  if (segment == "constant") {
    emit("@" + std::to_string(index));
    emit("D=A");
  } else {
    /*todo*/ // 其余段（local/argument/this/that/temp/pointer/static）留空
  }
}

void CodeWriter::writeArithmetic(const std::string &command) {
  emit("// " + command);
  if (command == "add") {
    // 其他算子交由学生补齐
    popToD(); // D = y
    emit("@SP");
    emit("A=M-1"); // x at *SP-1
    emit("M=D+M");
  } else {
    /*todo*/ // sub/and/or/neg/not/eq/gt/lt 等
  }
}

void CodeWriter::writePushPop(const std::string &ctype,
                              const std::string &segment, int index) {
  if (ctype == "C_PUSH") {
    emit("// push " + segment + " " + std::to_string(index));
    if (segment == "constant") {
      segToD(segment, index);
      pushD();
    } else {
      /*todo*/ // 其他段的 push
    }
  } else if (ctype == "C_POP") {
    emit("// pop " + segment + " " + std::to_string(index));
    /*todo*/ // 所有 pop 留给学生实现
  } else {
    throw std::runtime_error("writePushPop: bad ctype " + ctype);
  }
}

void CodeWriter::writeLabel(const std::string &label) {
  emit("// label " + label);
  /*todo*/ // (label)
}

void CodeWriter::writeGoto(const std::string &label) {
  emit("// goto " + label);
  /*todo*/ // 无条件跳转
}

void CodeWriter::writeIf(const std::string &label) {
  emit("// if-goto " + label);
  /*todo*/ // 弹栈判非零跳转
}

void CodeWriter::writeFunction(const std::string &functionName, int numLocals) {
  emit("// function " + functionName + " " + std::to_string(numLocals));
  /*todo*/ // 定义标签并初始化本地变量为 0
}

void CodeWriter::writeCall(const std::string &functionName, int numArgs) {
  emit("// call " + functionName + " " + std::to_string(numArgs));
  /*todo*/ // 压返回地址、保存调用者、重置 ARG/LCL、跳转
}

void CodeWriter::writeReturn() {
  emit("// return");
  /*todo*/ // 按规范恢复 THAT/THIS/ARG/LCL 并跳转到返回地址
}

void CodeWriter::close() {
  out_.flush();
  out_.close();
}