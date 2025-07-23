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
        mcu.set_control_signal("000000", "000000");
        assertEquals(1, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(0, mcu.MemtoReg);
        assertEquals(0, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(1, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("10", mcu.ALUOp);
        assertEquals("rtype", mcu.instruction);
    }

    @Test
    void testSetControlSignalAddi() {
        mcu.set_control_signal("001000", "000000");
        assertEquals(0, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(0, mcu.MemtoReg);
        assertEquals(1, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(1, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("00", mcu.ALUOp);
        assertEquals("addi", mcu.instruction);
    }

    @Test
    void testSetControlSignalAddiu() {
        mcu.set_control_signal("001001", "000000");
        assertEquals(0, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(0, mcu.MemtoReg);
        assertEquals(1, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(1, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("00", mcu.ALUOp);
        assertEquals("addiu", mcu.instruction);
    }

    @Test
    void testSetControlSignalAndi() {
        mcu.set_control_signal("001100", "000000");
        assertEquals(0, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(0, mcu.MemtoReg);
        assertEquals(1, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(1, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("11", mcu.ALUOp);
        assertEquals("andi", mcu.instruction);
    }

    @Test
    void testSetControlSignalOri() {
        mcu.set_control_signal("001101", "000000");
        assertEquals(0, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(0, mcu.MemtoReg);
        assertEquals(1, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(1, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("11", mcu.ALUOp);
        assertEquals("ori", mcu.instruction);
    }

    @Test
    void testSetControlSignalLui() {
        mcu.set_control_signal("001111", "000000");
        assertEquals(0, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(-1, mcu.MemRead);
        assertEquals(-1, mcu.MemtoReg);
        assertEquals(-1, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(1, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(1, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("XX", mcu.ALUOp);
        assertEquals("lui", mcu.instruction);
    }

    @Test
    void testSetControlSignalBeq() {
        mcu.set_control_signal("000100", "000000");
        assertEquals(-1, mcu.RegDst);
        assertEquals(1, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(-1, mcu.MemtoReg);
        assertEquals(0, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(0, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("01", mcu.ALUOp);
        assertEquals("beq", mcu.instruction);
    }

    @Test
    void testSetControlSignalBne() {
        mcu.set_control_signal("000101", "000000");
        assertEquals(-1, mcu.RegDst);
        assertEquals(1, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(-1, mcu.MemtoReg);
        assertEquals(0, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(0, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("01", mcu.ALUOp);
        assertEquals("bne", mcu.instruction);
    }

    @Test
    void testSetControlSignalLw() {
        mcu.set_control_signal("100011", "000000");
        assertEquals(0, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(1, mcu.MemRead);
        assertEquals(1, mcu.MemtoReg);
        assertEquals(1, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(1, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("00", mcu.ALUOp);
        assertEquals("lw", mcu.instruction);
    }

    @Test
    void testSetControlSignalSw() {
        mcu.set_control_signal("101011", "000000");
        assertEquals(-1, mcu.RegDst);
        assertEquals(-1, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(-1, mcu.MemtoReg);
        assertEquals(1, mcu.ALUSrc);
        assertEquals(1, mcu.MemWrite);
        assertEquals(0, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("00", mcu.ALUOp);
        assertEquals("sw", mcu.instruction);
    }

    @Test
    void testSetControlSignalJ() {
        mcu.set_control_signal("000010", "000000");
        assertEquals(-1, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(-1, mcu.MemtoReg);
        assertEquals(-1, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(0, mcu.RegWrite);
        assertEquals(1, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(3, mcu.PCSrc);
        assertEquals("XX", mcu.ALUOp);
        assertEquals("j", mcu.instruction);
    }

    @Test
    void testSetControlSignalJal() {
        mcu.set_control_signal("000011", "000000");
        assertEquals(2, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(2, mcu.MemtoReg);
        assertEquals(-1, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(1, mcu.RegWrite);
        assertEquals(1, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(3, mcu.PCSrc);
        assertEquals("XX", mcu.ALUOp);
        assertEquals("jal", mcu.instruction);
    }

    @Test
    void testSetControlSignalSyscall() {
        mcu.set_control_signal("000000", "001100");
        assertEquals(0, mcu.RegDst);
        assertEquals(0, mcu.Branch);
        assertEquals(0, mcu.MemRead);
        assertEquals(0, mcu.MemtoReg);
        assertEquals(0, mcu.ALUSrc);
        assertEquals(0, mcu.MemWrite);
        assertEquals(0, mcu.RegWrite);
        assertEquals(0, mcu.Jump);
        assertEquals(0, mcu.LUICtr);
        assertEquals(0, mcu.PCSrc);
        assertEquals("XX", mcu.ALUOp);
        assertEquals("syscall", mcu.instruction);
    }

    @Test
    void testALUControlUnitAddOperation() {
        ALUControlUnit alucu = new ALUControlUnit();
        alucu.set_ALU_control_signals("00", "001000", "XXXXXX");
        assertEquals("0010", alucu.get_ALU_control_signal(), "ALU should perform addition (used for lw, sw, and addi)");
    }

    @Test
    void testALUControlUnitSubOperation() {
        ALUControlUnit alucu = new ALUControlUnit();
        alucu.set_ALU_control_signals("01", "000100", "XXXXXX");
        assertEquals("0110", alucu.get_ALU_control_signal(), "ALU should perform subtraction (used for beq and bne)");
    }

    @Test
    void testALUControlUnitAndOperation() {
        ALUControlUnit alucu = new ALUControlUnit();
        alucu.set_ALU_control_signals("11", "001100", "XXXXXX");
        assertEquals("0000", alucu.get_ALU_control_signal(), "ALU should perform and");
    }

    @Test
    void testALUControlUnitOrOperation() {
        ALUControlUnit alucu = new ALUControlUnit();
        alucu.set_ALU_control_signals("11", "001101", "XXXXXX");
        assertEquals("0001", alucu.get_ALU_control_signal(), "ALU should perform or");
    }

    @Test
    void testALUControlUnitRTypeAdd() {
        ALUControlUnit alucu = new ALUControlUnit();
        alucu.set_ALU_control_signals("10", "000000", "100000");
        assertEquals("0010", alucu.get_ALU_control_signal(), "ALU should perform addition");
    }

    @Test
    void testALUControlUnitRTypeSub() {
        ALUControlUnit alucu = new ALUControlUnit();
        alucu.set_ALU_control_signals("10", "000000", "100010");
        assertEquals("0110", alucu.get_ALU_control_signal(), "ALU should perform subtraction");
    }
}

