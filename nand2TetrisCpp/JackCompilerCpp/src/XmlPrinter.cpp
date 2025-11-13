#include "XmlPrinter.hpp"
#include <stdexcept>

static std::string toLower(std::string s) {
  for (char &c : s)
    if (c >= 'A' && c <= 'Z')
      c = char(c - 'A' + 'a');
  return s;
}

XmlPrinter::XmlPrinter(const std::string &file_name) {
  out_.open(file_name);
  if (!out_)
    throw std::runtime_error("Cannot open XML file: " + file_name);
}

XmlPrinter::~XmlPrinter() {
  while (!stack_.empty())
    closeTag();
  if (out_.is_open())
    out_.close();
}

void XmlPrinter::indent() {
  for (int i = 0; i < indent_; ++i)
    out_ << "  ";
}

std::string XmlPrinter::escape(const std::string &s) {
  std::string r;
  r.reserve(s.size() + 8);
  for (char c : s) {
    if (c == '<')
      r += "&lt;";
    else if (c == '>')
      r += "&gt;";
    else if (c == '&')
      r += "&amp;";
    else
      r.push_back(c);
  }
  return r;
}

void XmlPrinter::openTag(const std::string &name) {
  indent();
  out_ << "<" << name << ">\n";
  stack_.push_back(name);
  indent_++;
}

void XmlPrinter::closeTag() {
  if (stack_.empty())
    return;
  indent_--;
  indent();
  out_ << "</" << stack_.back() << ">\n";
  stack_.pop_back();
}

void XmlPrinter::token(const std::string &type, const std::string &value) {
  // Match nand2tetris token tags: keyword / symbol / identifier /
  // integerConstant / stringConstant
  std::string tag;
  std::string t = toLower(type);
  if (t == "keyword")
    tag = "keyword";
  else if (t == "symbol")
    tag = "symbol";
  else if (t == "identifier")
    tag = "identifier";
  else if (t == "int_const" || t == "int")
    tag = "integerConstant";
  else if (t == "string_const" || t == "string")
    tag = "stringConstant";
  else
    tag = type;

  indent();
  out_ << "<" << tag << "> " << escape(value) << " </" << tag << ">\n";
}
