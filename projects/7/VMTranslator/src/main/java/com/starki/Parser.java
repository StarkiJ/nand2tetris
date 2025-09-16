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
        /*todo */
    }

    // 输入当中还有更多命令吗？
    public boolean hasMoreCommands() {
        /*todo */
    }

    // 从输入读取下一条命令，将其指定为“当前命令”。
    // 仅当hasMoreCommands()返回为真时，才能调用此程序。初始情况下，没有“当前命令”
    public void advance() {
        /*todo */
    }

    // 返回当前VM命令的类型，
    // 对于所有算术命令，总是返回C_ARITHMETIC
    public String commandType() {
        if (currentCommand.startsWith("push")) {
            return "C_PUSH";
        }
        /*todo */
    }

    // 返回当前命令的第一个参数，
    // 如果当前命令类型为C_ARITHMETIC，则返回算术命令的名称（如add,sub等）。
    // 当前命令类型为C_RETURN时，不应该调用本程序
    public String arg1() {
        /*todo */
    }

    // 返回当前命令的第二个参数，
    // 仅当前命令类型为C_PUSH，C_POP，C_FUNCTION，C_CALL，才可调用，返回该内存地址
    // 当前命令类型为C_ARITHMETIC，C_RETURN时，不应该调用本程序
    public int arg2() {
        if (commandType().equals("C_PUSH") || commandType().equals("C_POP")
                || commandType().equals("C_FUNCTION") || commandType().equals("C_CALL")) {
            /*todo */
        } else {
            return -1; // 如果命令类型不需要第二个参数，返回默认值
        }
    }
}
