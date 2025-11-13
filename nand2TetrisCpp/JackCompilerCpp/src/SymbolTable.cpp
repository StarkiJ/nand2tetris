#include "SymbolTable.hpp"
#include <algorithm>
#include <stdexcept>

// NOTE: This file is a student skeleton. Fill all TODOs.

SymbolTable::SymbolTable() {
  // TODO: initialize indices and clear tables
  throw std::logic_error("TODO(SymbolTable::SymbolTable)");
}

void SymbolTable::startSubroutine() {
  // TODO: Reset subroutine scope (subTable_, argIndex_, varIndex_)
  throw std::logic_error("TODO(SymbolTable::startSubroutine)");
}

void SymbolTable::define(const std::string &name, const std::string &type,
                         const std::string &kind) {
  // TODO: Insert into classTable_ or subTable_ depending on kind
  //       and assign running index (staticIndex_, fieldIndex_, argIndex_, varIndex_)
  throw std::logic_error("TODO(SymbolTable::define)");
}

int SymbolTable::varCount(const std::string &kind) const {
  // TODO: Return number of identifiers of given kind currently defined
  throw std::logic_error("TODO(SymbolTable::varCount)");
}

std::string SymbolTable::kindOf(const std::string &name) const {
  // TODO: Return kind of identifier ('static'|'field'|'arg'|'var' or "NONE")
  throw std::logic_error("TODO(SymbolTable::kindOf)");
}

std::string SymbolTable::typeOf(const std::string &name) const {
  // TODO: Return type of identifier
  throw std::logic_error("TODO(SymbolTable::typeOf)");
}

int SymbolTable::indexOf(const std::string &name) const {
  // TODO: Return running index assigned to identifier
  throw std::logic_error("TODO(SymbolTable::indexOf)");
}
