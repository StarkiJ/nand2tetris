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
        /*todo */
        writer.println("</class>");
        writer.close();
        vmWriter.close();
    }

    // 编译静态声明或字段声明
    // classVarDec: ('static'|'field') type varName (',' varName)* ';'
    public void compileClassVarDec() {
        writer.println("<classVarDec>");
        /*todo */
        writer.println("</classVarDec>");
    }

    // 编译type
    // type: 'int'|'char'|'boolean'|className
    public void compileType() {
        /*todo */
    }

    // 编译整个方法、函数或构造函数
    // subroutineDec: ('constructor'|'function'|'method') ('void'|type) subroutineName '(' parameterList ')' subroutineBody
    public void compileSubroutine() {
        symbolTable.startSubroutine();
        writer.println("<subroutineDec>");
        /*todo */

        writer.println("</subroutineDec>");
    }

    // 编译参数列表（可能为空），不包含括号“()”
    // parameterList: (type varName (',' type varName)*)?
    public void compileParameterList() {
        writer.println("<parameterList>");
        /*todo */
        writer.println("</parameterList>");
    }

    // 编译Var声明
    // varDec: 'var' type varName (',' varName)* ';'
    public void compileVarDec() {
        writer.println("<varDec>");
        /*todo */
        writer.println("</varDec>");
    }

    // 编译一系列语句，不包含大括号“{}”
    // statements: statement*
    // statement: letStatement|ifStatement|whileStatement|doStatement|returnStatement
    public void compileStatements() {
        writer.println("<statements>");
        /*todo */
        writer.println("</statements>");
    }

    // 编译do语句
    // doStatement: 'do' subroutineCall ';'
    public void compileDo() {
        writer.println("<doStatement>");
        /*todo */
        writer.println("</doStatement>");
    }

    //编译subroutineCall
    // subroutineCall: subroutineName '(' expressionList ')'|(className|varName) '.' subroutineName '(' expressionList ')'
    public void compileSubroutineCall() {
        /*todo */
    }

    // 编译let语句
    // letStatement: 'let' varName ('[' expression ']')? '=' expression ';'
    public void compileLet() {
        writer.println("<letStatement>");
        /*todo */
        writer.println("</letStatement>");
    }

    // 编译while语句
    // whileStatement: 'while' '(' expression ')' '{' statements '}'
    public void compileWhile() {
        writer.println("<whileStatement>");
        /*todo */
        writer.println("</whileStatement>");
    }

    // 编译return语句
    // returnStatement: 'return' expression? ';'
    public void compileReturn() {
        writer.println("<returnStatement>");
        /*todo */
        writer.println("</returnStatement>");
    }

    // 编译if语句，包含可选的else从句
    // ifStatement: 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}')?
    public void compileIf() {
        writer.println("<ifStatement>");
        /*todo */
        writer.println("</ifStatement>");
    }

    // 编译一个表达式
    // expression: term (op term)*
    public void compileExpression() {
        writer.println("<expression>");
        /*todo */
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
        /*todo */
        writer.println("</term>");
    }

    // 编译一个用逗号分隔的表达式列表（可能为空）
    // expressionList: (expression (',' expression)* )?
    public void compileExpressionList() {
        writer.println("<expressionList>");
        /*todo */
        writer.println("</expressionList>");
    }
}
