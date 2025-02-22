import java.util.HashSet;
import java.util.Scanner;
import java.io.*;
import java.util.Set;

// 从输入流中删除所有的注释和空格，
// 并根据Jack语法的规则将输入流分解成Jack语言的字元（终结符）
public class JackTokenizer {
    private Scanner scanner;
    private String[] currentLine;
    private int currentLineIndex;
    private String currentToken;
    private String tokenType;
    private static final Set<String> keywords = Set.of(
            "class", "constructor", "function", "method", "field", "static", "var",
            "int", "char", "boolean", "void", "true", "false", "null", "this",
            "let", "do", "if", "else", "while", "return");
    private static final Set<String> symbols = Set.of(
            "{", "}", "(", ")", "[", "]", ".", ",", ";",
            "+", "-", "*", "/", "&", "|", "<", ">", "=", "~");

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
        if (currentLineIndex < currentLine.length) {
            return true;
        } else if (scanner.hasNextLine()) {
            currentLine = scanner.nextLine()
                    .replaceAll("//.*|/\\*|\\*|/\\*/", "")
                    .trim().split("\\s+");
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
        currentToken = currentLine[currentLineIndex++];
    }

    // 返回当前字元的类型
    public String tokenType() {
//        return switch (currentToken) {
//            case "class", "constructor", "function", "method", "field", "static", "var",
//                 "int", "char", "boolean", "void", "true", "false", "null", "this",
//                 "let", "do", "if", "else", "while", "return" -> "KEYWORD";
//            case "{", "}", "(", ")", "[", "]", ".", ",", ";",
//                 "+", "-", "*", "/", "&", "|", "<", ">", "=", "~" -> "SYMBOL";
//            case "\"" -> "STRING_CONST";
//            case "[0-9]+" -> "INT_CONST";
//            default -> "IDENTIFIER";
//        };
        if (keywords.contains(currentToken)) {
            return "KEYWORD";
        } else if (symbols.contains(currentToken)) {
            return "SYMBOL";
        } else if (currentToken.startsWith("\"")) {
            return "STRING_CONST";
        } else if (currentToken.matches("[0-9]+")) {
            return "INT_CONST";
        } else {
            return "IDENTIFIER";
        }
    }

    // 返回当前字元的关键字。
    // 仅当tokenType()的返回值为KEYWORD时才能被调用
    public String keyword() {
        return null;
    }

    // 返回当前字元的字符。
    // 仅当tokenType()的返回值为SYMBOL时才能被调用
    public String symbol() {
        return null;
    }

    // 返回当前字元的标识符。
    // 仅当tokenType()的返回值为IDENTIFIER时才能被调用
    public String identifier() {
        return null;
    }

    // 返回当前字元的整数值。
    // 仅当tokenType()的返回值为INT_CONST时才能被调用
    public int intVal() {
        return 0;
    }

    // 返回当前字元的字符串值。
    // 仅当tokenType()的返回值为STRING_CONST时才能被调用
    public String stringVal() {
        return null;
    }
}
