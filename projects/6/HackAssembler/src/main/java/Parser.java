import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private Scanner scanner;
    private String currentLine = "";// 按空格分组的当前行
    private int lineIndex = 0;
    private String instruction;

    private static final int NORMAL = 0;  // 正常状态
    private static final int IN_COMMENT = 1;  // 注释内
    private int state = NORMAL;

    /**
     * 构造函数：打开输入文件/输入流，为语法解析作准备
     */
    public Parser(String fileName) {
        try {
            scanner = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        }
    }

    /**
     * 输入当中还有更多命令吗？
     */
    public boolean hasMoreCommands() {
        if (lineIndex < currentLine.length()) {
            return true;
        } else if (scanner.hasNextLine()) {//当前文件还有字符
            currentLine = scanner.nextLine()
                    //.replaceAll("//.*|/\\*.*|\\*.*|/\\*/.*", "")
                    .replaceAll("//.*", "")
                    .trim()/*.split("\\s+")*/;
            lineIndex = 0;
            return hasMoreCommands();
        } else {
            scanner.close();
            return false;
        }
    }

    /**
     * 从输入中获取下一个命令，使其成为当前命令。
     * 该函数仅当hasMoreTokens()返回为真时才能调用。
     * 最初始状态是没有当前命令
     */
    public void advance() {
        instruction = "";
        while (hasMoreCommands()) {
            String currentChar = "" + currentLine.charAt(lineIndex++);
            switch (state) {
                case NORMAL:
                    if (currentChar.equals("/") && hasMoreCommands() && currentLine.charAt(lineIndex) == '*') {
                        state = IN_COMMENT;
                    } else if (currentChar.equals(" ")) {
                        if (instruction.isEmpty()) {
                            advance();
                        }
                        return;
                    } else {
                        instruction += currentChar;
                    }
                    break;
                case IN_COMMENT:
                    if (currentChar.equals("*") && hasMoreCommands() && currentLine.charAt(lineIndex) == '/') {
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

    /**
     * 返回当前命令的类型:
     * A_COMMAND
     * 当 @Xxx 中的 Xxx 是符号或十进制数字使
     * C_COMMAND
     * 用于 dest=comp;jump
     * L_COMMAND (实际上是伪命令)
     * 当 (Xxx) 中的 Xxx 是符号时
     */
    public String instructionType() {
        if (instruction.charAt(0) == '@') {
            return "A_INSTRUCTION";
        } else if (instruction.charAt(0) == '(') {
            return "L_INSTRUCTION";
        } else {
            return "C_INSTRUCTION";
        }
    }

    /**
     * 返回形如@Xxx或(Xxx)的当前命令的符号或十进制值。
     * 仅当 instructionType()是 A_INSTRUCTION 或 L_INSTRUCTION 时才能调用
     */
    public String symbol() {
        return switch (instructionType()) {
            case "A_INSTRUCTION" -> instruction.substring(1);
            case "L_INSTRUCTION" -> instruction.substring(1, instruction.length() - 1);
            default -> null;
        };
    }

    /**
     * 返回当前 C-指令的 dest 助记符 (具有8种可能的形式)
     * 仅当 commandrype()是 C_INSTRUCTION 时才能调用
     */
    public String dest() {
        if (instructionType().equals("C_INSTRUCTION")) {
            String[] parts = instruction.split("=");
            return parts.length > 1 ? parts[0] : null;
        }
        return null;
    }

    /**
     * 返回当前 C-指令的 comp 助记符 (具有28种可能的形式)
     * 仅当 instructionrype()是 C_INSTRUCTION 时才能调用
     */
    public String comp() {
        if (instructionType().equals("C_INSTRUCTION")) {
            String[] parts = instruction.split("=");
            return parts.length > 1 ? parts[1] : instruction;
        }
        return null;
    }

    /**
     * 返回当前 C-指令的 jump 助记符 (具有8种可能的形式)
     * 仅当 instructionrype()是 C_INSTRUCTION 时才能调用
     */
    public String jump() {
        if (instructionType().equals("C_INSTRUCTION")) {
            String[] parts = instruction.split(";");
            return parts.length > 1 ? parts[1] : null;
        }
        return null;
    }

}
