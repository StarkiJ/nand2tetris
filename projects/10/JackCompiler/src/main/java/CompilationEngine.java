import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

// 执行编译输出。从JackTokenizer中得到输入，然后将分析后的结果放入输出文件或输出流。
public class CompilationEngine {
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
        while (tokenizer.tokenType().equals("keyword")
                && (tokenizer.keyword().equals("static") || tokenizer.keyword().equals("field"))) {
            compileClassVarDec();
        }
        // subroutineDec*
        while (tokenizer.tokenType().equals("keyword")
                && (tokenizer.keyword().equals("constructor") || tokenizer.keyword().equals("function")
                || tokenizer.keyword().equals("method"))) {
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
        while (tokenizer.symbol().equals(",")) {
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

    // 编译类型
    // type: 'int'|'char'|'boolean'|className
    public void compileType() {
        if (tokenizer.tokenType().equals("keyword")) {
            writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        } else {
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
        tokenizer.advance();
        // ')'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();

        // subroutineBody: '{' varDec* statements '}'
        writer.println("<subroutineBody>");
        // '{'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
        // varDec*
        while (tokenizer.tokenType().equals("keyword") && tokenizer.keyword().equals("var")) {
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
        if (tokenizer.tokenType().equals("keyword") || tokenizer.tokenType().equals("identifier")) {
            // type
            compileType();
            // varName
            writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
            tokenizer.advance();
            // (',' type varName)*
            while (tokenizer.symbol().equals(",")) {
                writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
                tokenizer.advance();
                compileType();
                writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
                tokenizer.advance();
            }
            writer.println("</parameterList>");
        }
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
        while (tokenizer.symbol().equals(",")) {
            writer.println("<symbol> " + tokenizer.symbol() + "</symbol>");
            tokenizer.advance();
            writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
            tokenizer.advance();
        }
        // ';'
        writer.println("<symbol> " + tokenizer.symbol() + " </symbol>");
        tokenizer.advance();
    }

    // 编译一系列语句，不包含大括号“{}”
    // statements: statement*
    // statement: letStatement|ifStatement|whileStatement|doStatement|returnStatement
    public void compileStatements() {
        writer.println("<statements>");
        while (tokenizer.tokenType().equals("keyword")
                && (tokenizer.keyword().equals("let") || tokenizer.keyword().equals("if")
                || tokenizer.keyword().equals("while") || tokenizer.keyword().equals("do")
                || tokenizer.keyword().equals("return"))) {
            switch (tokenizer.keyword()) {
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
            tokenizer.advance();
        }
    }

    // 编译do语句
    // doStatement: 'do' subroutineCall ';'
    public void compileDo() {
        writer.println("<doStatement>");
        // 'do'
        writer.println("<keyword> " + tokenizer.keyword() + " </keyword>");
        tokenizer.advance();

    }

    // 编译let语句
    public void compileLet() {
    }

    // 编译while语句
    public void compileWhile() {
    }

    // 编译return语句
    public void compileReturn() {
    }

    // 编译if语句，包含可选的else从句
    public void compileIf() {
    }

    // 编译一个表达式
    public void compileExpression() {
    }

    // 编译一个“term”。
    // 本程序在“从多种可能的分析规则中作出决策”的时候会遇到一点难度。
    // 特别是，如果当前字元为标识符，那么本程序就必须要区分变量、数组、子程序调用这三种情况。
    // 通过提前查看下一个字元（可以为“[”、“(”或“.”），就可以区分这三种可能性了。
    // 后续任何其他字元都不属于这个term，故不须要取用
    public void compileTerm() {
    }

    // 编译一个用逗号分隔的表达式列表（可能为空）
    public void compileExpressionList() {
    }
}
