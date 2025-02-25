import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

// 分析器程序对给定的source进行操作，
// source是形如Xxx.jack的文件名称或者包含若干个此类文件的路径名
public class JackAnalyzer {
    public static void main(String[] args) {
        // 如果未传入参数，使用默认文件路径
        if (args.length == 0) {
            args = new String[]{"..\\ArrayTest"};
        }

        // 确保传入的参数是有效的文件路径
        if (args.length != 1) {
            System.err.println("Usage: java com.starki.JackCompiler <Jack file path>");
            return;
        }

        String filePath = args[0].trim(); // 获取命令行参数中的文件路径

        if (filePath.endsWith(".jack"))// 检查文件路径是否以.vm结尾
        {
            System.out.println("File Compile: Jack file - " + filePath);
            // tokenizeFile(filePath);
            compileFile(filePath);
        } else if (!filePath.matches(".")) {
            System.out.println("Directory Compile: Jack directory - " + filePath);
            // tokenizeDirectory(filePath);
            compileDirectory(filePath);
        } else {
            System.err.println("Invalid file path: " + filePath);
        }
    }

    private static void compileDirectory(String filePath) {
        // 创建一个File对象，表示文件夹
        File folder = new File(filePath);

        // 检查文件夹是否存在并且是目录
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".jack"));
            // 如果有符合条件的文件
            if (files != null && files.length > 0) {
                // 遍历文件夹中的每个Jack文件
                for (File file : files) {
                    compileFile(filePath + "\\" + file.getName());
                }
            } else {
                System.err.println("Error Jack file no found: " + filePath);
            }
        } else {
            System.err.println("Error directory no found: " + filePath);
        }
    }

    private static void compileFile(String filePath) {
        int index = filePath.lastIndexOf("\\");
        String fileName = filePath.substring(0, index) + "\\output\\"
                + filePath.substring(index + 1, filePath.lastIndexOf("."));
        PrintWriter writer;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".xml")));
        } catch (Exception e) {
            System.err.println("Error creating output file: " + e.getMessage());
            return;
        }
        JackTokenizer tokenizer = new JackTokenizer(filePath);
        CompilationEngine engine = new CompilationEngine(tokenizer, writer);
        engine.compileClass();
        try {
            writer.close();
        } catch (Exception e) {
            System.err.println("Error closing output file: " + e.getMessage());
        }
        System.out.println("File Compile Success: " + filePath);
    }
}
