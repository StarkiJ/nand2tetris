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
        if (lineIndex < currentLine.length()) {
            return true;
        } else if (scanner.hasNextLine()) {//当前文件还有字符
            currentLine = scanner.nextLine()
                    //.replaceAll("//.*|/\\*.*|\\*.*|/\\*/.*", "")
                    .replaceAll("//.*", "")
                    .trim()/*.split("\\s+")*/;
            lineIndex = 0;
            return hasMoreTokens();
        } else {
            // scanner.close();
            return false;
        }
    }

    // 提前查看下一个字元
    public String peekNextToken() {
        return tokenQueue.peek();
    }

    // 从输入中获取下一个字符，使其成为当前字元。
    // 该函数仅当hasMoreTokens()返回为真时才能调用。
    // 最初始状态是没有当前字元
    public void advance() {
        if (!tokenQueue.isEmpty()) {
            currentToken = tokenQueue.poll();
        }
    }
//    public void advance() {
//        if (tokenQueue.isEmpty() && hasMoreTokens()) {
//            advancePre();
//            tokenQueue.offer(currentToken);
//        }
//        currentToken = tokenQueue.poll();
//    }

    // 预处理
    private void advancePre() {
        currentToken = "";
        //currentTokenType = null;
        while (hasMoreTokens()) {
            String currentChar = "" + currentLine.charAt(lineIndex++);
            switch (state) {
                case NORMAL:
                    if (currentChar.equals("/") && hasMoreTokens() && currentLine.charAt(lineIndex) == '*') {
                        state = IN_COMMENT;
                    } else if (symbols.contains(currentChar)) {// 如果当前字符是符号
                        if (currentToken.isEmpty()) { // 之前没有字符，则将符号作为字元
                            //currentTokenType = "SYMBOL";
                            currentToken = currentChar;
                        } else { // 之前有字符，则将之前字符作为字元，并回退一格
                            lineIndex--;
                        }
                        return;
                    } else if (currentChar.equals("\"")) {// 如果当前字符是双引号，则读取字符串
                        state = IN_QUOTE;
                        currentToken = "\"";
                        //currentTokenType = "STRING_CONST";
                    } else if (currentChar.equals(" ")) {
                        if (currentToken.isEmpty()) {
                            advancePre();
                        }
                        return;
                    } else {
                        currentToken += currentChar;
                    }
                    break;
                case IN_QUOTE:
                    currentToken += currentChar;
                    if (currentChar.equals("\"")) {
                        state = NORMAL;
                        return;
                    }
                    break;
                case IN_COMMENT:
                    if (currentChar.equals("*") && hasMoreTokens() && currentLine.charAt(lineIndex) == '/') {
                        state = NORMAL;
                        lineIndex++;
                    }
                    break;
                default:
                    System.err.println("Invalid state: " + state);
                    return;
            }
        }//end while
    }

    // 返回当前字元的类型
    public String tokenType() {
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
//    public String tokenType() {
//        if (currentTokenType == null) {
//            if (keywords.contains(currentToken)) {
//                currentTokenType = "KEYWORD";
//            } else if (symbols.contains(currentToken)) {
//                currentTokenType = "SYMBOL";
//            } else if (currentToken.startsWith("\"")) {
//                currentTokenType = "STRING_CONST";
//            } else if (currentToken.matches("[0-9]+")) {
//                currentTokenType = "INT_CONST";
//            } else {
//                currentTokenType = "IDENTIFIER";
//            }
//        }
//        return currentTokenType;
//    }

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
        return currentToken.substring(1, currentToken.length() - 1);
    }

    // 返回当前字元
    public String getToken() {
        return currentToken;
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
        while (hasMoreTokens()) {
            advancePre();
            tokenQueue.offer(currentToken);
            switch (tokenType()) {
                case "KEYWORD":
                    writerT.println("<keyword> " + keyword() + " </keyword>");
                    break;
                case "SYMBOL":
                    writerT.println("<symbol> " + symbol() + " </symbol>");
                    break;
                case "IDENTIFIER":
                    writerT.println("<identifier> " + identifier() + " </identifier>");
                    break;
                case "INT_CONST":
                    writerT.println("<integerConstant> " + intVal() + " </integerConstant>");
                    break;
                case "STRING_CONST":
                    writerT.println("<stringConstant> " + stringVal() + " </stringConstant>");
                    break;
                default:
                    System.err.println("Invalid token type: " + tokenType());
                    break;
            }
        }
        writerT.println("</tokens>");
        try {
            writerT.close();
        } catch (Exception e) {
            System.err.println("Error closing output file: " + e.getMessage());
        }
        System.out.println("File tokenize Success: " + filePath);
    }
}
