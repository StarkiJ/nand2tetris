import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.StringTokenizer;

// 执行编译输出。从JackTokenizer中得到输入，然后将分析后的结果放入输出文件或输出流。
public class CompilationEngine {
    private static final Set<String> op = Set.of("&", "|", "<", ">", "=", "-", "+", "*", "/", "~");
    private static final Set<String> unaryOp = Set.of("-", "~");
    private static final Set<String> keywordConstant = Set.of("true", "false", "null", "this");
    private JackTokenizer tokenizer;
    private PrintWriter writer;
    private VMWriter vmWriter;
    private SymbolTable symbolTable;
    private String className;
    private String name, type, kind;
    private int index;
    private int nArgs, nLabel;

    // 构造函数：利用给定的输入和输出创建新的编译引擎，接下来必须调用compileClass()
    public CompilationEngine(JackTokenizer input, String output) {
        try {
            tokenizer = input;
            writer = new PrintWriter(new BufferedWriter(new FileWriter(output + ".xml")));
            vmWriter = new VMWriter(output + ".vm");
            nLabel = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 编译整个类
    // class: 'class' className '{' classVarDec* subroutineDec* '}'
    public void compileClass() {
        symbolTable = new SymbolTable();
        writer.println("<class>");
        tokenizer.advance();
        // 'class'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // className
        className = tokenizer.identifier();
        writer.println("<identifier> " + className + " </identifier>");
        tokenizer.advance();
        // '{'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // classVarDec*
        while (tokenizer.getToken().equals("static") || tokenizer.getToken().equals("field")) {
            compileClassVarDec();
        }
        // subroutineDec*
        while (tokenizer.getToken().equals("constructor") || tokenizer.getToken().equals("function")
                || tokenizer.getToken().equals("method")) {
            compileSubroutine();
        }
        // '}'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</class>");
        writer.close();
        vmWriter.close();
    }

    // 编译静态声明或字段声明
    // classVarDec: ('static'|'field') type varName (',' varName)* ';'
    public void compileClassVarDec() {
        writer.println("<classVarDec>");
        // 'static'|'field'
        kind = tokenizer.keyword();
        writer.println("<keyword> " + kind + " </keyword>");
        tokenizer.advance();
        // type
        compileType();
        // varName
        name = tokenizer.identifier();
        symbolTable.define(name, type, kind);
        writer.println("<identifier> " + name + " </identifier>");
        tokenizer.advance();
        // (',' varName)*
        while (tokenizer.getToken().equals(",")) {
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            name = tokenizer.identifier();
            symbolTable.define(name, type, kind);
            writer.println("<identifier> " + name + " </identifier>");
            tokenizer.advance();
        }
        // ';'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</classVarDec>");
    }

    // 编译type
    // type: 'int'|'char'|'boolean'|className
    public void compileType() {
        type = tokenizer.getToken();
        if (tokenizer.tokenType().equals("KEYWORD")) {
            // 'int'|'char'|'boolean'
            writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        } else {
            // className
            writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
        }
        tokenizer.advance();
    }

    // 编译整个方法、函数或构造函数
    // subroutineDec: ('constructor'|'function'|'method') ('void'|type) subroutineName '(' parameterList ')' subroutineBody
    public void compileSubroutine() {
        symbolTable.startSubroutine();
        writer.println("<subroutineDec>");
        // 'constructor'|'function'|'method'
        kind = tokenizer.keyword();
        boolean isConstructor = kind.equals("constructor");
        boolean isMethod = kind.equals("method");
        writer.println("<keyword> " + kind + " </keyword>");
        tokenizer.advance();
        // void|type
        compileType();
        // subroutineName
        String subName = tokenizer.identifier();
        writer.println("<identifier> " + subName + " </identifier>");
        tokenizer.advance();
        // '('
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // parameterList
        if (isMethod) {
            symbolTable.define("this", className, "arg");
        }
        compileParameterList();
        // ')'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();

        // subroutineBody: '{' varDec* statements '}'
        writer.println("<subroutineBody>");
        // '{'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // varDec*
        while (tokenizer.getToken().equals("var")) {
            compileVarDec();
        }
        vmWriter.writeFunction(className + "." + subName, symbolTable.varCount("var"));
        if (isConstructor) {
            vmWriter.writePush("constant", symbolTable.varCount("field"));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
        }
        if (isMethod) {
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);
        }
        // statements
        compileStatements();
        // '}'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</subroutineBody>");

        writer.println("</subroutineDec>");
    }

    // 编译参数列表（可能为空），不包含括号“()”
    // parameterList: (type varName (',' type varName)*)?
    public void compileParameterList() {
        writer.println("<parameterList>");
        if (tokenizer.tokenType().equals("KEYWORD") || tokenizer.tokenType().equals("IDENTIFIER")) {
            // type
            compileType();
            // varName
            name = tokenizer.identifier();
            symbolTable.define(name, type, "arg");
            writer.println("<identifier> " + name + " </identifier>");
            tokenizer.advance();
            // (',' type varName)*
            while (tokenizer.getToken().equals(",")) {
                writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                tokenizer.advance();
                compileType();
                name = tokenizer.identifier();
                symbolTable.define(name, type, "arg");
                writer.println("<identifier> " + name + " </identifier>");
                tokenizer.advance();
            }
        }
        writer.println("</parameterList>");
    }

    // 编译Var声明
    // varDec: 'var' type varName (',' varName)* ';'
    public void compileVarDec() {
        writer.println("<varDec>");
        // 'var'
        kind = tokenizer.keyword();
        writer.println("<keyword> " + kind + " </keyword>");
        tokenizer.advance();
        // type
        compileType();
        // varName
        name = tokenizer.identifier();
        symbolTable.define(name, type, kind);
        writer.println("<identifier> " + name + " </identifier>");
        tokenizer.advance();
        // (',' varName)*
        while (tokenizer.getToken().equals(",")) {
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            name = tokenizer.identifier();
            symbolTable.define(name, type, kind);
            writer.println("<identifier> " + name + " </identifier>");
            tokenizer.advance();
        }
        // ';'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</varDec>");
    }

    // 编译一系列语句，不包含大括号“{}”
    // statements: statement*
    // statement: letStatement|ifStatement|whileStatement|doStatement|returnStatement
    public void compileStatements() {
        writer.println("<statements>");
        while (tokenizer.getToken().equals("let") || tokenizer.getToken().equals("if") || tokenizer.getToken().equals("while")
                || tokenizer.getToken().equals("do") || tokenizer.getToken().equals("return")) {
            switch (tokenizer.getToken()) {
                case "let":
                    compileLet();
                    break;
                case "if":
                    compileIf();
                    break;
                case "while":
                    compileWhile();
                    break;
                case "do":
                    compileDo();
                    break;
                case "return":
                    compileReturn();
                    break;
            }
        }
        writer.println("</statements>");
    }

    // 编译do语句
    // doStatement: 'do' subroutineCall ';'
    public void compileDo() {
        writer.println("<doStatement>");
        // 'do'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // subroutineCall
        compileSubroutineCall();
        vmWriter.writePop("temp", 0);
        // ';'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</doStatement>");
    }

    //编译subroutineCall
    // subroutineCall: subroutineName '(' expressionList ')'|(className|varName) '.' subroutineName '(' expressionList ')'
    public void compileSubroutineCall() {
        String subName = tokenizer.identifier();
        // subroutineName | (className | varName)
        writer.println("<identifier> " + subName + " </identifier>");
        tokenizer.advance();
        if (tokenizer.getToken().equals(".")) {
            // '.'
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            // subroutineName
            name = subName;
            subName = tokenizer.identifier();
            type = symbolTable.typeOf(name);
            kind = symbolTable.kindOf(name);
            index = symbolTable.indexOf(name);
            // 判断是方法调用还是函数调用
            if (index >= 0) {// 方法调用
                name = type;
                vmWriter.writePush(kind, index);
                nArgs++;
            }
            subName = name + "." + subName;
            writer.println("<identifier> " + name + " </identifier>");
            tokenizer.advance();
        } else {
            subName = className + "." + subName;
            vmWriter.writePush("pointer", 0);
            nArgs++;
        }
        // '('
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // expressionList
        compileExpressionList();
        // ')'
        vmWriter.writeCall(subName, nArgs);
        nArgs = 0;
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
    }

    // 编译let语句
    // letStatement: 'let' varName ('[' expression ']')? '=' expression ';'
    public void compileLet() {
        writer.println("<letStatement>");
        // 'let'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // varName
        String varName = tokenizer.identifier();
        boolean isArray = false;
        writer.println("<identifier> " + varName + " </identifier>");
        tokenizer.advance();
        if (tokenizer.getToken().equals("[")) {
            // '['
            isArray = true;
            vmWriter.writePush(symbolTable.kindOf(varName), symbolTable.indexOf(varName));
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            // expression
            compileExpression();
            // ']'
            // 获取数组
            vmWriter.writeArithmetic("add");
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
        }
        // '='
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // expression
        compileExpression();
        // ';'
        if (isArray) {
            // 将expression的值赋给数组
            vmWriter.writePop("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePush("that", 0);
            vmWriter.writePop("that", 0);
        } else {
            // 将expression的值赋给变量
            vmWriter.writePop(symbolTable.kindOf(varName), symbolTable.indexOf(varName));
        }
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</letStatement>");
    }

    // 编译while语句
    // whileStatement: 'while' '(' expression ')' '{' statements '}'
    public void compileWhile() {
        writer.println("<whileStatement>");
        // 'while'
        int tmpIndex = nLabel++;
        vmWriter.writeLabel("WHILE_START_" + tmpIndex);
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // '('
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // expression
        compileExpression();
        // ')'
        vmWriter.writeArithmetic("not");
        vmWriter.writeIf("WHILE_END_" + tmpIndex);
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // '{'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // statements
        compileStatements();
        // '}'
        vmWriter.writeGoto("WHILE_START_" + tmpIndex);
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        vmWriter.writeLabel("WHILE_END_" + tmpIndex);
        writer.println("</whileStatement>");
    }

    // 编译return语句
    // returnStatement: 'return' expression? ';'
    public void compileReturn() {
        writer.println("<returnStatement>");
        // 'return'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        if (!tokenizer.getToken().equals(";")) {
            // expression
            compileExpression();
        } else {
            vmWriter.writePush("constant", 0);
        }
        // ';'
        vmWriter.writeReturn();
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</returnStatement>");
    }

    // 编译if语句，包含可选的else从句
    // ifStatement: 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}')?
    public void compileIf() {
        writer.println("<ifStatement>");
        // 'if'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // '('
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // expression
        compileExpression();
        // ')'
        vmWriter.writeArithmetic("not");
        int tmpIndex = nLabel++;
        vmWriter.writeIf("IF_ELSE_" + tmpIndex);
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // '{'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // statements
        compileStatements();
        // '}'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        if (tokenizer.getToken().equals("else")) {
            // 'else'
            vmWriter.writeGoto("IF_END_" + tmpIndex);
            vmWriter.writeLabel("IF_ELSE_" + tmpIndex);
            writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
            tokenizer.advance();
            // '{'
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            // statements
            compileStatements();
            // '}'
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            vmWriter.writeLabel("IF_END_" + tmpIndex);
        } else {
            vmWriter.writeLabel("IF_ELSE_" + tmpIndex);
        }
        writer.println("</ifStatement>");
    }

