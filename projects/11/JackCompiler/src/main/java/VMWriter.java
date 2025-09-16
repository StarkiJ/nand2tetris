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
        /*todo */
    }

    // 写入VM pop命令
    public void writePop(String segment, int index) {
        /*todo */
    }

    // 写入VM arithmetic命令
    public void writeArithmetic(String command) {
        /*todo */
    }

    // 写入VM label命令
    public void writeLabel(String label) {
        /*todo */
    }

    // 写入VM goto命令
    public void writeGoto(String label) {
        /*todo */
    }

    // 写入VM if-goto命令
    public void writeIf(String label) {
        /*todo */
    }

    // 写入VM call命令
    public void writeCall(String name, int nArgs) {
        /*todo */
    }

    // 写入VM function命令
    public void writeFunction(String name, int nVars) {
        /*todo */
    }

    // 写入VM return命令
    public void writeReturn() {
        /*todo */
    }

    // 关闭输出文件
    public void close() {
        writer.close();
    }
}
