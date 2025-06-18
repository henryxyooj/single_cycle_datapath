import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


public class MIPSTest {
    private MIPS mips;

    @BeforeEach
    void setUp() {
        mips = new MIPS();
    }

    @Test
    void testInstructionExecuteRTypeAdd() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "014b4820"); // add $t1, $t2, $t3
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.REGISTERS.put("$t3", 0x00000032);
        mips.BIT32_INSTRUCTION = "00000001010010110100100000100000";
        mips.instruction_decode();
        mips.execute();

        assertEquals(0x00000048, mips.get_REG().WRITE_DATA);
    }

    @Test
    void testInstructionExecuteRTypeAnd() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "02114024"); // and $t0, $s0, $s1
        mips.REGISTERS.put("$t0", 0x00000000);
        mips.REGISTERS.put("$s0", 0x00000064);
        mips.REGISTERS.put("$s1", 0x00000012);
        mips.BIT32_INSTRUCTION = "00000010000100010100000000100100";
        mips.instruction_decode();
        mips.execute();

        assertEquals(0x00000000, mips.get_REG().WRITE_DATA);
    }

    @Test
    void testInstructionExecuteRTypeOr() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "0272B025"); // or $s6, $s3, $s2
        mips.REGISTERS.put("$s6", 0x00000000);
        mips.REGISTERS.put("$s3", 0x00000128);
        mips.REGISTERS.put("$s2", 0x0000032);
        mips.BIT32_INSTRUCTION = "00000010011100101011000000100101";
        mips.instruction_decode();
        mips.execute();

        assertEquals(0x0000013A, mips.get_REG().WRITE_DATA);
    }

    @Test
    void testInstructionExecuteRTypeSlt() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "016A702A"); // slt $t6, $t3, $t2
        mips.REGISTERS.put("$t6", 0x00000000);
        mips.REGISTERS.put("$t3", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000008);
        mips.BIT32_INSTRUCTION = "00000001011010100111000000101010";
        mips.instruction_decode();
        mips.execute();

        assertEquals(0x00000001, mips.get_REG().WRITE_DATA);
    }

    @Test
    void testInstructionExecuteRTypeSub() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "01336822"); // sub $t5, $t1, $s3
        mips.REGISTERS.put("$t5", 0x00000000);
        mips.REGISTERS.put("$t1", 0x00000012);
        mips.REGISTERS.put("$s3", 0x00000006);
        mips.BIT32_INSTRUCTION = "00000001001100110110100000100010";
        mips.instruction_decode();
        mips.execute();

        assertEquals(0x0000000C, mips.get_REG().WRITE_DATA);
    }

    @Test
    void testInstructionExecuteRTypeSyscall() {  // not done yet
        mips.testing_mode = true;

    }

    @Test
    void testInstructionDecodeITypeAddi() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "2149FFFF"); // addi $t1, $t2, 0xffff
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.BIT32_INSTRUCTION = "00100001010010011111111111111111";
        mips.instruction_decode();

        assertEquals("001000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("1111111111111111", mips.BIT32_INSTRUCTION.substring(16, 32), "Immediate has been incorrectly parsed"); // imm

        // are the control signals correctly set?
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("00", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000016, mips.get_REG().READ_REGISTER_1, "read_register_1 = " + mips.get_REG().READ_REGISTER_1 + " instead of 0x00000016" );

        // what happens in the regdst_mux()?
        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeITypeAddiu() {    // implement later, this was copy and pasted from addi
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "2149FFFF"); // addi $t1, $t2, 0xffff
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.BIT32_INSTRUCTION = "00100001010010011111111111111111";
        mips.instruction_decode();

        assertEquals("001000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("1111111111111111", mips.BIT32_INSTRUCTION.substring(16, 32), "Immediate has been incorrectly parsed"); // imm

        // are the control signals correctly set?
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("00", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000016, mips.get_REG().READ_REGISTER_1, "read_register_1 = " + mips.get_REG().READ_REGISTER_1 + " instead of 0x00000016" );

        // what happens in the regdst_mux()?
        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeITypeAndi() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "32AD000F"); // andi $t5, $s5, 0x000f
        mips.REGISTERS.put("$t5", 0x00000000);
        mips.REGISTERS.put("$s5", 0x00000064);
        mips.BIT32_INSTRUCTION = "00110010101011010000000000001111";
        mips.instruction_decode();

        assertEquals("001100", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("0000000000001111", mips.BIT32_INSTRUCTION.substring(16, 32), "Immediate has been incorrectly parsed"); // imm

        // are the control signals correctly set?
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("11", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0000", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000064, mips.get_REG().READ_REGISTER_1, "read_register_1 = " + mips.get_REG().READ_REGISTER_1 + " instead of 0x00000064" );

        // what happens in the regdst_mux()?
        assertEquals("$t5", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeITypeOri() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "32AD000F"); // ori $t0, $s0, 0x02ff
        mips.REGISTERS.put("$t0", 0x00000012);
        mips.REGISTERS.put("$s0", 0x000000128);
        mips.BIT32_INSTRUCTION = "00110110000010000000001011111111";
        mips.instruction_decode();

        assertEquals("001101", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("0000001011111111", mips.BIT32_INSTRUCTION.substring(16, 32), "Immediate has been incorrectly parsed"); // imm

        // are the control signals correctly set?
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("11", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0001", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000128, mips.get_REG().READ_REGISTER_1, "read_register_1 = " + mips.get_REG().READ_REGISTER_1 + " instead of 0x00000064" );

        // what happens in the regdst_mux()?
        assertEquals("$t0", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeITypeLui() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "32AD000F"); // lui $t0, 0x1234
        mips.REGISTERS.put("$t0", 0x00000012);
        mips.BIT32_INSTRUCTION = "00111100000010000001001000110100";
        mips.instruction_decode();

        assertEquals("001111", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("0001001000110100", mips.BIT32_INSTRUCTION.substring(16, 32), "Immediate has been incorrectly parsed"); // imm

        // are the control signals correctly set?
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("XX", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("XXXX", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0, mips.get_REG().READ_REGISTER_1, "read_register_1 = " + mips.get_REG().READ_REGISTER_1 + " instead of 0x12340000" );
        assertEquals(0x12340000, mips.get_REG().WRITE_DATA);

        // what happens in the regdst_mux()?
        assertEquals("$t0", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeITypeBeq() {

    }

    @Test
    void testInstructionDecodeITypeBne() {

    }

    @Test
    void testInstructionDecodeITypeLw() {

    }

    @Test
    void testInstructionDecodeITypeSw() {

    }

    @Test
    void testInstructionDecodeRTypeAdd() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "014b4820"); // add $t1, $t2, $t3
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.REGISTERS.put("$t3", 0x00000032);
        mips.BIT32_INSTRUCTION = "00000001010010110100100000100000";
        mips.instruction_decode();

        assertEquals("000000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("100000", mips.BIT32_INSTRUCTION.substring(26, 32), "Funct has been incorrectly parsed");  // funct

        // are the control signals correctly set?
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("10", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000016, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");
        assertEquals(0x00000032, mips.get_REG().READ_REGISTER_2, "read_register_2 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeRTypeAnd() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "01CAA824"); // and $s5, $t6, $t2
        mips.REGISTERS.put("$s5", 0x00000064);
        mips.REGISTERS.put("$t6", 0x00000002);
        mips.REGISTERS.put("$t2", 0x00000004);
        mips.BIT32_INSTRUCTION = "00000001110010101010100000100100";
        mips.instruction_decode();

        assertEquals("000000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("100100", mips.BIT32_INSTRUCTION.substring(26, 32), "Funct has been incorrectly parsed");  // funct

        // are the control signals correctly set?
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("10", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0000", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000002, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");
        assertEquals(0x00000004, mips.get_REG().READ_REGISTER_2, "read_register_2 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertEquals("$s5", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeRTypeOr() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "0132A825"); // or $s5, $t1, $s2
        mips.REGISTERS.put("$s5", 0x00000128);
        mips.REGISTERS.put("$t1", 0x00000032);
        mips.REGISTERS.put("$s2", 0x00000064);
        mips.BIT32_INSTRUCTION = "00000001001100101010100000100101";
        mips.instruction_decode();

        assertEquals("000000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("100101", mips.BIT32_INSTRUCTION.substring(26, 32), "Funct has been incorrectly parsed");  // funct

        // are the control signals correctly set?
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("10", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0001", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000032, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");
        assertEquals(0x00000064, mips.get_REG().READ_REGISTER_2, "read_register_2 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertEquals("$s5", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeRTypeSlt() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "014B4822"); // slt $t1, $t7, $t4
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t7", 0x00000032);
        mips.REGISTERS.put("$t4", 0x00000032);
        mips.BIT32_INSTRUCTION = "00000001111011000100100000101010";
        mips.instruction_decode();

        assertEquals("000000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("101010", mips.BIT32_INSTRUCTION.substring(26, 32), "Funct has been incorrectly parsed");  // funct

        // are the control signals correctly set?
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("10", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0111", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000032, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");
        assertEquals(0x00000032, mips.get_REG().READ_REGISTER_2, "read_register_2 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeRTypeSub() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "014B4822"); // sub $t1, $t2, $t3
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.REGISTERS.put("$t3", 0x00000032);
        mips.BIT32_INSTRUCTION = "00000001010010110100100000100010";
        mips.instruction_decode();

        assertEquals("000000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("100010", mips.BIT32_INSTRUCTION.substring(26, 32), "Funct has been incorrectly parsed");  // funct

        // are the control signals correctly set?
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("10", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0110", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000016, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");
        assertEquals(0x00000032, mips.get_REG().READ_REGISTER_2, "read_register_2 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionDecodeRTypeSyscall() {  // not done yet
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "014B4822"); // sub $t1, $t2, $t3
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.REGISTERS.put("$t3", 0x00000032);
        mips.BIT32_INSTRUCTION = "00000001010010110100100000100010";
        mips.instruction_decode();

        assertEquals("000000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("100010", mips.BIT32_INSTRUCTION.substring(26, 32), "Funct has been incorrectly parsed");  // funct

        // are the control signals correctly set?
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("10", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0110", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000016, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");
        assertEquals(0x00000032, mips.get_REG().READ_REGISTER_2, "read_register_2 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testLoadMemory() throws IOException {
        Path temp_file = Files.createTempFile("test_data", ".data");
        Files.write(temp_file, Arrays.asList(
                "65746e45", // "etnE"
                "6f792072", // "oy r"
                "69207275" // "i ru"
        ));

        mips.load_memory(temp_file.toString());

        int memory = 0x10010000;
        assertEquals("65746e45", mips.MEMORY_AND_WORDS.get(memory));
        assertEquals("6f792072", mips.MEMORY_AND_WORDS.get(memory + 4));
        assertEquals("69207275", mips.MEMORY_AND_WORDS.get(memory + 8));

        Files.delete(temp_file);
    }

    @Test
    void testLoadMemoryValidFormat() throws IOException {
        Path temp_file = Files.createTempFile("test_data", ".data");
        Files.write(temp_file, Arrays.asList(
                "65746e45", // "etnE"
                "6f792072", // "oy r"
                "69207275", // "i ru"
                "ziof0kf0"
        ));

        assertThrows(IllegalArgumentException.class, () -> mips.load_memory(temp_file.toString()));

        Files.delete(temp_file);
    }

    @Test
    void testLoadInstructions() throws IOException {
        Path temp_file = Files.createTempFile("test_instructions", ".text");
        Files.write(temp_file, Arrays.asList(
                "24020004", // addiu $v0, $zero, 4
                "00422020" // add $a0, $v0, $v0
        ));

        mips.load_instructions(temp_file.toString());

        // check instructions are stored and incremented starting at TEXT_START_ADDRESS
        int address = 0x00400000;
        assertEquals("24020004", mips.INSTRUCTIONS.get(address));
        assertEquals("00422020", mips.INSTRUCTIONS.get(address + 4));

        assertEquals(2, mips.INSTRUCTIONS.size());
        assertEquals("00422020", mips.INSTRUCTIONS.get(0x00400004));

        Files.delete(temp_file);
    }

    @Test
    void testLoadInstructionsFormatValidCharacters() throws IOException {
        Path temp_file = Files.createTempFile("test_instructions", ".text");
        Files.write(temp_file, Arrays.asList(
                "24020004", // addiu $v0, $zero, 4
                "00422020", // add $a0, $v0, $v0
                "0000000z" // invalid hexadecimal value
        ));

        assertThrows(IllegalArgumentException.class, () -> mips.load_instructions(temp_file.toString()));

        Files.delete(temp_file);
    }

    @Test
    void testSetAndGetRegisterValueWithBit5() {
        String bit5 = "01000"; // $t0
        int value = 12345;

        mips.set_register_value_with_bit5(bit5, value);

        int get = mips.get_register_value_from_bit5(bit5);
        assertEquals(value, get, "Register value should match the value set");
    }

    @Test
    void testGetRegisterFromBit5() {
        String bit5 = "01000"; // $t0
        String expected_reg = "$t0";
        String actual_reg = mips.get_register_from_bit5(bit5);

        assertEquals(expected_reg, actual_reg, "Bit5 mapping to register name should be correct");
    }

    @Test
    void testReadAddress() {
        int address = 0x00400000;
        String expected_instruction = "24020004";

        mips.INSTRUCTIONS.put(address, expected_instruction);
        mips.PC = address;

        String fetched_instruction = mips.read_address();
        assertEquals(expected_instruction, fetched_instruction, "Should read correct instruction at PC");
    }
}