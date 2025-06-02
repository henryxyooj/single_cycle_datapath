import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class RegistersTest {
    private Registers registers;

    @BeforeEach
    void setUp() {
         registers = new Registers();
    }

    @Test
    void testReadRegister1AndData1() {
        registers.read_register_1(42);
        assertEquals(42, registers.read_data_1());
    }

    @Test
    void testReadRegister2AndData2() {
        registers.read_register_2(42);
        assertEquals(42, registers.read_data_2());
    }

    @Test
    void testWriteRegister() {
        registers.write_register("$t0");
        assertEquals("$t0", registers.WRITE_REGISTER);
    }

    @Test
    void testWriteData() {
        registers.write_data(128);
        assertEquals(128, registers.WRITE_DATA);
    }
}
