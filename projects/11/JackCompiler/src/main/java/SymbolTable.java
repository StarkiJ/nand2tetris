import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Object[]> classTable;
    private HashMap<String, Object[]> subroutineTable;
    private int staticIndex;//static type in class
    private int fieldIndex;//field type in class
    private int argIndex;//argument type in subroutine
    private int varIndex;//local type in subroutine

    // 构造函数：创建新的空符号表
    public SymbolTable() {
        classTable = new HashMap<>();
        subroutineTable = new HashMap<>();
        staticIndex = 0;
        fieldIndex = 0;
        argIndex = 0;
        varIndex = 0;
    }

    // 开创新的子程序作用域（即将子程序的符号表重置）
    public void startSubroutine() {
        subroutineTable.clear();
        argIndex = 0;
        varIndex = 0;
    }

    //定义给定了名称、类型和分类的新标识符，并赋给它一个索引。
    //STATIC和FIELD标识符的作用域是整个类，ARG和VAR的作用域是整个子程序
    // type: "int" | "char" | "boolean" | 类名
    // kind: "static" | "field" | "arg" | "var"
    public void define(String name, String type, String kind) {
        /*todo */
    }

    // 返回已经定义在当前作用域内的变量的数量
    public int varCount(String kind) {
        /*todo */
    }

    // 返回当前作用域内的标识符的种类。如果该标识符在当前作用域内是未知的，那么返回NONE
    public String kindOf(String name) {
        /*todo */
        return "NONE";
    }

    // 返回当前作用域内的标识符的类型
    public String typeOf(String name) {
        /*todo */
        return "NONE";
    }

    // 返回标识符的索引
    public int indexOf(String name) {
        /*todo */
        return -1;
    }
}
