#pragma once
#include <fstream>
#include <string>
#include <vector>

class XmlPrinter {
public:
  explicit XmlPrinter(const std::string &file_name);
  ~XmlPrinter();

  void openTag(const std::string &name);
  void closeTag(); // closes the most recent tag
  // Emit a terminal token tag: keyword/symbol/identifier/int_const/string_const
  void token(const std::string &type, const std::string &value);

private:
  std::ofstream out_;
  std::vector<std::string> stack_;
  int indent_ = 0;

  void indent();
  static std::string escape(const std::string &s);
};
