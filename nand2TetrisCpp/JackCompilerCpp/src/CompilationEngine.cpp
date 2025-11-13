#include "CompilationEngine.hpp"
#include <iostream>
#include <stdexcept>
#include <unordered_set>

static const std::unordered_set<std::string> kOps = {"+", "-", "*", "/", "&",
                                                     "|", "<", ">", "="};
static const std::unordered_set<std::string> kUnary = {"-", "~"};
static const std::unordered_set<std::string> kKeywordConst = {"true", "false",
                                                              "null", "this"};

CompilationEngine::CompilationEngine(JackTokenizer &tokenizer,
                                     const std::string &out_base,
                                     bool xml_debug)
    : tokenizer_(tokenizer), vm_(out_base + ".vm"), xml_debug_(xml_debug) {
  if (xml_debug_) {
    px_ = std::make_unique<XmlPrinter>(out_base + ".xml");
    if (xml_debug_)
      px_->closeTag();
  }
}

void CompilationEngine::emitCurrentToken() {
  if (!xml_debug_)
    return;
  std::string typ = tokenizer_.tokenType();
  std::string val =
      (typ == "STRING_CONST")
          ? tokenizer_.stringVal()
          : (typ == "SYMBOL" ? tokenizer_.symbol() : tokenizer_.getToken());
  px_->token(typ, val);
}

void CompilationEngine::expect(const std::string &tok) {
  if (tokenizer_.getToken() != tok) {
    throw std::runtime_error("Expected '" + tok + "', got '" +
                             tokenizer_.getToken() + "'");
  }
  emitCurrentToken();
  tokenizer_.advance();
}
bool CompilationEngine::accept(const std::string &tok) {
  if (tokenizer_.getToken() == tok) {
    emitCurrentToken();
    tokenizer_.advance();
    return true;
  }
  return false;
}
std::string CompilationEngine::kindToSegment(const std::string &kind) {
  if (kind == "static")
    return "static";
  if (kind == "field")
    return "this";
  if (kind == "arg")
    return "argument";
  if (kind == "var")
    return "local";
  return "";
}
void CompilationEngine::compileType() {
  // 'int' | 'char' | 'boolean' | className
  std::string t = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance();
}

