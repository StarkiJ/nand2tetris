package com.starki;

import java.io.*;

public class CodeWrite {
    private PrintWriter writer;
    private String fileName;
    private int i = 0;

    // 构造函数，打开输出文件/输出流，准备进行写入
    public CodeWrite(String fileName) {
        // 打开输出文件
        // 打开输出流
        try {
            setFileName(fileName);
            this.writer = new PrintWriter(new BufferedWriter(new FileWriter(this.fileName)));
        } catch (IOException e) {
            System.err.println("Error opening output file: " + e.getMessage());
        }
    }

    // 通知代码写入程序，新的VM文件翻译过程已经开始
    public void setFileName(String fileName) {
        // 写入init代码
        this.fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".asm";
//        writer.println("// init");
//        writer.println("@256");
//        writer.println("D=A");
//        writer.println("@SP");
//        writer.println("M=D");
//        writer.println(" // Your additional init code here (e.g., calling Sys.init)");
    }

    // 将给定的算术操作所对应的汇编代码写至输出
    public void writeArithmetic(String command) {
        writer.println("// " + command); // 添加原始VM命令作为注释
        switch (command) {
            case "add":// x+y
                // y=*(SP--)
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                // x=x+y
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=M+D");
                break;
            case "sub":// x-y
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=M-D");
                break;
            case "neg":// y=-y
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=-M");
                break;
            case "eq":// x=(x==y)
                // y=*(SP--)
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                // D=x-y
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("D=M-D");
                // if (x==y) goto EQ_TRUE
                writer.println("@EQ_TRUE_" + i);
                writer.println("D;JEQ");
                // else x=0
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=0");
                // goto EQ_END
                writer.println("@EQ_END_" + i);
                writer.println("0;JMP");
                // EQ_TRUE: x=1(全1)
                writer.println("(EQ_TRUE_" + i + ")");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=-1");
                writer.println("(EQ_END_" + (i++) + ")");
                break;
            case "gt":// x=(x>y)
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("D=M-D");
                writer.println("@GT_TRUE_" + i);
                writer.println("D;JGT");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=0");
                writer.println("@GT_END_" + i);
                writer.println("0;JMP");
                writer.println("(GT_TRUE_" + i + ")");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=-1");
                writer.println("(GT_END_" + (i++) + ")");
                break;
            case "lt":// x=(x<y)
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("D=M-D");
                writer.println("@LT_TRUE_" + i);
                writer.println("D;JLT");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=0");
                writer.println("@LT_END_" + i);
                writer.println("0;JMP");
                writer.println("(LT_TRUE_" + i + ")");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=-1");
                writer.println("(LT_END_" + (i++) + ")");
                break;
            case "and":// x=x&y
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=D&M");
                break;
            case "or":// x=x|y
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=D|M");
                break;
            case "not":// !y
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=!M");
                break;
            default:
                System.err.println("Invalid arithmetic command: " + command);
                break;
        }
    }

    // 将给定的command（命令类型为C_PUSH或C_POP）所对应的汇编代码写入至输出
    public void writePushPop(String command, String segment, int index) {
        writer.println("// " + command + " " + segment + " " + index);

    }

    // 关闭输出文件
    public void close() {
        // 关闭输出文件
    }
}
