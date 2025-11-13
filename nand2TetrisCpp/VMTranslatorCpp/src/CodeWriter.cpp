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
  writeCall("Sys.init", 0);
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
  } else if (segment == "local") {
    emit("@" + std::to_string(index));
    emit("D=A");
    emit("@LCL");
    emit("A=D+M");
    emit("D=M");
  } else if (segment == "argument") {
    emit("@" + std::to_string(index));
    emit("D=A");
    emit("@ARG");
    emit("A=D+M");
    emit("D=M");
  } else if (segment == "this") {
    emit("@" + std::to_string(index));
    emit("D=A");
    emit("@THIS");
    emit("A=D+M");
    emit("D=M");
  } else if (segment == "that") {
    emit("@" + std::to_string(index));
    emit("D=A");
    emit("@THAT");
    emit("A=D+M");
    emit("D=M");
  } else if (segment == "temp") {
    emit("@R" + std::to_string(5 + index));
    emit("D=M");
  } else if (segment == "pointer") {
    if (index == 0) {
      emit("@THIS");
      emit("D=M");
    } else if (index == 1) {
      emit("@THAT");
      emit("D=M");
    } else {
      throw std::runtime_error("pointer index must be 0 or 1");
    }
  } else if (segment == "static") {
    emit("@" + module_name_ + "." + std::to_string(index));
    emit("D=M");
  } else {
    throw std::runtime_error("Unknown segment: " + segment);
  }
}

void CodeWriter::writeArithmetic(const std::string &command) {
  emit("// " + command);
  if (command == "add" || command == "sub" || command == "and" ||
      command == "or") {
    // binary ops
    popToD(); // D = y
    emit("@SP");
    emit("A=M-1"); // x at *SP-1
    if (command == "add")
      emit("M=D+M");
    else if (command == "sub")
      emit("M=M-D");
    else if (command == "and")
      emit("M=M&D");
    else if (command == "or")
      emit("M=M|D");
  } else if (command == "neg" || command == "not") {
    // unary ops on top
    emit("@SP");
    emit("A=M-1");
    if (command == "neg")
      emit("M=-M");
    else
      emit("M=!M");
  } else if (command == "eq" || command == "gt" || command == "lt") {
    // comparison: x op y -> push bool
    popToD(); // D = y
    emit("@SP");
    emit("A=M-1"); // x in M
    emit("D=M-D"); // D = x - y
    std::string tru = (command == "eq"   ? "EQ_TRUE_"
                       : command == "gt" ? "GT_TRUE_"
                                         : "LT_TRUE_") +
                      std::to_string(label_id_);
    std::string end = (command == "eq"   ? "EQ_END_"
                       : command == "gt" ? "GT_END_"
                                         : "LT_END_") +
                      std::to_string(label_id_);
    if (command == "eq")
      emit("@" + tru), emit("D;JEQ");
    else if (command == "gt")
      emit("@" + tru), emit("D;JGT");
    else
      emit("@" + tru), emit("D;JLT");
    // false -> 0
    emit("@SP");
    emit("A=M-1");
    emit("M=0");
    emit("@" + end);
    emit("0;JMP");
    // true -> -1
    emit("(" + tru + ")");
    emit("@SP");
    emit("A=M-1");
    emit("M=-1");
    emit("(" + end + ")");
    label_id_++;
  } else {
    throw std::runtime_error("Invalid arithmetic: " + command);
  }
}