void CompilationEngine::compileClass() {
  // 'class' className '{' classVarDec* subroutineDec* '}'
  tokenizer_.advance(); // load first token
  if (tokenizer_.getToken() != "class")
    throw std::runtime_error("File must start with 'class'");
  if (xml_debug_)
    px_->openTag("class");
  expect("class");
  className_ = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance();
  expect("{");

  // reset class scope
  symbols_ = SymbolTable();

  // classVarDec*
  while (tokenizer_.getToken() == "static" ||
         tokenizer_.getToken() == "field") {
    compileClassVarDec();
  }
  // subroutineDec*
  while (tokenizer_.getToken() == "constructor" ||
         tokenizer_.getToken() == "function" ||
         tokenizer_.getToken() == "method") {
    compileSubroutine();
  }
  expect("}");
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileClassVarDec() {
  // ('static'|'field') type varName (',' varName)* ';'
  if (xml_debug_)
    px_->openTag("classVarDec");
  std::string kind = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance(); // static | field
  std::string type = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance(); // type
  std::string name = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance();
  symbols_.define(name, type, kind);
  while (accept(",")) {
    name = tokenizer_.getToken();
    emitCurrentToken();
    tokenizer_.advance();
    symbols_.define(name, type, kind);
  }
  expect(";");
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileSubroutine() {
  // ('constructor'|'function'|'method') ('void'|type) subroutineName '('
  // parameterList ')' subroutineBody
  if (xml_debug_)
    px_->openTag("subroutineDec");
  std::string subKind = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance(); // constructor|function|method
  // return type
  compileType(); // consume return type (or 'void')

  std::string subName = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance();
  expect("(");

  symbols_.startSubroutine();
  if (subKind == "method") {
    // arg0 is this
    symbols_.define("this", className_, "arg");
  }
  compileParameterList(subKind == "method");
  expect(")");

  // subroutineBody: '{' varDec* statements '}'
  expect("{");
  if (xml_debug_)
    px_->openTag("subroutineBody");
  // varDec*
  while (tokenizer_.getToken() == "var") {
    compileVarDec();
  }
  // function label
  int nLocals = symbols_.varCount("var");
  vm_.writeFunction(className_ + "." + subName, nLocals);

  if (subKind == "constructor") {
    int nFields = symbols_.varCount("field");
    vm_.writePush("constant", nFields);
    vm_.writeCall("Memory.alloc", 1);
    vm_.writePop("pointer", 0); // this = base
  } else if (subKind == "method") {
    // set THIS to argument 0
    vm_.writePush("argument", 0);
    vm_.writePop("pointer", 0);
  }

  compileStatements();
  expect("}");
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileParameterList(bool isMethod) {
  // ((type varName) (',' type varName)*)?
  if (xml_debug_)
    px_->openTag("parameterList");
  if (tokenizer_.getToken() != ")") {
    std::string type = tokenizer_.getToken();
    emitCurrentToken();
    tokenizer_.advance();
    std::string name = tokenizer_.getToken();
    emitCurrentToken();
    tokenizer_.advance();
    symbols_.define(name, type, "arg");
    while (accept(",")) {
      type = tokenizer_.getToken();
      emitCurrentToken();
      tokenizer_.advance();
      name = tokenizer_.getToken();
      emitCurrentToken();
      tokenizer_.advance();
      symbols_.define(name, type, "arg");
    }
  }
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileVarDec() {
  // 'var' type varName (',' varName)* ';'
  if (xml_debug_)
    px_->openTag("varDec");
  expect("var");
  std::string type = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance();
  std::string name = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance();
  symbols_.define(name, type, "var");
  while (accept(",")) {
    name = tokenizer_.getToken();
    emitCurrentToken();
    tokenizer_.advance();
    symbols_.define(name, type, "var");
  }
  expect(";");
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileStatements() {
  if (xml_debug_)
    px_->openTag("statements");
  while (true) {
    const std::string &t = tokenizer_.getToken();
    if (t == "let")
      compileLet();
    else if (t == "if")
      compileIf();
    else if (t == "while")
      compileWhile();
    else if (t == "do")
      compileDo();
    else if (t == "return")
      compileReturn();
    else
      break;
  }
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileLet() {
  // 'let' varName ('[' expression ']')? '=' expression ';'
  if (xml_debug_)
    px_->openTag("letStatement");
  expect("let");
  std::string varName = tokenizer_.getToken();
  emitCurrentToken();
  tokenizer_.advance();
  bool isArray = false;
  if (accept("[")) {
    isArray = true;
    vm_.writePush(kindToSegment(symbols_.kindOf(varName)),
                  symbols_.indexOf(varName));
    compileExpression();
    expect("]");
    vm_.writeArithmetic("add"); // base + index
  }
  expect("=");
  compileExpression();
  expect(";");

  if (isArray) {
    // *that = value  where pointer 1 = base+index
    vm_.writePop("temp", 0);
    vm_.writePop("pointer", 1);
    vm_.writePush("temp", 0);
    vm_.writePop("that", 0);
  } else {
    vm_.writePop(kindToSegment(symbols_.kindOf(varName)),
                 symbols_.indexOf(varName));
  }
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileIf() {
  // 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}')?
  if (xml_debug_)
    px_->openTag("ifStatement");
  expect("if");
  expect("(");
  compileExpression();
  expect(")");
  int idx = labelCounter_++;
  vm_.writeIf("IF_TRUE_" + std::to_string(idx));
  vm_.writeGoto("IF_FALSE_" + std::to_string(idx));
  vm_.writeLabel("IF_TRUE_" + std::to_string(idx));
  expect("{");
  compileStatements();
  expect("}");
  if (accept("else")) {
    vm_.writeGoto("IF_END_" + std::to_string(idx));
    vm_.writeLabel("IF_FALSE_" + std::to_string(idx));
    expect("{");
    compileStatements();
    expect("}");
    vm_.writeLabel("IF_END_" + std::to_string(idx));
  } else {
    vm_.writeLabel("IF_FALSE_" + std::to_string(idx));
  }
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileWhile() {
  // 'while' '(' expression ')' '{' statements '}'
  if (xml_debug_)
    px_->openTag("whileStatement");
  expect("while");
  int idx = labelCounter_++;
  vm_.writeLabel("WHILE_EXP_" + std::to_string(idx));
  expect("(");
  compileExpression();
  expect(")");
  vm_.writeArithmetic("not");
  vm_.writeIf("WHILE_END_" + std::to_string(idx));
  expect("{");
  compileStatements();
  expect("}");
  vm_.writeGoto("WHILE_EXP_" + std::to_string(idx));
  vm_.writeLabel("WHILE_END_" + std::to_string(idx));
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileDo() {
  // 'do' subroutineCall ';'  (discard return)
  if (xml_debug_)
    px_->openTag("doStatement");
  expect("do");
  // subroutineCall:
  //  (className|varName '.'){opt} subroutineName '(' expressionList ')'
  std::string name = tokenizer_.getToken();
  tokenizer_.advance();
  int nArgs = 0;
  if (accept(".")) {
    std::string afterDot = tokenizer_.getToken();
    tokenizer_.advance();
    // is name a var? then it's method call on object
    std::string k = symbols_.kindOf(name);
    if (k != "NONE") {
      vm_.writePush(kindToSegment(k), symbols_.indexOf(name));
      name = symbols_.typeOf(name) + "." + afterDot;
      nArgs = 1;
    } else {
      name = name + "." + afterDot;
    }
  } else {
    // method on this
    vm_.writePush("pointer", 0);
    name = className_ + "." + name;
    nArgs = 1;
  }
  expect("(");
  nArgs += compileExpressionList();
  expect(")");
  vm_.writeCall(name, nArgs);
  expect(";");
  vm_.writePop("temp", 0); // discard
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileReturn() {
  // 'return' expression? ';'
  if (xml_debug_)
    px_->openTag("returnStatement");
  expect("return");
  if (tokenizer_.getToken() != ";") {
    compileExpression();
  } else {
    vm_.writePush("constant", 0); // void -> 0
  }
  expect(";");
  vm_.writeReturn();
  if (xml_debug_)
    px_->closeTag();
}

void CompilationEngine::compileExpression() {
  // term (op term)*
  if (xml_debug_)
    px_->openTag("expression");
  compileTerm();
  while (kOps.count(tokenizer_.getToken())) {
    std::string op = tokenizer_.getToken();
    tokenizer_.advance();
    compileTerm();
    if (op == "+")
      vm_.writeArithmetic("add");
    else if (op == "-")
      vm_.writeArithmetic("sub");
    else if (op == "*")
      vm_.writeCall("Math.multiply", 2);
    else if (op == "/")
      vm_.writeCall("Math.divide", 2);
    else if (op == "&")
      vm_.writeArithmetic("and");
    else if (op == "|")
      vm_.writeArithmetic("or");
    else if (op == "<")
      vm_.writeArithmetic("lt");
    else if (op == ">")
      vm_.writeArithmetic("gt");
    else if (op == "=")
      vm_.writeArithmetic("eq");
  }
  if (xml_debug_)
    px_->closeTag();
}
void CompilationEngine::compileTerm() {
  // term:
  // integerConstant | stringConstant | keywordConstant |
  // varName | varName '[' expression ']' | subroutineCall |
  // '(' expression ')' | unaryOp term
  if (xml_debug_) px_->openTag("term");

  const std::string ttype = tokenizer_.tokenType();

  // --- integerConstant ---
  if (ttype == "INT_CONST") {
    vm_.writePush("constant", std::stoi(tokenizer_.getToken()));
    emitCurrentToken();           // integerConstant
    tokenizer_.advance();
    if (xml_debug_) px_->closeTag();
    return;
  }

  // --- stringConstant ---
  if (ttype == "STRING_CONST") {
    const std::string s = tokenizer_.stringVal();
    vm_.writePush("constant", (int)s.size());
    vm_.writeCall("String.new", 1);
    for (unsigned char c : s) {
      vm_.writePush("constant", (int)c);
      vm_.writeCall("String.appendChar", 2);
    }
    emitCurrentToken();           // stringConstant
    tokenizer_.advance();
    if (xml_debug_) px_->closeTag();
    return;
  }

  // --- keywordConstant: true/false/null/this ---
  if (ttype == "KEYWORD" && kKeywordConst.count(tokenizer_.getToken())) {
    const std::string kw = tokenizer_.getToken();
    if (kw == "true") {
      vm_.writePush("constant", 1);
      vm_.writeArithmetic("neg");
    } else if (kw == "false" || kw == "null") {
      vm_.writePush("constant", 0);
    } else if (kw == "this") {
      vm_.writePush("pointer", 0);
    }
    emitCurrentToken();           // keyword
    tokenizer_.advance();
    if (xml_debug_) px_->closeTag();
    return;
  }

  // --- '(' expression ')' ---
  if (tokenizer_.getToken() == "(") {
    expect("(");                  // 会把 '(' 也写到 XML
    compileExpression();
    expect(")");
    if (xml_debug_) px_->closeTag();
    return;
  }

  // --- unaryOp term ---
  if (kUnary.count(tokenizer_.getToken())) {
    const std::string op = tokenizer_.getToken();
    emitCurrentToken();           // '-' 或 '~'
    tokenizer_.advance();
    compileTerm();
    if (op == "-") vm_.writeArithmetic("neg");
    else           vm_.writeArithmetic("not");
    if (xml_debug_) px_->closeTag();
    return;
  }

  // --- IDENTIFIER 开头的三种情况 ---
  if (ttype == "IDENTIFIER") {
    // 先拿下名字（不决定分支，不吃额外 token）
    const std::string name = tokenizer_.getToken();
    emitCurrentToken();           // identifier (varName / className / subroutineName)
    tokenizer_.advance();         // 现在 current 指向 name 后面的那个 token

    // 1) varName '[' expression ']'
    if (tokenizer_.getToken() == "[") {
      // 数组：base + index -> that 访问
      expect("[");
      vm_.writePush(kindToSegment(symbols_.kindOf(name)), symbols_.indexOf(name));
      compileExpression();
      expect("]");
      vm_.writeArithmetic("add");
      vm_.writePop("pointer", 1);
      vm_.writePush("that", 0);
      if (xml_debug_) px_->closeTag();
      return;
    }

    // 2) subroutineCall: (className|varName) '.' subroutineName '(' expressionList ')'
    if (tokenizer_.getToken() == ".") {
      expect(".");                // '.'
      // subroutineName
      const std::string afterDot = tokenizer_.getToken();
      emitCurrentToken();         // identifier (subroutineName)
      tokenizer_.advance();

      int nArgs = 0;
      std::string callName;
      const std::string k = symbols_.kindOf(name);
      if (k != "NONE") {
        // varName.method(...) -> 推入对象作为 this
        vm_.writePush(kindToSegment(k), symbols_.indexOf(name));
        callName = symbols_.typeOf(name) + "." + afterDot;
        nArgs = 1;
      } else {
        // ClassName.function(...)
        callName = name + "." + afterDot;
      }

      expect("(");
      nArgs += compileExpressionList();
      expect(")");
      vm_.writeCall(callName, nArgs);
      if (xml_debug_) px_->closeTag();
      return;
    }

    // 3) subroutineCall: subroutineName '(' expressionList ')'   —— 当前类的方法调用
    if (tokenizer_.getToken() == "(") {
      expect("(");
      vm_.writePush("pointer", 0);    // 隐式 this
      int nArgs = 1 + compileExpressionList();
      expect(")");
      vm_.writeCall(className_ + "." + name, nArgs);
      if (xml_debug_) px_->closeTag();
      return;
    }

    // 4) plain varName
    vm_.writePush(kindToSegment(symbols_.kindOf(name)), symbols_.indexOf(name));
    if (xml_debug_) px_->closeTag();
    return;
  }

  // 其它情况都是错误
  throw std::runtime_error("Invalid term at token: " + tokenizer_.getToken());
}

int CompilationEngine::compileExpressionList() {
  // (expression (',' expression)*)?
  int n = 0;
  if (tokenizer_.getToken() != ")") {
    compileExpression();
    n++;
    while (accept(",")) {
      compileExpression();
      n++;
    }
  }
  return n;
}
