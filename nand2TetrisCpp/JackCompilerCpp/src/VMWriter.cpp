#include "VMWriter.hpp"
#include <stdexcept>

// NOTE: This file is a student skeleton. Fill all TODOs.

VMWriter::VMWriter(const std::string &file_name) {
  // TODO: open output stream for writing
  throw std::logic_error("TODO(VMWriter::VMWriter)");
}

VMWriter::~VMWriter() {
  // TODO: close the stream
  throw std::logic_error("TODO(VMWriter::~VMWriter)");
}

void VMWriter::writePush(const std::string &segment, int index) {
  // TODO: emit `push <segment> <index>`
  throw std::logic_error("TODO(VMWriter::writePush)");
}
void VMWriter::writePop(const std::string &segment, int index) {
  // TODO: emit `pop <segment> <index>`
  throw std::logic_error("TODO(VMWriter::writePop)");
}
void VMWriter::writeArithmetic(const std::string &cmd) {
  // TODO: emit arithmetic/logic VM command
  throw std::logic_error("TODO(VMWriter::writeArithmetic)");
}
void VMWriter::writeLabel(const std::string &label) {
  // TODO: emit `label <label>`
  throw std::logic_error("TODO(VMWriter::writeLabel)");
}
void VMWriter::writeGoto(const std::string &label) {
  // TODO: emit `goto <label>`
  throw std::logic_error("TODO(VMWriter::writeGoto)");
}
void VMWriter::writeIf(const std::string &label) {
  // TODO: emit `if-goto <label>`
  throw std::logic_error("TODO(VMWriter::writeIf)");
}
void VMWriter::writeCall(const std::string &name, int nArgs) {
  // TODO: emit `call <name> <nArgs>`
  throw std::logic_error("TODO(VMWriter::writeCall)");
}
void VMWriter::writeFunction(const std::string &name, int nLocals) {
  // TODO: emit `function <name> <nLocals>`
  throw std::logic_error("TODO(VMWriter::writeFunction)");
}
void VMWriter::writeReturn() {
  // TODO: emit `return`
  throw std::logic_error("TODO(VMWriter::writeReturn)");
}
void VMWriter::close() {
  // TODO: actually close the stream if open
  throw std::logic_error("TODO(VMWriter::close)");
}
