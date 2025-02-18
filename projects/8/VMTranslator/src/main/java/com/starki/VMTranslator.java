package com.starki;

import java.io.File;

public class VMTranslator {
    public static void main(String[] args) {
        // 如果未传入参数，使用默认文件路径
        if (args.length == 0) {
            args = new String[]{"..\\ProgramFlow\\BasicLoop\\BasicLoop.vm"};
        }

        // 确保传入的参数是有效的文件路径
        if (args.length != 1) {
            System.err.println("Usage: java com.starki.VMTranslator <VM file path>");
            return;
        }

        String filePath = args[0].trim(); // 获取命令行参数中的文件路径

        if (filePath.endsWith(".vm"))// 检查文件路径是否以.vm结尾
        {
            System.out.println("File Translate: VM file - " + filePath);
            translateFile(filePath);
        } else if (!filePath.matches(".")) {
            System.out.println("Directory Translate: VM directory - " + filePath);
            translateDirectory(filePath);
        } else {
            System.err.println("Invalid file path: " + filePath);
        }
    }

    private static void translateFile(String filePath) {
        CodeWrite codeWriter = new CodeWrite(filePath.replace(".vm", ".asm"));

        processFile(filePath, codeWriter);

        // 完成所有命令翻译后，关闭文件流
        codeWriter.close();
        System.out.println("Translation completed successfully.");
    }

    private static void translateDirectory(String filePath) {
        // 创建一个File对象，表示文件夹
        File folder = new File(filePath);

        // 检查文件夹是否存在并且是目录
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".vm"));
            // 如果有符合条件的文件
            if (files != null && files.length > 0) {
                CodeWrite codeWriter = new CodeWrite(filePath + "\\" + folder.getName() + ".asm");
                codeWriter.writeInit();
                // 遍历文件夹中的每个VM文件
                for (File file : files) {
                    processFile(filePath + "\\" + file.getName(), codeWriter);
                }
                // 完成所有命令翻译后，关闭文件流
                codeWriter.close();
                System.out.println("Translation completed successfully.");
            } else {
                System.err.println("Error VM file no found: " + filePath);
            }
        } else {
            System.err.println("Error directory no found: " + filePath);
        }
    }

    private static void processFile(String filePath, CodeWrite codeWriter) {
        codeWriter.setFileName(filePath); // 更新文件名，用于static变量

        Parser parser = new Parser(filePath); // 打开并解析输入文件

        while (parser.hasMoreCommands()) {
            parser.advance(); // 获取下一条命令

            String commandType = parser.commandType(); // 获取当前命令的类型

            switch (commandType) {
                case "C_ARITHMETIC":
                    codeWriter.writeArithmetic(parser.arg1());
                    break;
                case "C_PUSH", "C_POP":
                    codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                    break;
                case "C_LABEL":
                    codeWriter.writeLabel(parser.arg1());
                    break;
                case "C_GOTO":
                    codeWriter.writeGoto(parser.arg1());
                    break;
                case "C_IF":
                    codeWriter.writeIf(parser.arg1());
                    break;
                case "C_CALL":
                    codeWriter.writeCall(parser.arg1(), parser.arg2());
                    break;
                case "C_RETURN":
                    codeWriter.writeReturn();
                    break;
                case "C_FUNCTION":
                    codeWriter.writeFunction(parser.arg1(), parser.arg2());
                    break;
            }
        }
    }
}
