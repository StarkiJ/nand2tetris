import java.util.ArrayDeque;
import java.util.Map;
import java.util.Scanner;
import java.io.*;
import java.util.Set;

// 从输入流中删除所有的注释和空格，
// 并根据Jack语法的规则将输入流分解成Jack语言的字元（终结符）
public class JackTokenizer {
    private Scanner scanner;
    private String currentLine = "";// 按空格分组的当前行
    private int lineIndex = 0;
    private String currentToken;
    // private String currentTokenType;
    private ArrayDeque<String> tokenQueue = new ArrayDeque<>();

    // 定义0为正常，1为双引号，2为注释
    private static final int NORMAL = 0;  // 正常状态
    private static final int IN_QUOTE = 1;  // 双引号内
    private static final int IN_COMMENT = 2;  // 注释内
    private int state = NORMAL;

    private static final Set<String> keywords = Set.of(
            "class", "constructor", "function", "method", "field", "static", "var",
            "int", "char", "boolean", "void", "true", "false", "null", "this",
            "let", "do", "if", "else", "while", "return");
    private static final Set<String> symbols = Set.of(
            "{", "}", "(", ")", "[", "]", ".", ",", ";",
            "+", "-", "*", "/", "&", "|", "<", ">", "=", "~");
    private static final Map<String, String> replaceSymbols = Map.of(
            "<", "&lt;",
            ">", "&gt;",
            "&", "&amp;");

    // 构造函数：打开输入文件/输入流，准备进行字符转换操作
    public JackTokenizer(String input) {
        try {
            scanner = new Scanner(new File(input));
            while (hasMoreTokens()) {
                advancePre();
                tokenQueue.offer(currentToken);
            }
            //tokenizeFile(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 输入中是否还有字元？
    private boolean hasMoreTokens() {
        /*todo */
    }

    // 提前查看下一个字元
    public String peekNextToken() {
        /*todo */
    }

    // 从输入中获取下一个字符，使其成为当前字元。
    // 该函数仅当hasMoreTokens()返回为真时才能调用。
    // 最初始状态是没有当前字元
    public void advance() {
        /*todo */
    }

    // 预处理
    private void advancePre() {
        /*todo */
    }

    // 返回当前字元的类型
    public String tokenType() {
        /*todo */
    }

    // 返回当前字元的关键字。
    // 仅当tokenType()的返回值为KEYWORD时才能被调用
    public String keyword() {
        /*todo */
    }

    // 返回当前字元的字符。
    // 仅当tokenType()的返回值为SYMBOL时才能被调用
    public String symbol() {
        // 如果是符号，则替换掉特殊字符
        /*todo */
    }

    // 返回当前字元的标识符。
    // 仅当tokenType()的返回值为IDENTIFIER时才能被调用
    public String identifier() {
        /*todo */
    }

    // 返回当前字元的整数值。
    // 仅当tokenType()的返回值为INT_CONST时才能被调用
    public int intVal() {
        /*todo */
    }

    // 返回当前字元的字符串值。
    // 仅当tokenType()的返回值为STRING_CONST时才能被调用
    public String stringVal() {
        /*todo */
    }

    // 返回当前字元
    public String getToken() {
        /*todo */
    }

    // 从文件读取字符，并生成token
    private void tokenizeFile(String filePath) {
        int index = filePath.lastIndexOf("\\");
        String fileName = filePath.substring(0, index) + "\\output\\"
                + filePath.substring(index + 1, filePath.lastIndexOf("."));
        PrintWriter writerT;
        try {
            writerT = new PrintWriter(new BufferedWriter(new FileWriter(fileName + "T.xml")));
        } catch (Exception e) {
            System.err.println("Error creating output file: " + e.getMessage());
            return;
        }
        writerT.println("<tokens>");
        /*todo */
        writerT.println("</tokens>");
        try {
            writerT.close();
        } catch (Exception e) {
            System.err.println("Error closing output file: " + e.getMessage());
        }
        System.out.println("File tokenize Success: " + filePath);
    }
}
