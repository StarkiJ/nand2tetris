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
        symbols.put("LCL", 1);
        symbols.put("ARG", 2);
        symbols.put("THIS", 3);
        symbols.put("THAT", 4);
        symbols.put("SCREEN", 16384);
        symbols.put("KBD", 24576);
        for (int i = 0; i < 16; i++) {
            symbols.put("R" + i, i);
        }
    }

    /**
     * 将 (symbol, address) 配对加入符号表
     */
    public void addEntry(String symbol, int address) {
        if (symbols.containsKey(symbol)) {
            return;
        }
        symbols.put(symbol, address);
    }

    /**
     * 判断 symbol 是否在符号表中
     */
    public boolean contains(String symbol) {
        return symbols.containsKey(symbol);
    }

    /**
     * 返回符号表中 symbol 关联的地址
      */
    public int getAddress(String symbol) {
        return symbols.get(symbol);
    }
}
