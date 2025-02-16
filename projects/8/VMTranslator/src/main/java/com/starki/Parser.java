package com.starki;

import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

// 分析.vm文件，封装对输入代码的访问。
// 它读取VM命令并解析，然后为它们的各个部分提供方便的访问入口。
// 除此之外，它还移除代码中所有的空格和注释
public class Parser {
    private Scanner scanner;
    private String[] currentCommand;

    // 构造函数，打开输入文件/输入流，准备进行语法解析
    public Parser(String fileName) {
        try {
            File file = new File(fileName);
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        }
    }

    // 输入当中还有更多命令吗？
    public boolean hasMoreCommands() {
        if (scanner.hasNextLine()) {
            return true;
        } else {
            scanner.close(); // 关闭Scanner对象，释放资源
            return false;
        }
    }

    // 从输入读取下一条命令，将其指定为“当前命令”。
    // 仅当hasMoreCommands()返回为真时，才能调用此程序。初始情况下，没有“当前命令”
    public void advance() {
        String tmpLine;
        do {
            tmpLine = scanner.nextLine().trim();
        } while (tmpLine.isEmpty() || tmpLine.startsWith("//"));
        currentCommand = tmpLine.replaceAll("//.*", "").trim().split(" ");
    }

    // 返回当前VM命令的类型，
    // 对于所有算术命令，总是返回C_ARITHMETIC
    public String commandType() {
        return switch (currentCommand[0]) {
            case "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not" -> "C_ARITHMETIC";
            case "push" -> "C_PUSH";
            case "pop" -> "C_POP";
            case "label" -> "C_LABEL";
            case "goto" -> "C_GOTO";
            case "if-goto" -> "C_IF";
            case "function" -> "C_FUNCTION";
            case "call" -> "C_CALL";
            case "return" -> "C_RETURN";
            default -> {
                System.err.println("Error unsupported command: " + Arrays.toString(currentCommand));
                yield null;
            }
        };
    }

    // 返回当前命令的第一个参数，
    // 如果当前命令类型为C_ARITHMETIC，则返回算术命令的名称（如add,sub等）。
    // 当前命令类型为C_RETURN时，不应该调用本程序
    public String arg1() {
        return switch (commandType()) {
            // 对于算术命令，直接返回命令名称（例如 add, sub）
            case "C_ARITHMETIC" -> currentCommand[0];
            // 返回段名（例如 local, argument, this 等）
            case "C_PUSH", "C_POP", "C_LABEL", "C_GOTO", "C_IF", "C_CALL", "C_FUNCTION" -> currentCommand[1];
            // 如果命令类型不需要参数，返回 null
            default -> {
                System.err.println("Error unsupported command: " + Arrays.toString(currentCommand));
                yield null;
            }
        };

    }

    // 返回当前命令的第二个参数，
    // 仅当前命令类型为C_PUSH，C_POP，C_FUNCTION，C_CALL，才可调用，返回该内存地址
    // 当前命令类型为C_ARITHMETIC，C_RETURN时，不应该调用本程序
    public int arg2() {
        return switch (commandType()) {
            // 返回栈的索引或变量、参数的个数
            case "C_PUSH", "C_POP", "C_FUNCTION", "C_CALL" -> Integer.parseInt(currentCommand[2]);
            // 如果命令类型不需要第二个参数，返回默认值
            default -> {
                System.err.println("Error unsupported command: " + Arrays.toString(currentCommand));
                yield -1;
            }
        };

    }
}
