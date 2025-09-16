import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Integer> symbols;

    /**
     * 构造函数：创建空的符号表
     */
    public SymbolTable() {
        symbols = new HashMap<>();
        // 预置符号表
        symbols.put("SP", 0);
        /*todo */
    }

    /**
     * 将 (symbol, address) 配对加入符号表
     */
    public void addEntry(String symbol, int address) {
        /*todo */
    }

    /**
     * 判断 symbol 是否在符号表中
     */
    public boolean contains(String symbol) {
        /*todo */
    }

    /**
     * 返回符号表中 symbol 关联的地址
      */
    public int getAddress(String symbol) {
        /*todo */
    }
}
