import java.util.HashMap;
import java.util.Map;

public class Mappings {
    public static final Map<String, String> HEX_TO_BIT4 = new HashMap<>();
    public static final Map<String, String> BIT5_TO_REG = new HashMap<>();
    public static final Map<String, String> REG_TO_BIT5 = new HashMap<>();

    static {
        HEX_TO_BIT4.put("0", "0000"); HEX_TO_BIT4.put("1", "0001");
        HEX_TO_BIT4.put("2", "0010"); HEX_TO_BIT4.put("3", "0011");
        HEX_TO_BIT4.put("4", "0100"); HEX_TO_BIT4.put("5", "0101");
        HEX_TO_BIT4.put("6", "0110"); HEX_TO_BIT4.put("7", "0111");
        HEX_TO_BIT4.put("8", "1000"); HEX_TO_BIT4.put("9", "1001");
        HEX_TO_BIT4.put("a", "1010"); HEX_TO_BIT4.put("b", "1011");
        HEX_TO_BIT4.put("c", "1100"); HEX_TO_BIT4.put("d", "1101");
        HEX_TO_BIT4.put("e", "1110"); HEX_TO_BIT4.put("f", "1111");

        BIT5_TO_REG.put("00000", "$zero"); BIT5_TO_REG.put("00001", "$at");
        BIT5_TO_REG.put("00010", "$v0"); BIT5_TO_REG.put("00011", "$v1");
        BIT5_TO_REG.put("00100", "$a0"); BIT5_TO_REG.put("00101", "$a1");
        BIT5_TO_REG.put("00110", "$a2"); BIT5_TO_REG.put("00111", "$a3");
        BIT5_TO_REG.put("01000", "$t0"); BIT5_TO_REG.put("01001", "$t1");
        BIT5_TO_REG.put("01010", "$t2"); BIT5_TO_REG.put("01011", "$t3");
        BIT5_TO_REG.put("01100", "$t4"); BIT5_TO_REG.put("01101", "$t5");
        BIT5_TO_REG.put("01110", "$t6"); BIT5_TO_REG.put("01111", "$t7");
        BIT5_TO_REG.put("10000", "$s0"); BIT5_TO_REG.put("10001", "$s1");
        BIT5_TO_REG.put("10010", "$s2"); BIT5_TO_REG.put("10011", "$s3");
        BIT5_TO_REG.put("10100", "$s4"); BIT5_TO_REG.put("10101", "$s5");
        BIT5_TO_REG.put("10110", "$s6"); BIT5_TO_REG.put("10111", "$s7");
        BIT5_TO_REG.put("11000", "$t8"); BIT5_TO_REG.put("11001", "$t9");
        BIT5_TO_REG.put("11010", "$k0"); BIT5_TO_REG.put("11011", "$k1");
        BIT5_TO_REG.put("11100", "$gp"); BIT5_TO_REG.put("11101", "$sp");
        BIT5_TO_REG.put("11110", "$fp"); BIT5_TO_REG.put("11111", "$ra");

        REG_TO_BIT5.put("$zero", "00000"); REG_TO_BIT5.put("$at", "00001");
        REG_TO_BIT5.put("$v0", "00010"); REG_TO_BIT5.put("$v1", "00011");
        REG_TO_BIT5.put("$a0", "00100"); REG_TO_BIT5.put("$a1", "00101");
        REG_TO_BIT5.put("$a2", "00110"); REG_TO_BIT5.put("$a3", "00111");
        REG_TO_BIT5.put("$t0", "01000"); REG_TO_BIT5.put("$t1", "01001");
        REG_TO_BIT5.put("$t2", "01010"); REG_TO_BIT5.put("$t3", "01011");
        REG_TO_BIT5.put("$t4", "01100"); REG_TO_BIT5.put("$t5", "01101");
        REG_TO_BIT5.put("$t6", "01110"); REG_TO_BIT5.put("$t7", "01111");
        REG_TO_BIT5.put("$s0", "10000"); REG_TO_BIT5.put("$s1", "10001");
        REG_TO_BIT5.put("$s2", "10010"); REG_TO_BIT5.put("$s3", "10011");
        REG_TO_BIT5.put("$s4", "10100"); REG_TO_BIT5.put("$s5", "10101");
        REG_TO_BIT5.put("$s6", "10110"); REG_TO_BIT5.put("$s7", "10111");
        REG_TO_BIT5.put("$t8", "11000"); REG_TO_BIT5.put("$t9", "11001");
        REG_TO_BIT5.put("$k0", "11010"); REG_TO_BIT5.put("$k1", "11011");
        REG_TO_BIT5.put("$gp", "11100"); REG_TO_BIT5.put("$sp", "11101");
        REG_TO_BIT5.put("$fp", "11110"); REG_TO_BIT5.put("$ra", "11111");
    }
}
