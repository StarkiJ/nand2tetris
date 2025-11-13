#include "SymbolTable.hpp"
#include <algorithm>

SymbolTable::SymbolTable() {}

void SymbolTable::startSubroutine() {
  subTable_.clear();
  argIndex_ = 0;
  varIndex_ = 0;
}

void SymbolTable::define(const std::string &name, const std::string &type,
                         const std::string &kind) {
  if (kind == "static") {
    classTable_[name] = {type, kind, staticIndex_++};
  } else if (kind == "field") {
    classTable_[name] = {type, kind, fieldIndex_++};
  } else if (kind == "arg") {
    subTable_[name] = {type, kind, argIndex_++};
  } else if (kind == "var") {
    subTable_[name] = {type, kind, varIndex_++};
  }
}

int SymbolTable::varCount(const std::string &kind) const {
  int count = 0;
  const auto *table =
      (kind == "static" || kind == "field") ? &classTable_ : &subTable_;
  for (auto &kv : *table) {
    if (std::get<1>(kv.second) == kind)
      count++;
  }
  return count;
}

std::string SymbolTable::kindOf(const std::string &name) const {
  if (subTable_.count(name))
    return std::get<1>(subTable_.at(name));
  if (classTable_.count(name))
    return std::get<1>(classTable_.at(name));
  return "NONE";
}

std::string SymbolTable::typeOf(const std::string &name) const {
  if (subTable_.count(name))
    return std::get<0>(subTable_.at(name));
  if (classTable_.count(name))
    return std::get<0>(classTable_.at(name));
  return "";
}

int SymbolTable::indexOf(const std::string &name) const {
  if (subTable_.count(name))
    return std::get<2>(subTable_.at(name));
  if (classTable_.count(name))
    return std::get<2>(classTable_.at(name));
  return -1;
}
