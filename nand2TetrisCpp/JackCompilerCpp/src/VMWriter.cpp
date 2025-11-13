#include "VMWriter.hpp"
#include <stdexcept>

VMWriter::VMWriter(const std::string &file_name) {
  out_.open(file_name);
  if (!out_)
    throw std::runtime_error("Cannot open output vm: " + file_name);
}
VMWriter::~VMWriter() { close(); }

void VMWriter::writePush(const std::string &segment, int index) {
  out_ << "push " << segment << " " << index << "\n";
}
void VMWriter::writePop(const std::string &segment, int index) {
  out_ << "pop " << segment << " " << index << "\n";
}
void VMWriter::writeArithmetic(const std::string &cmd) { out_ << cmd << "\n"; }
void VMWriter::writeLabel(const std::string &label) {
  out_ << "label " << label << "\n";
}
void VMWriter::writeGoto(const std::string &label) {
  out_ << "goto " << label << "\n";
}
void VMWriter::writeIf(const std::string &label) {
  out_ << "if-goto " << label << "\n";
}
void VMWriter::writeCall(const std::string &name, int nArgs) {
  out_ << "call " << name << " " << nArgs << "\n";
}
void VMWriter::writeFunction(const std::string &name, int nLocals) {
  out_ << "function " << name << " " << nLocals << "\n";
}
void VMWriter::writeReturn() { out_ << "return\n"; }
void VMWriter::close() {
  if (out_.is_open())
    out_.close();
}
