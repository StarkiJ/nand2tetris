package com.starki;

public class VMTranslator {
    public static void main(String[] args) {
        // 确保传入的参数是有效的文件路径
        if (args.length != 1) {
            System.err.println("Usage: java com.starki.VMTranslator <VM file path>");
            return;
        }

        String filePath = args[0]; // 获取命令行参数中的文件路径

        if (!filePath.endsWith(".vm"))// 检查文件路径是否以.vm结尾
        {
            System.err.println("Error: Invalid VM file - " + filePath);
            return;
        }

        getCodeWrite(filePath);
    }

    private static void getCodeWrite(String filePath) {
        Parser parser = new Parser(filePath);// 打开并解析输入文件
        CodeWrite codeWriter = new CodeWrite(filePath);

        // 逐条命令进行翻译
        while (parser.hasMoreCommands()) {
            parser.advance(); // 获取下一条命令

            String commandType = parser.commandType(); // 获取当前命令的类型

            if (commandType.equals("C_ARITHMETIC")) {
                // 如果是算术命令，写入相应的汇编代码
                codeWriter.writeArithmetic(parser.arg1());
            } else if (commandType.equals("C_PUSH") || commandType.equals("C_POP")) {
                // 如果是推送或弹出命令，写入相应的汇编代码
                codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
            }
        }

        // 完成所有命令翻译后，关闭文件流
        codeWriter.close();
        System.out.println("Translation completed successfully.");
    }
}
