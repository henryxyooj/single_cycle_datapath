import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MainControlUnitTest {
    private MainControlUnit mcu;

    @BeforeEach
    void setUp() {
        mcu = new MainControlUnit();
    }

    @Test
    void testSetControlSignalRType() {
        mcu.set_control_signal("000000");
        assertEquals(1, mcu.RegDst);
        assertEquals(1, mcu.RegWrite);
        assertEquals("10", mcu.get_ALUOp());
        assertEquals("rtype and syscall", mcu.get_instruction());
    }

    @Test
    void testSetControlSignalAddi() {
        mcu.set_control_signal("001000");
        assertEquals(0, mcu.RegDst);
        assertEquals(1, mcu.RegWrite);
        assertEquals(1, mcu.ALUSrc);
        assertEquals("00", mcu.get_ALUOp());
        assertEquals("addi", mcu.get_instruction());
    }

    @Test
    void testSetControlSignalLui() {
        mcu.set_control_signal("001111");
        assertEquals(1, mcu.LUICtr);
        assertEquals(1, mcu.RegWrite);
        assertEquals("lui", mcu.get_instruction());
    }
}
