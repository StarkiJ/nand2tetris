import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*;
import java.util.Set;

// 从输入流中删除所有的注释和空格，
// 并根据Jack语法的规则将输入流分解成Jack语言的字元（终结符）
public class JackTokenizer {
    private Scanner scanner;
    private String currentLine = "";// 按空格分组的当前行
    private int currentLineIndex = 0;
    private String currentToken;
    private String tokenType;
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 输入中是否还有字元？
    public boolean hasMoreTokens() {
        /*if (currentStrIndex < currentStr.length()) {//当前组还有字符
            return true;
        } else if (currentLineIndex < currentLine.length) {//当前行还有字符
            currentStr = currentLine[currentLineIndex++];
            currentStrIndex = 0;
            return hasMoreTokens();
        }*/
        if (currentLineIndex < currentLine.length()) {
            return true;
        } else if (scanner.hasNextLine()) {//当前文件还有字符
            currentLine = scanner.nextLine()
                    .replaceAll("//.*|/\\*.*|\\*.*|/\\*/.*", "")
                    //.replaceAll("//.*", "")
                    .trim()/*.split("\\s+")*/;
            currentLineIndex = 0;
            return hasMoreTokens();
        } else {
            scanner.close();
            return false;
        }
    }

    // 从输入中获取下一个字符，使其成为当前字元。
    // 该函数仅当hasMoreTokens()返回为真时才能调用。
    // 最初始状态是没有当前字元
    public void advance() {
        if(!hasMoreTokens())
        {
            return;
        }
        currentToken = "";
        tokenType = null;
        while (hasMoreTokens()) {
            String currentChar = "" + currentLine.charAt(currentLineIndex++);
            switch (state) {
                case NORMAL:
                    if (symbols.contains(currentChar)) {// 如果当前字符是符号
                        if (currentToken.isEmpty()) { // 之前没有字符，则将符号作为字元
                            tokenType = "SYMBOL";
                            currentToken = currentChar;
                        } else { // 之前有字符，则将之前字符作为字元，并回退一格
                            currentLineIndex--;
                            tokenType();
                        }
                        return;
                    } else if (currentChar.equals("\"")) {// 如果当前字符是双引号，则读取字符串
                        state = IN_QUOTE;
                        tokenType = "STRING_CONST";
                    } else if (currentChar.equals(" ")) {
                        if (currentToken.isEmpty()) {
                            advance();
                        }
                        tokenType();
                        return;
                    } else {
                        currentToken += currentChar;
                    }
                    break;
                case IN_QUOTE:
                    if (currentChar.equals("\"")) {
                        state = NORMAL;
                        return;
                    }
                    currentToken += currentChar;
                    break;
                default:
                    System.err.println("Invalid state: " + state);
                    return;
            }
        }//end while
    }

    // 返回当前字元的类型
    public String tokenType() {
        if (tokenType == null) {
            if (keywords.contains(currentToken)) {
                tokenType = "KEYWORD";
            } else if (symbols.contains(currentToken)) {
                tokenType = "SYMBOL";
            } else if (currentToken.startsWith("\"")) {
                tokenType = "STRING_CONST";
            } else if (currentToken.matches("[0-9]+")) {
                tokenType = "INT_CONST";
            } else {
                tokenType = "IDENTIFIER";
            }
        }
        return tokenType;
    }

    // 返回当前字元的关键字。
    // 仅当tokenType()的返回值为KEYWORD时才能被调用
    public String keyword() {
        return currentToken;
    }

    // 返回当前字元的字符。
    // 仅当tokenType()的返回值为SYMBOL时才能被调用
    public String symbol() {
        // 如果是符号，则替换掉特殊字符
        return replaceSymbols.getOrDefault(currentToken, currentToken);
    }

    // 返回当前字元的标识符。
    // 仅当tokenType()的返回值为IDENTIFIER时才能被调用
    public String identifier() {
        return currentToken;
    }

    // 返回当前字元的整数值。
    // 仅当tokenType()的返回值为INT_CONST时才能被调用
    public int intVal() {
        return Integer.parseInt(currentToken);
    }

    // 返回当前字元的字符串值。
    // 仅当tokenType()的返回值为STRING_CONST时才能被调用
    public String stringVal() {
        return currentToken;
    }
}
