package com.starki;

public class VMTranslator {
    public static void main(String[] args) {
        args = new String[]{"D:\\code\\nand2tetris\\projects\\8\\ProgramFlow\\FibonacciSeries\\FibonacciSeries.vm"};
        // 确保传入的参数是有效的文件路径
        if (args.length != 1) {
            System.err.println("Usage: java com.starki.VMTranslator <VM file path>");
            return;
        }

        String filePath = args[0]; // 获取命令行参数中的文件路径

        if (filePath.endsWith(".vm"))// 检查文件路径是否以.vm结尾
        {
            System.out.println("File Translate: VM file - " + filePath);
            getCodeWrite(filePath);
        } else if (!filePath.endsWith(".")) {
            System.out.println("Directory Translate: VM directory - " + filePath);
            getCodeWrite(filePath);
        } else {
            System.err.println("Invalid file path: " + filePath);
        }
    }

    private static void getCodeWrite(String filePath) {
        Parser parser = new Parser(filePath);// 打开并解析输入文件
        CodeWrite codeWriter = new CodeWrite(filePath);

        // 逐条命令进行翻译
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

        // 完成所有命令翻译后，关闭文件流
        codeWriter.close();
        System.out.println("Translation completed successfully.");
    }
}
