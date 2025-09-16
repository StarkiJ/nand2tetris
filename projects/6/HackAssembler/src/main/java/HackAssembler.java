import java.io.*;

public class HackAssembler {
    public static void main(String[] args) {
        // 如果未传入参数，使用默认文件路径
        if (args.length == 0) {
            args = new String[]{"D:\\code\\nand2tetris\\projects\\9\\Snake"};
        }

        // 确保传入的参数是有效的文件路径
        if (args.length != 1) {
            System.err.println("Usage: java HackAssembler <asm file path>");
            return;
        }

        String filePath = args[0].trim(); // 获取命令行参数中的文件路径

        if (filePath.endsWith(".asm"))// 检查文件路径是否以.vm结尾
        {
            System.out.println("File Assemble: asm file - " + filePath);
            assembleFile(filePath);
        } else if (!filePath.matches(".")) {
            System.out.println("Directory Assemble: asm directory - " + filePath);
            assembleDirectory(filePath);
        } else {
            System.err.println("Invalid file path: " + filePath);
        }
    }

    private static void assembleDirectory(String filePath) {
        // 创建一个File对象，表示文件夹
        File folder = new File(filePath);

        // 检查文件夹是否存在并且是目录
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".asm"));
            /*todo */
        } else {
            System.err.println("Error directory no found: " + filePath);
        }
    }

    private static void assembleFile(String filePath) {
        try {
            // 预处理自定义地址标签，将标签符号添加到符号表
            Parser parser = new Parser(filePath);
            SymbolTable symbolTable = new SymbolTable();
            int instructionIndex = 0;

            while (parser.hasMoreInstructions()) {
                parser.advance();
                if (parser.instructionType().equals("L_INSTRUCTION")) {
                    symbolTable.addEntry(parser.symbol(), instructionIndex);
                } else {
                    instructionIndex++;
                }
            }

            // 开始生成hack文件
            parser = new Parser(filePath);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath.replace(".asm", ".hack"))));
            int varIndex = 16;

            while (parser.hasMoreInstructions()) {
                parser.advance();

                switch (parser.instructionType()) {
                    /*todo */
                    case "":
                        break;
                    case "UNKNOWN":
                        System.err.println("Invalid state: " + parser.instructionType());
                        return;
                }
            }

            writer.close();
            System.out.println("File Assemble Success: " + filePath);

        } catch (IOException e) {
            System.err.println("Error opening output file: " + e.getMessage());
        }
    }
}
