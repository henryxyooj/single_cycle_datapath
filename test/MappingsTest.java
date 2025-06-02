import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MappingsTest {
    @Test
    void testHexToBit4Mapping() {
        assertEquals("0000", Mappings.HEX_TO_BIT4.get("0"));
        assertEquals("0001", Mappings.HEX_TO_BIT4.get("1"));
        assertEquals("0010", Mappings.HEX_TO_BIT4.get("2"));
        assertEquals("0011", Mappings.HEX_TO_BIT4.get("3"));
        assertEquals("0100", Mappings.HEX_TO_BIT4.get("4"));
        assertEquals("0101", Mappings.HEX_TO_BIT4.get("5"));
        assertEquals("0110", Mappings.HEX_TO_BIT4.get("6"));
        assertEquals("0111", Mappings.HEX_TO_BIT4.get("7"));
        assertEquals("1000", Mappings.HEX_TO_BIT4.get("8"));
        assertEquals("1001", Mappings.HEX_TO_BIT4.get("9"));
        assertEquals("1010", Mappings.HEX_TO_BIT4.get("a"));
        assertEquals("1011", Mappings.HEX_TO_BIT4.get("b"));
        assertEquals("1100", Mappings.HEX_TO_BIT4.get("c"));
        assertEquals("1101", Mappings.HEX_TO_BIT4.get("d"));
        assertEquals("1110", Mappings.HEX_TO_BIT4.get("e"));
        assertEquals("1111", Mappings.HEX_TO_BIT4.get("f"));
    }

    @Test
    void testBit5ToRegMapping() {
        assertEquals("$zero", Mappings.BIT5_TO_REG.get("00000"));
        assertEquals("$at", Mappings.BIT5_TO_REG.get("00001"));
        assertEquals("$v0", Mappings.BIT5_TO_REG.get("00010"));
        assertEquals("$v1", Mappings.BIT5_TO_REG.get("00011"));
        assertEquals("$a0", Mappings.BIT5_TO_REG.get("00100"));
        assertEquals("$a1", Mappings.BIT5_TO_REG.get("00101"));
        assertEquals("$a2", Mappings.BIT5_TO_REG.get("00110"));
        assertEquals("$a3", Mappings.BIT5_TO_REG.get("00111"));
        assertEquals("$t0", Mappings.BIT5_TO_REG.get("01000"));
        assertEquals("$t1", Mappings.BIT5_TO_REG.get("01001"));
        assertEquals("$t2", Mappings.BIT5_TO_REG.get("01010"));
        assertEquals("$t3", Mappings.BIT5_TO_REG.get("01011"));
        assertEquals("$t4", Mappings.BIT5_TO_REG.get("01100"));
        assertEquals("$t5", Mappings.BIT5_TO_REG.get("01101"));
        assertEquals("$t6", Mappings.BIT5_TO_REG.get("01110"));
        assertEquals("$t7", Mappings.BIT5_TO_REG.get("01111"));
        assertEquals("$s0", Mappings.BIT5_TO_REG.get("10000"));
        assertEquals("$s1", Mappings.BIT5_TO_REG.get("10001"));
        assertEquals("$s2", Mappings.BIT5_TO_REG.get("10010"));
        assertEquals("$s3", Mappings.BIT5_TO_REG.get("10011"));
        assertEquals("$s4", Mappings.BIT5_TO_REG.get("10100"));
        assertEquals("$s5", Mappings.BIT5_TO_REG.get("10101"));
        assertEquals("$s6", Mappings.BIT5_TO_REG.get("10110"));
        assertEquals("$s7", Mappings.BIT5_TO_REG.get("10111"));
        assertEquals("$t8", Mappings.BIT5_TO_REG.get("11000"));
        assertEquals("$t9", Mappings.BIT5_TO_REG.get("11001"));
        assertEquals("$k0", Mappings.BIT5_TO_REG.get("11010"));
        assertEquals("$k1", Mappings.BIT5_TO_REG.get("11011"));
        assertEquals("$gp", Mappings.BIT5_TO_REG.get("11100"));
        assertEquals("$sp", Mappings.BIT5_TO_REG.get("11101"));
        assertEquals("$fp", Mappings.BIT5_TO_REG.get("11110"));
        assertEquals("$ra", Mappings.BIT5_TO_REG.get("11111"));
    }

    @Test
    void testRegToBit5Mapping() {
        assertEquals("00000", Mappings.REG_TO_BIT5.get("$zero"));
        assertEquals("00001", Mappings.REG_TO_BIT5.get("$at"));
        assertEquals("00010", Mappings.REG_TO_BIT5.get("$v0"));
        assertEquals("00011", Mappings.REG_TO_BIT5.get("$v1"));
        assertEquals("00100", Mappings.REG_TO_BIT5.get("$a0"));
        assertEquals("00101", Mappings.REG_TO_BIT5.get("$a1"));
        assertEquals("00110", Mappings.REG_TO_BIT5.get("$a2"));
        assertEquals("00111", Mappings.REG_TO_BIT5.get("$a3"));
        assertEquals("01000", Mappings.REG_TO_BIT5.get("$t0"));
        assertEquals("01001", Mappings.REG_TO_BIT5.get("$t1"));
        assertEquals("01010", Mappings.REG_TO_BIT5.get("$t2"));
        assertEquals("01011", Mappings.REG_TO_BIT5.get("$t3"));
        assertEquals("01100", Mappings.REG_TO_BIT5.get("$t4"));
        assertEquals("01101", Mappings.REG_TO_BIT5.get("$t5"));
        assertEquals("01110", Mappings.REG_TO_BIT5.get("$t6"));
        assertEquals("01111", Mappings.REG_TO_BIT5.get("$t7"));
        assertEquals("10000", Mappings.REG_TO_BIT5.get("$s0"));
        assertEquals("10001", Mappings.REG_TO_BIT5.get("$s1"));
        assertEquals("10010", Mappings.REG_TO_BIT5.get("$s2"));
        assertEquals("10011", Mappings.REG_TO_BIT5.get("$s3"));
        assertEquals("10100", Mappings.REG_TO_BIT5.get("$s4"));
        assertEquals("10101", Mappings.REG_TO_BIT5.get("$s5"));
        assertEquals("10110", Mappings.REG_TO_BIT5.get("$s6"));
        assertEquals("10111", Mappings.REG_TO_BIT5.get("$s7"));
        assertEquals("11000", Mappings.REG_TO_BIT5.get("$t8"));
        assertEquals("11001", Mappings.REG_TO_BIT5.get("$t9"));
        assertEquals("11010", Mappings.REG_TO_BIT5.get("$k0"));
        assertEquals("11011", Mappings.REG_TO_BIT5.get("$k1"));
        assertEquals("11100", Mappings.REG_TO_BIT5.get("$gp"));
        assertEquals("11101", Mappings.REG_TO_BIT5.get("$sp"));
        assertEquals("11110", Mappings.REG_TO_BIT5.get("$fp"));
        assertEquals("11111", Mappings.REG_TO_BIT5.get("$ra"));
    }
}