void CodeWriter::writePushPop(const std::string &ctype,
                              const std::string &segment, int index) {
  if (ctype == "C_PUSH") {
    emit("// push " + segment + " " + std::to_string(index));
    segToD(segment, index);
    pushD();
  } else if (ctype == "C_POP") {
    emit("// pop " + segment + " " + std::to_string(index));
    if (segment == "temp") {
      popToD();
      emit("@R" + std::to_string(5 + index));
      emit("M=D");
    } else if (segment == "pointer") {
      popToD();
      if (index == 0)
        emit("@THIS");
      else if (index == 1)
        emit("@THAT");
      else
        throw std::runtime_error("pointer index must be 0 or 1");
      emit("M=D");
    } else if (segment == "static") {
      popToD();
      emit("@" + module_name_ + "." + std::to_string(index));
      emit("M=D");
    } else if (segment == "local" || segment == "argument" ||
               segment == "this" || segment == "that") {
      // compute base+index in R13
      if (segment == "local")
        emit("@LCL");
      else if (segment == "argument")
        emit("@ARG");
      else if (segment == "this")
        emit("@THIS");
      else
        emit("@THAT");
      emit("D=M");
      emit("@" + std::to_string(index));
      emit("D=D+A");
      emit("@R13");
      emit("M=D");
      // pop to *addr
      popToD();
      emit("@R13");
      emit("A=M");
      emit("M=D");
    } else {
      throw std::runtime_error("Bad segment for pop: " + segment);
    }
  } else {
    throw std::runtime_error("writePushPop: bad ctype " + ctype);
  }
}

void CodeWriter::writeLabel(const std::string &label) {
  emit("// label " + label);
  emit("(" + label + ")");
}

void CodeWriter::writeGoto(const std::string &label) {
  emit("// goto " + label);
  emit("@" + label);
  emit("0;JMP");
}

void CodeWriter::writeIf(const std::string &label) {
  emit("// if-goto " + label);
  popToD();
  emit("@" + label);
  emit("D;JNE");
}

void CodeWriter::writeFunction(const std::string &functionName, int numLocals) {
  emit("// function " + functionName + " " + std::to_string(numLocals));
  emit("(" + functionName + ")");
  for (int i = 0; i < numLocals; ++i) {
    emit("@SP");
    emit("A=M");
    emit("M=0");
    emit("@SP");
    emit("M=M+1");
  }
}

void CodeWriter::writeCall(const std::string &functionName, int numArgs) {
  emit("// call " + functionName + " " + std::to_string(numArgs));
  std::string ret = "RET_" + std::to_string(label_id_++);
  // push return-address
  emit("@" + ret);
  emit("D=A");
  pushD();
  // push LCL ARG THIS THAT
  for (auto seg : {"LCL", "ARG", "THIS", "THAT"}) {
    emit(std::string("@") + seg);
    emit("D=M");
    pushD();
  }
  // ARG = SP - n - 5
  emit("@SP");
  emit("D=M");
  emit("@" + std::to_string(numArgs + 5));
  emit("D=D-A");
  emit("@ARG");
  emit("M=D");
  // LCL = SP
  emit("@SP");
  emit("D=M");
  emit("@LCL");
  emit("M=D");
  // goto f
  emit("@" + functionName);
  emit("0;JMP");
  // (ret)
  emit("(" + ret + ")");
}

void CodeWriter::writeReturn() {
  emit("// return");
  // FRAME = LCL (R13)
  emit("@LCL");
  emit("D=M");
  emit("@R13");
  emit("M=D");
  // RET = *(FRAME-5) (R14)
  emit("@5");
  emit("A=D-A");
  emit("D=M");
  emit("@R14");
  emit("M=D");
  // *ARG = pop()
  popToD();
  emit("@ARG");
  emit("A=M");
  emit("M=D");
  // SP = ARG + 1
  emit("@ARG");
  emit("D=M+1");
  emit("@SP");
  emit("M=D");
  // THAT = *(FRAME-1)
  emit("@R13");
  emit("AM=M-1");
  emit("D=M");
  emit("@THAT");
  emit("M=D");
  // THIS = *(FRAME-2)
  emit("@R13");
  emit("AM=M-1");
  emit("D=M");
  emit("@THIS");
  emit("M=D");
  // ARG = *(FRAME-3)
  emit("@R13");
  emit("AM=M-1");
  emit("D=M");
  emit("@ARG");
  emit("M=D");
  // LCL = *(FRAME-4)
  emit("@R13");
  emit("AM=M-1");
  emit("D=M");
  emit("@LCL");
  emit("M=D");
  // goto RET
  emit("@R14");
  emit("A=M");
  emit("0;JMP");
}

void CodeWriter::close() {
  out_.flush();
  out_.close();
}
