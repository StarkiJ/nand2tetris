#pragma once
#include <string>
#include <tuple>
#include <unordered_map>

// SymbolTable: tracks identifiers at class and subroutine scopes
// kind: "static" | "field" | "arg" | "var" | "NONE"
class SymbolTable {
public:
  SymbolTable();

  // Start a new subroutine scope (clears subroutine table and resets arg/var
  // indices)
  void startSubroutine();

  // Define a new identifier and assign it a running index for its kind
  void define(const std::string &name, const std::string &type,
              const std::string &kind);

  // Return number of variables of given kind in the current scope (class or
  // subroutine)
  int varCount(const std::string &kind) const;

  // Return kind of given identifier ("static","field","arg","var","NONE")
  std::string kindOf(const std::string &name) const;

  // Return type string
  std::string typeOf(const std::string &name) const;

  // Return running index of identifier
  int indexOf(const std::string &name) const;

private:
  using Entry =
      std::tuple<std::string /*type*/, std::string /*kind*/, int /*index*/>;
  std::unordered_map<std::string, Entry> classTable_;
  std::unordered_map<std::string, Entry> subTable_;
  int staticIndex_ = 0;
  int fieldIndex_ = 0;
  int argIndex_ = 0;
  int varIndex_ = 0;
};