    // 编译一个表达式
    // expression: term (op term)*
    public void compileExpression() {
        writer.println("<expression>");
        // term
        compileTerm();
        while (op.contains(tokenizer.getToken())) {
            String s = tokenizer.getToken();
            // op: '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '='
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            // term
            compileTerm();
            switch (s) {
                case "+":
                    vmWriter.writeArithmetic("add");
                    break;
                case "-":
                    vmWriter.writeArithmetic("sub");
                    break;
                case "*":
                    vmWriter.writeCall("Math.multiply", 2);
                    break;
                case "/":
                    vmWriter.writeCall("Math.divide", 2);
                    break;
                case "&":
                    vmWriter.writeArithmetic("and");
                    break;
                case "|":
                    vmWriter.writeArithmetic("or");
                    break;
                case "<":
                    vmWriter.writeArithmetic("lt");
                    break;
                case ">":
                    vmWriter.writeArithmetic("gt");
                    break;
                case "=":
                    vmWriter.writeArithmetic("eq");
                    break;
            }
        }
        writer.println("</expression>");
    }

    // 编译一个“term”。
    // 本程序在“从多种可能的分析规则中作出决策”的时候会遇到一点难度。
    // 特别是，如果当前字元为标识符，那么本程序就必须要区分变量、数组、子程序调用这三种情况。
    // 通过提前查看下一个字元（可以为“[”、“(”或“.”），就可以区分这三种可能性了。
    // 后续任何其他字元都不属于这个term，故不须要取用
    // term: integerConstant | stringConstant | keywordConstant | varName |
    //       varName '[' expression ']' | '(' expression ')' | (unaryOp term) | subroutineCall
    public void compileTerm() {
        writer.println("<term>");
        switch (tokenizer.tokenType()) {
            // integerConstant | stringConstant | keywordConstant
            case "INT_CONST":
                int num = tokenizer.intVal();
                vmWriter.writePush("constant", num);
                writer.println("<integerConstant> " + num + " </integerConstant>");
                tokenizer.advance();
                break;
            case "STRING_CONST":
                String str = tokenizer.stringVal();
                int len = str.length();
                vmWriter.writePush("constant", len);
                vmWriter.writePush("String.new", 1);
                for (int i = 0; i < len; i++) {
                    int ascii = str.charAt(i);
                    vmWriter.writePush("constant", ascii);
                    vmWriter.writeCall("String.appendChar", 2);
                }
                writer.println("<stringConstant> " + str + " </stringConstant>");
                tokenizer.advance();
                break;
            case "KEYWORD":
                switch (tokenizer.keyword()) {
                    case "true":
                        vmWriter.writePush("constant", 0);
                        vmWriter.writeArithmetic("not");
                        break;
                    case "false":
                    case "null":
                        vmWriter.writePush("constant", 0);
                        break;
                    case "this":
                        vmWriter.writePush("pointer", 0);
                        break;
                }
                writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
                tokenizer.advance();
                break;
            // varName | varName '[' expression ']' | subroutineCall
            case "IDENTIFIER":
                String tmpName = tokenizer.identifier();
                switch (tokenizer.peekNextToken()) {
                    // varName '[' expression ']'
                    case "[":
                        // varName
                        vmWriter.writePush(symbolTable.kindOf(tmpName), symbolTable.indexOf(tmpName));// push varname
                        writer.println("<identifier> " + tmpName + " </identifier>");
                        tokenizer.advance();
                        // '['
                        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                        tokenizer.advance();
                        // expression
                        compileExpression();
                        // ']'
                        vmWriter.writeArithmetic("add");
                        vmWriter.writePop("pointer", 1);
                        vmWriter.writePush("that", 0);
                        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                        tokenizer.advance();
                        break;
                    // subroutineCall
                    case "(", ".":
                        compileSubroutineCall();
                        break;
                    // varName
                    default:
                        vmWriter.writePush(symbolTable.kindOf(tmpName), symbolTable.indexOf(tmpName));
                        writer.println("<identifier> " + tmpName + " </identifier>");
                        tokenizer.advance();
                        break;
                }
                break;
            // '(' expression ')' | (unaryOp term)
            case "SYMBOL":
                if (tokenizer.getToken().equals("(")) {
                    // '('
                    writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                    tokenizer.advance();
                    // expression
                    compileExpression();
                    // ')'
                    writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                    tokenizer.advance();
                } else if (unaryOp.contains(tokenizer.getToken())) {
                    // unaryOp: '-' | '~'
                    String uOp = tokenizer.symbol();
                    writer.println("<symbol> " + uOp + " </symbol>");
                    tokenizer.advance();
                    // term
                    compileTerm();
                    if (uOp.equals("-")) {
                        vmWriter.writeArithmetic("neg");
                    } else if (uOp.equals("~")) {
                        vmWriter.writeArithmetic("not");
                    }
                }
                break;
            default:
                System.err.println("Invalid term type: " + tokenizer.tokenType());
                break;
        }
        writer.println("</term>");
    }

    // 编译一个用逗号分隔的表达式列表（可能为空）
    // expressionList: (expression (',' expression)* )?
    public void compileExpressionList() {
        writer.println("<expressionList>");
        if (!tokenizer.getToken().equals(")")) {
            // expression
            nArgs++;
            compileExpression();
            while (tokenizer.getToken().equals(",")) {
                // ','
                writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                tokenizer.advance();
                // expression
                nArgs++;
                compileExpression();
            }
        }
        writer.println("</expressionList>");
    }
}
