package com.starki;

import java.util.Scanner;
import java.io.*;

// 分析.vm文件，封装对输入代码的访问。
// 它读取VM命令并解析，然后为它们的各个部分提供方便的访问入口。
// 除此之外，它还移除代码中所有的空格和注释
public class Parser {
    private Scanner scanner;
    private String currentCommand;

    // 构造函数，打开输入文件/输入流，准备进行语法解析
    public Parser(String fileName) {
        try {
            File file = new File(fileName);
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            e.printStackTrace();
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
        do {
            currentCommand = scanner.nextLine().trim();// 移除首尾的空格
        } while (currentCommand.isEmpty() || currentCommand.startsWith("//"));
    }

    // 返回当前VM命令的类型，
    // 对于所有算术命令，总是返回C_ARITHMETIC
    public String commandType() {
        if (currentCommand.startsWith("push")) {
            return "C_PUSH";
        } else if (currentCommand.startsWith("pop")) {
            return "C_POP";
        } else if (currentCommand.equals("return")) {
            return "C_RETURN";
        } else if (currentCommand.equals("add") || currentCommand.equals("sub") || currentCommand.equals("neg")
                || currentCommand.equals("eq") || currentCommand.equals("gt") || currentCommand.equals("lt")
                || currentCommand.equals("and") || currentCommand.equals("or") || currentCommand.equals("not")) {
            return "C_ARITHMETIC";
        } else {
            return null; // 或者抛出异常，表示不支持的命令
        }
    }

    // 返回当前命令的第一个参数，
    // 如果当前命令类型为C_ARITHMETIC，则返回算术命令的名称（如add,sub等）。
    // 当前命令类型为C_RETURN时，不应该调用本程序
    public String arg1() {
        if (commandType().equals("C_ARITHMETIC")) {
            return currentCommand; // 对于算术命令，直接返回命令名称（例如 add, sub）
        } else if (commandType().equals("C_PUSH") || commandType().equals("C_POP")) {
            String[] parts = currentCommand.split(" ");
            return parts[1]; // 返回段名（例如 local, argument, this 等）
        } else {
            return null; // 如果命令类型不需要参数，返回 null
        }
    }

    // 返回当前命令的第二个参数，
    // 仅当前命令类型为C_PUSH，C_POP，C_FUNCTION，C_CALL，才可调用，返回该内存地址
    // 当前命令类型为C_ARITHMETIC，C_RETURN时，不应该调用本程序
    public int arg2() {
        if (commandType().equals("C_PUSH") || commandType().equals("C_POP")
                || commandType().equals("C_FUNCTION") || commandType().equals("C_CALL")) {
            String[] parts = currentCommand.split(" ");
            return Integer.parseInt(parts[2]); // 返回栈的索引或变量、参数的个数
        } else {
            return -1; // 如果命令类型不需要第二个参数，返回默认值
        }
    }
}
