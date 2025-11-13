#include "XmlPrinter.hpp"
#include <stdexcept>

// NOTE: This file is a student skeleton. Fill all TODOs.
XmlPrinter::XmlPrinter(const std::string &file_name) {
  // TODO: open XML output file
  throw std::logic_error("TODO(XmlPrinter::XmlPrinter)");
}

XmlPrinter::~XmlPrinter() {
  // TODO: close any open tags and file
  throw std::logic_error("TODO(XmlPrinter::~XmlPrinter)");
}

void XmlPrinter::openTag(const std::string &name) {
  // TODO: increase indent and write <name>
  throw std::logic_error("TODO(XmlPrinter::openTag)");
}

void XmlPrinter::closeTag() {
  // TODO: decrease indent and write </name>
  throw std::logic_error("TODO(XmlPrinter::closeTag)");
}

void XmlPrinter::token(const std::string &type, const std::string &value) {
  // TODO: write a token element with proper tag mapping and escaping
  throw std::logic_error("TODO(XmlPrinter::token)");
}
