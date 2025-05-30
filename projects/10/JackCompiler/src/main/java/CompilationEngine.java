import java.io.PrintWriter;
import java.util.Set;

// 执行编译输出。从JackTokenizer中得到输入，然后将分析后的结果放入输出文件或输出流。
public class CompilationEngine {
    private static final Set<String> op = Set.of("&", "|", "<", ">", "=", "-", "+", "*", "/", "~");
    private static final Set<String> unaryOp = Set.of("-", "~");
    private static final Set<String> keywordConstant = Set.of("true", "false", "null", "this");
    private JackTokenizer tokenizer;
    private PrintWriter writer;

    // 构造函数：利用给定的输入和输出创建新的编译引擎，接下来必须调用compileClass()
    public CompilationEngine(JackTokenizer input, PrintWriter output) {
        try {
            tokenizer = input;
            writer = output;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 编译整个类
    // class: 'class' className '{' classVarDec* subroutineDec* '}'
    public void compileClass() {
        writer.println("<class>");
        tokenizer.advance();
        // 'class'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // className
        writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
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
    }

    // 编译静态声明或字段声明
    // classVarDec: ('static'|'field') type varName (',' varName)* ';'
    public void compileClassVarDec() {
        writer.println("<classVarDec>");
        // 'static'|'field'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // type
        compileType();
        // varName
        writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
        tokenizer.advance();
        // (',' varName)*
        while (tokenizer.getToken().equals(",")) {
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
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
        writer.println("<subroutineDec>");
        // 'constructor'|'function'|'method'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // void|type
        compileType();
        // subroutineName
        writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
        tokenizer.advance();
        // '('
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // parameterList
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
            writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
            tokenizer.advance();
            // (',' type varName)*
            while (tokenizer.getToken().equals(",")) {
                writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                tokenizer.advance();
                compileType();
                writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
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
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // type
        compileType();
        // varName
        writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
        tokenizer.advance();
        // (',' varName)*
        while (tokenizer.getToken().equals(",")) {
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
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
        // ';'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</doStatement>");
    }

    //编译subroutineCall
    // subroutineCall: subroutineName '(' expressionList ')'|(className|varName) '.' subroutineName '(' expressionList ')'
    public void compileSubroutineCall() {
        // subroutineName | (className | varName)
        writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
        tokenizer.advance();
        if (tokenizer.getToken().equals(".")) {
            // '.'
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            // subroutineName
            writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
            tokenizer.advance();
        }
        // '('
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // expressionList
        compileExpressionList();
        // ')'
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
        writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
        tokenizer.advance();
        if (tokenizer.getToken().equals("[")) {
            // '['
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            // expression
            compileExpression();
            // ']'
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
        }
        // '='
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // expression
        compileExpression();
        // ';'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        writer.println("</letStatement>");
    }

    // 编译while语句
    // whileStatement: 'while' '(' expression ')' '{' statements '}'
    public void compileWhile() {
        writer.println("<whileStatement>");
        // 'while'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();
        // '('
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // expression
        compileExpression();
        // ')'
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
        }
        // ';'
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
            // op: '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '='
            writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            // term
            compileTerm();
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
                writer.println("<integerConstant> " + tokenizer.intVal() + " </integerConstant>");
                tokenizer.advance();
                break;
            case "STRING_CONST":
                writer.println("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>");
                tokenizer.advance();
                break;
            case "KEYWORD":
                writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
                tokenizer.advance();
                break;
            // varName | varName '[' expression ']' | subroutineCall
            case "IDENTIFIER":
                switch (tokenizer.peekNextToken()) {
                    // varName '[' expression ']'
                    case "[":
                        // varName
                        writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
                        tokenizer.advance();
                        // '['
                        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                        tokenizer.advance();
                        // expression
                        compileExpression();
                        // ']'
                        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                        tokenizer.advance();
                        break;
                    // subroutineCall
                    case "(", ".":
                        compileSubroutineCall();
                        break;
                    // varName
                    default:
                        writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
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
                    writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                    tokenizer.advance();
                    // term
                    compileTerm();
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
            compileExpression();
            while (tokenizer.getToken().equals(",")) {
                // ','
                writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                tokenizer.advance();
                // expression
                compileExpression();
            }
        }
        writer.println("</expressionList>");
    }
}
