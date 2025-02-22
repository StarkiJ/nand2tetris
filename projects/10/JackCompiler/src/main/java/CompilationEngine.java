public class CompilationEngine {
    // 构造函数：利用给定的输入和输出创建新的编译引擎，接下来必须调用compileClass()
    public CompilationEngine(String input, String output) {
    }

    // 编译整个类
    public void compileClass() {
    }

    // 编译静态声明或字段声明
    public void compileClassVarDec() {
    }

    // 编译整个方法、函数或构造函数
    public void compileSubroutine() {
    }

    // 编译参数列表（可能为空），不包含括号“()”
    public void compileParameterList() {
    }

    // 编译Var声明
    public void compileVarDec() {
    }

    // 编译一系列语句，不包含大括号“{}”
    public void compileStatements() {
    }

    // 编译do语句
    public void compileDo() {
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
