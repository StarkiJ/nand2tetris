public class Code {
    /**
     * 返回 dest 助记符对应的二进制码 (3位)
     */
    public static String dest(String dest) {
        if (dest == null) {
            return "000";
        }
        String[] result = {"0", "0", "0"};
        /*todo */
        return String.join("", result);
    }

    /**
     * 返回 comp 助记符对应的二进制码 (7位)
     */
    public static String comp(String comp) {
        return switch (comp) {
            case "0" -> "0101010";
            case "1" -> "0111111";
            /*todo */
            default -> throw new IllegalArgumentException("Invalid comp: " + comp);
        };
    }

    /**
     * 返回 jump 助记符对应的二进制码 (3位)
     */
    public static String jump(String jump) {
        if (jump == null) {
            return "000";
        }

        return switch (jump) {
            case "JGT" -> "001";
            /*todo */
            default -> throw new IllegalArgumentException("Invalid jump: " + jump);
        };
    }
}
