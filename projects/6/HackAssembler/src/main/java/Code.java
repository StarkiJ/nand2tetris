public class Code {
    /**
     * 返回 dest 助记符对应的二进制码 (3位)
     */
    public static String dest(String dest) {
        if (dest == null) {
            return "000";
        }
        String[] result = {"0", "0", "0"};
        if (dest.contains("A")) {
            result[0] = "1";
        }
        if (dest.contains("D")) {
            result[1] = "1";
        }
        if (dest.contains("M")) {
            result[2] = "1";
        }
        return String.join("", result);
    }

    /**
     * 返回 comp 助记符对应的二进制码 (7位)
     */
    public static String comp(String comp) {
        return switch (comp) {
            case "0" -> "0101010";
            case "1" -> "0111111";
            case "-1" -> "0111010";
            case "D" -> "0001100";
            case "A" -> "0110000";
            case "!D" -> "0001101";
            case "!A" -> "0110001";
            case "-D" -> "0001111";
            case "-A" -> "0110011";
            case "D+1" -> "0011111";
            case "A+1" -> "0110111";
            case "D-1" -> "0001110";
            case "A-1" -> "0110010";
            case "D+A" -> "0000010";
            case "D-A" -> "0010011";
            case "A-D" -> "0000111";
            case "D&A" -> "0000000";
            case "D|A" -> "0010101";
            case "M" -> "1110000";
            case "!M" -> "1110001";
            case "-M" -> "1110011";
            case "M+1" -> "1110111";
            case "M-1" -> "1110010";
            case "D+M" -> "1000010";
            case "D-M" -> "1010011";
            case "M-D" -> "1000111";
            case "D&M" -> "1000000";
            case "D|M" -> "1010101";
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
            case "JEQ" -> "010";
            case "JGE" -> "011";
            case "JLT" -> "100";
            case "JNE" -> "101";
            case "JLE" -> "110";
            case "JMP" -> "111";
            default -> throw new IllegalArgumentException("Invalid jump: " + jump);
        };
    }
}
