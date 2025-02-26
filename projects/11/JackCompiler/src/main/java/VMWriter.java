import java.io.*;

public class VMWriter {
    private final PrintWriter writer;

    // 构造函数：创建新的待写文件
    public VMWriter(String fileName) {
        // 打开文件
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 写入VM push命令
    public void writePush(String segment, int index) {
        writer.println("push " + segment + " " + index);
    }

    // 写入VM pop命令
    public void writePop(String segment, int index) {
        writer.println("pop " + segment + " " + index);
    }

    // 写入VM arithmetic命令
    public void writeArithmetic(String command) {
        writer.println(command);
    }

    // 写入VM label命令
    public void writeLabel(String label) {
        writer.println("label " + label);
    }

    // 写入VM goto命令
    public void writeGoto(String label) {
        writer.println("goto " + label);
    }

    // 写入VM if-goto命令
    public void writeIf(String label) {
        writer.println("if-goto " + label);
    }

    // 写入VM call命令
    public void writeCall(String name, int nArgs) {
        writer.println("call " + name + " " + nArgs);
    }

    // 写入VM function命令
    public void writeFunction(String name, int nVars) {
        writer.println("function " + name + " " + nVars);
    }

    // 写入VM return命令
    public void writeReturn() {
        writer.println("return");
    }

    // 关闭输出文件
    public void close() {
        writer.close();
    }
}
