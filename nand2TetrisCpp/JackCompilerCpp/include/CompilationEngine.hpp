#pragma once
#include "JackTokenizer.hpp"
#include "SymbolTable.hpp"
#include "VMWriter.hpp"
#include "XmlPrinter.hpp"
#include <memory>
#include <string>

// CompilationEngine: recursive-descent compiler producing VM
class CompilationEngine {
public:
  CompilationEngine(JackTokenizer &tokenizer, const std::string &out_base,
                    bool xml_debug = false);

  void compileClass();

private:
  // subroutines
  void compileClassVarDec();
  void compileSubroutine();
  void compileParameterList(bool isMethod);
  void compileVarDec();
  void compileStatements();
  void compileLet();
  void compileIf();
  void compileWhile();
  void compileDo();
  void compileReturn();
  void compileExpression();
  void compileTerm();
  int compileExpressionList();

  // helpers
  void expect(const std::string &tok);
  bool accept(const std::string &tok);
  void compileType();      // consumes a type token or 'void'
  void emitCurrentToken(); // write current token to XML (parse tree) if enabled
  std::string kindToSegment(const std::string &kind);
  std::string nextTokenRaw() const { return tokenizer_.getToken(); }

private:
  JackTokenizer &tokenizer_;
  VMWriter vm_;
  SymbolTable symbols_;
  std::string className_;
  int labelCounter_ = 0;

  bool xml_debug_ = false;
  std::unique_ptr<XmlPrinter> px_;
};
