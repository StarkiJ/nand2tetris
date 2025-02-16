package com.starki;

import java.io.*;

public class CodeWrite {
    private PrintWriter writer;
    private String filePath;
    private String fileName;
    private int count = 0;

    // 构造函数，打开输出文件/输出流，准备进行写入
    public CodeWrite(String fileName) {
        // 打开输出文件
        // 打开输出流
        try {
            setFileName(fileName);
            this.writer = new PrintWriter(new BufferedWriter(new FileWriter(this.filePath)));
        } catch (IOException e) {
            System.err.println("Error opening output file: " + e.getMessage());
        }
    }

    // 通知代码写入程序，新的VM文件翻译过程已经开始
    public void setFileName(String fileName) {
        this.filePath = fileName.substring(0, fileName.lastIndexOf(".")) + ".asm";
        this.fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lastIndexOf("."));
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
                writer.println("@EQ_TRUE_" + count);
                writer.println("D;JEQ");
                // else x=0
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=0");
                // goto EQ_END
                writer.println("@EQ_END_" + count);
                writer.println("0;JMP");
                // EQ_TRUE: x=1(全1)
                writer.println("(EQ_TRUE_" + count + ")");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=-1");
                writer.println("(EQ_END_" + (count++) + ")");
                break;
            case "gt":// x=(x>y)
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("D=M-D");
                writer.println("@GT_TRUE_" + count);
                writer.println("D;JGT");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=0");
                writer.println("@GT_END_" + count);
                writer.println("0;JMP");
                writer.println("(GT_TRUE_" + count + ")");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=-1");
                writer.println("(GT_END_" + (count++) + ")");
                break;
            case "lt":// x=(x<y)
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("D=M-D");
                writer.println("@LT_TRUE_" + count);
                writer.println("D;JLT");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=0");
                writer.println("@LT_END_" + count);
                writer.println("0;JMP");
                writer.println("(LT_TRUE_" + count + ")");
                writer.println("@SP");
                writer.println("A=M-1");
                writer.println("M=-1");
                writer.println("(LT_END_" + (count++) + ")");
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
        if (command.equals("C_PUSH")) {
            writer.println("// push " + segment + " " + index);
            // 根据segment的值，获取要压入栈的数据
            switch (segment) {
                case "constant":
                    writer.println("@" + index);
                    writer.println("D=A");
                    break;
                case "local":// x=*(LCL+index)
                    writer.println("@" + index);
                    writer.println("D=A");
                    writer.println("@LCL");
                    writer.println("A=D+M");
                    writer.println("D=M");
                    break;
                case "argument":
                    writer.println("@" + index);
                    writer.println("D=A");
                    writer.println("@ARG");
                    writer.println("A=D+M");
                    writer.println("D=M");
                    break;
                case "this":
                    writer.println("@" + index);
                    writer.println("D=A");
                    writer.println("@THIS");
                    writer.println("A=D+M");
                    writer.println("D=M");
                    break;
                case "that":
                    writer.println("@" + index);
                    writer.println("D=A");
                    writer.println("@THAT");
                    writer.println("A=D+M");
                    writer.println("D=M");
                    break;
                case "pointer":// x=*(THIS+index)=*(3+index)
                    writer.println("@R" + (index + 3));
                    writer.println("D=M");
                    break;
                case "temp":// x=*(5+index)
                    writer.println("@R" + (index + 5));
                    writer.println("D=M");
                    break;
                case "static":// x=*(fileName.index)
                    writer.println("@" + fileName + "." + index);
                    writer.println("D=M");
                    break;
                default:
                    System.err.println("Invalid segment: " + segment);
                    break;
            }
            // 将目标数据压入栈中，并更新栈顶指针
            writer.println("@SP");
            writer.println("A=M");
            writer.println("M=D");
            writer.println("@SP");
            writer.println("M=M+1");
        } else if (command.equals("C_POP")) {
            writer.println("// pop " + segment + " " + index);
            // 根据segment的值，将数据存储到相应的位置
            switch (segment) {
                case "local":// *(LCL+index)=*(--SP)
                    // 获取LCL[index]的地址，临时存储到R13中
                    writer.println("@" + index);
                    writer.println("D=A");
                    writer.println("@LCL");
                    writer.println("D=D+M");
                    writer.println("@R13");
                    writer.println("M=D");
                    // 从栈顶弹出数据，并更新栈顶指针
                    writer.println("@SP");
                    writer.println("AM=M-1");
                    writer.println("D=M");
                    // 从R13取出目标地址，将数据存储到LCL[index]中
                    writer.println("@R13");
                    writer.println("A=M");
                    writer.println("M=D");
                    break;
                case "argument":
                    writer.println("@" + index);
                    writer.println("D=A");
                    writer.println("@ARG");
                    writer.println("D=D+M");
                    writer.println("@R13");
                    writer.println("M=D");
                    writer.println("@SP");
                    writer.println("AM=M-1");
                    writer.println("D=M");
                    writer.println("@R13");
                    writer.println("A=M");
                    writer.println("M=D");
                    break;
                case "this":
                    writer.println("@" + index);
                    writer.println("D=A");
                    writer.println("@THIS");
                    writer.println("D=D+M");
                    writer.println("@R13");
                    writer.println("M=D");
                    writer.println("@SP");
                    writer.println("AM=M-1");
                    writer.println("D=M");
                    writer.println("@R13");
                    writer.println("A=M");
                    writer.println("M=D");
                    break;
                case "that":
                    writer.println("@" + index);
                    writer.println("D=A");
                    writer.println("@THAT");
                    writer.println("D=D+M");
                    writer.println("@R13");
                    writer.println("M=D");
                    writer.println("@SP");
                    writer.println("AM=M-1");
                    writer.println("D=M");
                    writer.println("@R13");
                    writer.println("A=M");
                    writer.println("M=D");
                    break;
                case "pointer":// *(3+index)=*(--SP)
                    writer.println("@SP");
                    writer.println("AM=M-1");
                    writer.println("D=M");
                    writer.println("@R" + (index + 3));
                    writer.println("M=D");
                    break;
                case "temp":// *(5+index)=*(--SP)
                    writer.println("@SP");
                    writer.println("AM=M-1");
                    writer.println("D=M");
                    writer.println("@R" + (index + 5));
                    writer.println("M=D");
                    break;
                case "static":// *(fileName.index)=*(--SP)
                    writer.println("@SP");
                    writer.println("AM=M-1");
                    writer.println("D=M");
                    writer.println("@" + fileName + "." + index);
                    writer.println("M=D");
                    break;
                default:
                    System.err.println("Invalid segment: " + segment);
                    break;
            }
        } else {
            System.err.println("Invalid command: " + command);
        }
    }

    // 关闭输出文件
    public void close() {
        writer.println("(END)");
        writer.println("@END");
        writer.println("0;JMP");
        writer.close();
    }
}
