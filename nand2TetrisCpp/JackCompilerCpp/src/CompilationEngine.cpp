#include "CompilationEngine.hpp"
#include <iostream>
#include <stdexcept>
#include <unordered_set>

static const std::unordered_set<std::string> kOps = {"+", "-", "*", "/", "&",
                                                     "|", "<", ">", "="};
static const std::unordered_set<std::string> kUnary = {"-", "~"};
static const std::unordered_set<std::string> kKeywordConst = {"true", "false",
                                                              "null", "this"};

// NOTE: This file is a student skeleton. Fill all TODOs.
// Helper methods can be provided; the main compile* routines are left blank.

CompilationEngine::CompilationEngine(JackTokenizer &tokenizer,
                                     const std::string &out_base,
                                     bool xml_debug)
  : tokenizer_(tokenizer), vm_(out_base + ".vm"), xml_debug_(xml_debug) {
  // TODO(optional): set up XmlPrinter if xml_debug_ is true
  throw std::logic_error("TODO(CompilationEngine::CompilationEngine)");
}

void CompilationEngine::emitCurrentToken() {
  // TODO(optional): emit current token into XML when xml_debug_ is true
  throw std::logic_error("TODO(CompilationEngine::emitCurrentToken)");
}

void CompilationEngine::expect(const std::string &tok) {
  // TODO: assert current token equals tok, emit & advance
  throw std::logic_error("TODO(CompilationEngine::expect)");
}

bool CompilationEngine::accept(const std::string &tok) {
  // TODO: if current token equals tok, consume and return true
  throw std::logic_error("TODO(CompilationEngine::accept)");
}

std::string CompilationEngine::kindToSegment(const std::string &kind) {
  // TODO: map symbol kinds to VM segments: static->static, field->this, arg->argument, var->local
  throw std::logic_error("TODO(CompilationEngine::kindToSegment)");
}

void CompilationEngine::compileType() {
  // TODO: 'int' | 'char' | 'boolean' | className
  throw std::logic_error("TODO(CompilationEngine::compileType)");
}

void CompilationEngine::compileClass() {
  // TODO: 'class' className '{' classVarDec* subroutineDec* '}'
  throw std::logic_error("TODO(CompilationEngine::compileClass)");
}

void CompilationEngine::compileClassVarDec() {
  // TODO: ('static'|'field') type varName (',' varName)* ';'
  throw std::logic_error("TODO(CompilationEngine::compileClassVarDec)");
}

void CompilationEngine::compileSubroutine() {
  // TODO: ('constructor'|'function'|'method') ('void'|type) subroutineName '(' parameterList ')' subroutineBody
  throw std::logic_error("TODO(CompilationEngine::compileSubroutine)");
}

void CompilationEngine::compileParameterList(bool /*isMethod*/) {
  // TODO: ((type varName) (',' type varName)*)?
  throw std::logic_error("TODO(CompilationEngine::compileParameterList)");
}

void CompilationEngine::compileVarDec() {
  // TODO: 'var' type varName (',' varName)* ';'
  throw std::logic_error("TODO(CompilationEngine::compileVarDec)");
}

void CompilationEngine::compileStatements() {
  // TODO: (let|if|while|do|return)*
  throw std::logic_error("TODO(CompilationEngine::compileStatements)");
}

void CompilationEngine::compileLet() {
  // TODO
  throw std::logic_error("TODO(CompilationEngine::compileLet)");
}

void CompilationEngine::compileIf() {
  // TODO
  throw std::logic_error("TODO(CompilationEngine::compileIf)");
}

void CompilationEngine::compileWhile() {
  // TODO
  throw std::logic_error("TODO(CompilationEngine::compileWhile)");
}

void CompilationEngine::compileDo() {
  // TODO
  throw std::logic_error("TODO(CompilationEngine::compileDo)");
}

void CompilationEngine::compileReturn() {
  // TODO
  throw std::logic_error("TODO(CompilationEngine::compileReturn)");
}

void CompilationEngine::compileExpression() {
  // TODO: term (op term)*
  throw std::logic_error("TODO(CompilationEngine::compileExpression)");
}

void CompilationEngine::compileTerm() {
  // TODO: implement all 11 forms of term
  throw std::logic_error("TODO(CompilationEngine::compileTerm)");
}

int CompilationEngine::compileExpressionList() {
  // TODO: (expression (',' expression)*)?
  throw std::logic_error("TODO(CompilationEngine::compileExpressionList)");
}
