import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MIPSTest {
    private MIPS mips;

    @BeforeEach
    void setUp() {
        mips = new MIPS();
    }

    @Test
    void testWriteBackJTypeJ() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "0840000C");  // j 0x0040000c
        mips.BIT32_INSTRUCTION = "00001000000100000000000000000011";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);

        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
    }

    @Test
    void testMemoryJTypeJ() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "0840000C");  // j 0x0040000c
        mips.BIT32_INSTRUCTION = "00001000000100000000000000000011";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
    }

    @Test
    void testInstructionDecodeJTypeJ() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "0840000C");  // j 0x0040000c
        mips.BIT32_INSTRUCTION = "00001000000100000000000000000011";
        mips.instruction_decode();

        assertEquals("000010", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");
        assertEquals("00000100000000000000000011", mips.BIT32_INSTRUCTION.substring(6, 32));

        // are the control signals correctly set?
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("XX", mips.get_MAIN_CONTROL_UNIT().ALUOp);
        assertEquals("j", mips.get_MAIN_CONTROL_UNIT().instruction);

        // what did the ALU yield?
        assertEquals("XXXX", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());
    }

    @Test
    void testJumpMuxJ() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "08100003");  // j 0x0040000c
        mips.BIT32_INSTRUCTION = "00001000000100000000000000000011";

        mips.instruction_decode();
        assertEquals("00000100000000000000000011", mips.TARGET);

        mips.jump_mux();
        assertEquals(0x0040000C, mips.JUMP_ADDRESS);
    }

    @Test
    void testWriteBackJTypeJr() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400014, "0x03e00008");  // jr $ra
        mips.BIT32_INSTRUCTION = "00000011111000000000000000001000";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);

        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
    }

    @Test
    void testMemoryJTypeJr() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400014, "0x03e00008");  // jr $ra
        mips.BIT32_INSTRUCTION = "00000011111000000000000000001000";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
    }

    @Test
    void testExecuteJTypeJr() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400014, "0x03e00008");  // jr $ra
        mips.BIT32_INSTRUCTION = "00000011111000000000000000001000";
        mips.REGISTERS.put("$ra", 0x00400008);
        mips.PC = 0x00400014;
        mips.instruction_decode();
        mips.execute();

        assertEquals(2, mips.get_MAIN_CONTROL_UNIT().PCSrc);
        assertEquals(0x00400008, mips.PC);
    }

    @Test
    void testInstructionDecodeJTypeJr() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400014, "0x03e00008");  // jr $ra
        mips.BIT32_INSTRUCTION = "00000011111000000000000000001000";
        mips.instruction_decode();

        assertEquals("000000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");
        assertEquals("001000", mips.BIT32_INSTRUCTION.substring(26, 32));

        // are the control signals correctly set?
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals(2, mips.get_MAIN_CONTROL_UNIT().PCSrc);
        assertEquals("XX", mips.get_MAIN_CONTROL_UNIT().ALUOp);
        assertEquals("jr", mips.get_MAIN_CONTROL_UNIT().instruction);

        // should be getting the $ra register, correct?
        assertEquals("11111", mips.RS);
        assertEquals("$ra", mips.get_register_from_bit5(mips.RS));

        // what did the ALU yield?
        assertEquals("XXXX", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());
    }

    @Test
    void testWriteBackJTypeJal() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400004, "0x0c100004");  // jal 0x0100004
        mips.BIT32_INSTRUCTION = "00001100000100000000000000000100";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals("$ra", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x00400008, mips.get_register_value_from_bit5("11111"));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$ra")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }


    @Test
    void testMemoryJTypeJal() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400004, "0x0c100004");  // jal 0x0100004
        mips.BIT32_INSTRUCTION = "00001100000100000000000000000100";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
    }

    @Test
    void testExecuteJTypeJal() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400004, "0x0c100004");  // jal 0x0100004
        mips.BIT32_INSTRUCTION = "00001100000100000000000000000100";
        mips.instruction_decode();
        mips.execute();

        assertEquals("$ra", mips.get_REG().WRITE_REGISTER);
        assertEquals(3, mips.get_MAIN_CONTROL_UNIT().PCSrc);
        assertEquals(2, mips.get_MAIN_CONTROL_UNIT().MemtoReg);

        assertEquals(0x00400010, mips.JUMP_ADDRESS);
    }

    @Test
    void testInstructionDecodeJTypeJal() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400014, "0x0c100004");  // jal 0x0100004
        mips.BIT32_INSTRUCTION = "00001100000100000000000000000100";
        mips.instruction_decode();

        assertEquals("000011", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");
        assertEquals("00000100000000000000000100", mips.BIT32_INSTRUCTION.substring(6, 32));

        // are the control signals correctly set?
        assertEquals(2, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(2, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals(3, mips.get_MAIN_CONTROL_UNIT().PCSrc);
        assertEquals("XX", mips.get_MAIN_CONTROL_UNIT().ALUOp);
        assertEquals("jal", mips.get_MAIN_CONTROL_UNIT().instruction);

        // what did the ALU yield?
        assertEquals("XXXX", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());
    }

    @Test
    void testWriteBackITypeAddi() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "212BFFFF"); // addi $t3, $t1, 0xffff
        mips.REGISTERS.put("$t1", 0x00000016);
        mips.BIT32_INSTRUCTION = "00100001001010111111111111111111";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals("$t3", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x00000015, mips.REGISTERS.get("$t3"));
        assertEquals(0x00000015, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RT)));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t3")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryITypeAddi() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "212BFFFF"); // addi $t3, $t1, 0xffff
        mips.REGISTERS.put("$t1", 0x00000016);
        mips.BIT32_INSTRUCTION = "00100001001010111111111111111111";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteITypeAddi() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "218BFFFF"); // addi $t3 $t4 0xffff
        mips.REGISTERS.put("$t4", 0x00000005);
        mips.BIT32_INSTRUCTION = "00100001100010111111111111111111";
        mips.instruction_decode();
        mips.execute();

        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().ALU_CONTROL_SIGNAL);
        assertEquals(-1, Integer.parseInt(mips.IMMEDIATE), "Immediate value parsed incorrectly");
        assertEquals(0x00000004, mips.get_REG().WRITE_DATA);
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
        assertEquals("addi", mips.get_MAIN_CONTROL_UNIT().instruction);

        // what did the ALU yield?
        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000016, mips.get_REG().READ_REGISTER_1, "read_register_1 = " + mips.get_REG().READ_REGISTER_1 + " instead of 0x00000016" );

        // what happens in the regdst_mux()?
        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testWriteBackITypeAddiu() {    // implement later, this was copy and pasted from addi
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "256A0010");  // addiu $t2, $t3, 0x0010
        mips.REGISTERS.put("$t3", 0x00000064);
        mips.BIT32_INSTRUCTION = "00100101011010100000000000010000";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals("$t2", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x00000074, mips.REGISTERS.get("$t2"));
        assertEquals(0x00000074, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RT)));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t2")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryITypeAddiu() {    // implement later, this was copy and pasted from addi
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "256A0010");  // addiu $t2, $t3, 0x0010
        mips.REGISTERS.put("$t3", 0x00000064);
        mips.BIT32_INSTRUCTION = "00100101011010100000000000010000";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteITypeAddiu() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "256A0010");  // addiu $t2, $t3, 0x0010
        mips.REGISTERS.put("$t3", 0x00000064);
        mips.BIT32_INSTRUCTION = "00100101011010100000000000010000";
        mips.instruction_decode();
        mips.execute();

        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().ALU_CONTROL_SIGNAL);
        assertEquals(16, Integer.parseInt(mips.IMMEDIATE), "Immediate value parsed incorrectly");
        assertEquals(0x00000074, mips.get_REG().WRITE_DATA);
    }

    @Test
    void testInstructionDecodeITypeAddiu() {    // li uses addiu instead, REMEMBER THIS! addiu doesn't trap on overflow
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "256A0010");  // addiu $t2, $t3, 0x0010
        mips.REGISTERS.put("$t3", 0x00000064);
        mips.BIT32_INSTRUCTION = "00100101011010100000000000010000";

        mips.instruction_decode();
        assertEquals("001001", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("0000000000010000", mips.BIT32_INSTRUCTION.substring(16, 32), "Immediate has been incorrectly parsed"); // imm

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
        assertEquals(0x00000064, mips.get_REG().READ_REGISTER_1, "read_register_1 = " + mips.get_REG().READ_REGISTER_1 + " instead of 0x00000016" );

        // what happens in the regdst_mux()?
        assertEquals("$t2", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testWriteBackITypeAndi() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "31490AFF"); // andi $t1 $t2 0x0aff
        mips.REGISTERS.put("$t2", 0x00000002);
        mips.BIT32_INSTRUCTION = "00110001010010010000101011111111";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x00000002, mips.REGISTERS.get("$t1"));
        assertEquals(0x00000002, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RT)));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t1")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryITypeAndi() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "31490AFF"); // andi $t1 $t2 0x0aff
        mips.REGISTERS.put("$t2", 0x00000002);
        mips.BIT32_INSTRUCTION = "00110001010010010000101011111111";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteITypeAndi() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "323300FF");  // andi $s3 $s1 0x00ff
        mips.REGISTERS.put("$s1", 0x00000032);
        mips.BIT32_INSTRUCTION = "00110010001100110000000011111111";
        mips.instruction_decode();
        mips.execute();

        assertEquals("0000", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().ALU_CONTROL_SIGNAL);
        assertEquals(255, Integer.parseInt(mips.IMMEDIATE), "Immediate value parsed incorrectly");
        assertEquals(0x00000032, mips.get_REG().WRITE_DATA);
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
    void testWriteBackITypeOri() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "31490AFF"); // ori $s1, $s2, 0x0cca
        mips.REGISTERS.put("$s2", 0x00000008);
        mips.BIT32_INSTRUCTION = "00110110010100010000110011001010";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals("$s1", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x00000CCA, mips.REGISTERS.get("$s1"));
        assertEquals(0x00000CCA, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RT)));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$s1")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryITypeOri() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "31490AFF"); // ori $s1, $s2, 0x0cca
        mips.REGISTERS.put("$s2", 0x00000008);
        mips.BIT32_INSTRUCTION = "00110110010100010000110011001010";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteITypeOri() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "3611000F");  // ori $s1 $s0 0x000f
        mips.REGISTERS.put("$s0", 0x00000128);
        mips.BIT32_INSTRUCTION = "00110110000100010000000000001111";
        mips.instruction_decode();
        mips.execute();

        assertEquals("0001", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().ALU_CONTROL_SIGNAL);
        assertEquals(15, Integer.parseInt(mips.IMMEDIATE), "Immediate value parsed incorrectly");
        assertEquals(0x0000012F, mips.get_REG().WRITE_DATA);
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
    void testWriteBackITypeBeq() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x0040000c, "112A0001");  // beq $t1, $t2, equals or beq $t9, $10, 0x00000001
        mips.REGISTERS.put("$t1", 2);
        mips.REGISTERS.put("$t2", 2);
        mips.BIT32_INSTRUCTION = "00010001001010100000000000000001";
        mips.PC = 0x0040000c;

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);

        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }

        assertEquals(0x00400014, mips.PC);
    }

    @Test
    void testMemoryITypeBeq() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x0040000c, "112A0001");  // beq $t1, $t2, equals or beq $t9, $10, 0x00000001
        mips.REGISTERS.put("$t1", 2);
        mips.REGISTERS.put("$t2", 2);
        mips.BIT32_INSTRUCTION = "00010001001010100000000000000001";

        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteITypeBeq() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x0040000c, "112A0001");  // beq $t1, $t2, equals or beq $t9, $10, 0x00000001
        mips.REGISTERS.put("$t1", 2);
        mips.REGISTERS.put("$t2", 2);
        mips.BIT32_INSTRUCTION = "00010001001010100000000000000001";
        mips.PC = 0x0040000c;

        mips.instruction_decode();
        mips.execute();

        assertEquals(2, mips.get_REG().read_data_1());
        assertEquals(2, mips.get_REG().read_data_2());
        assertEquals(0x00400014, mips.PC);
    }

    @Test
    void testInstructionDecodeITypeBeq() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x0040000c, "112A0001");  // beq $t1, $t2, equals or beq $t9, $10, 0x00000001
        mips.REGISTERS.put("$t1", 2);
        mips.REGISTERS.put("$t2", 2);
        mips.BIT32_INSTRUCTION = "00010001001010100000000000000001";

        mips.instruction_decode();
        assertEquals("000100", mips.BIT32_INSTRUCTION.substring(0, 6));
        assertEquals("0000000000000001", mips.BIT32_INSTRUCTION.substring(16, 32)); // offset

        // are the control signals correctly set?
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().PCSrc);
        assertEquals("01", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0110", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // comparison between rs and rt, what are their values?
        assertEquals(2, mips.get_REG().READ_REGISTER_1);
        assertEquals(2, mips.get_REG().READ_REGISTER_2);
    }

    @Test
    void testWriteBackITypeBne() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x0040000c, "152c0001");  // bne $t1, $t4, equals or bne $t9, $12, 0x00000001
        mips.REGISTERS.put("$t1", 2);
        mips.REGISTERS.put("$t4", 4);
        mips.BIT32_INSTRUCTION = "00010101001011000000000000000001";
        mips.PC = 0x0040000c;

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);

        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }

        assertEquals(0x00400014, mips.PC);
    }

    @Test
    void testMemoryITypeBne() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x0040000c, "152c0001");  // bne $t1, $t4, equals or bne $t9, $12, 0x00000001
        mips.REGISTERS.put("$t1", 2);
        mips.REGISTERS.put("$t4", 4);
        mips.BIT32_INSTRUCTION = "00010101001011000000000000000001";

        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteITypeBne() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x0040000c, "152c0001");  // bne $t1, $t4, equals or bne $t9, $12, 0x00000001
        mips.REGISTERS.put("$t1", 2);
        mips.REGISTERS.put("$t4", 4);
        mips.BIT32_INSTRUCTION = "00010101001011000000000000000001";
        mips.PC = 0x0040000c;

        mips.instruction_decode();
        mips.execute();

        assertEquals(2, mips.get_REG().read_data_1());
        assertEquals(4, mips.get_REG().read_data_2());
        assertEquals(0x00400014, mips.PC);
    }

    @Test
    void testInstructionDecodeITypeBne() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x0040000c, "152c0001");  // bne $t1, $t4, equals or bne $t9, $12, 0x00000001
        mips.REGISTERS.put("$t1", 2);
        mips.REGISTERS.put("$t4", 4);
        mips.BIT32_INSTRUCTION = "00010101001011000000000000000001";

        mips.instruction_decode();
        assertEquals("000101", mips.BIT32_INSTRUCTION.substring(0, 6));
        assertEquals("0000000000000001", mips.BIT32_INSTRUCTION.substring(16, 32)); // offset

        // are the control signals correctly set?
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().PCSrc);
        assertEquals("01", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0110", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // comparison between rs and rt, what are their values?
        assertEquals(2, mips.get_REG().READ_REGISTER_1);
        assertEquals(4, mips.get_REG().READ_REGISTER_2);
    }

    @Test
    void testWriteBackITypeLw() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "8E6C000F"); // lw $t4, 0x000F($s3)
        mips.BIT32_INSTRUCTION = "10001110011011000000000000001111";
        mips.REGISTERS.put("$s3", 0x10010000); //base
        mips.MEMORY_AND_WORDS.put(0x1001000F, "00001234");

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);

        assertEquals(0x1234, mips.REGISTERS.get("$t4"));
        assertEquals("$t4", mips.get_REG().WRITE_REGISTER);

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t4")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryITypeLw() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "8E6C000F"); // lw $t4, 0x000F($s3)
        mips.BIT32_INSTRUCTION = "10001110011011000000000000001111";
        mips.REGISTERS.put("$s3", 0x10010000); //base
        mips.MEMORY_AND_WORDS.put(0x1001000F, "00001234");

        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);

        assertEquals(0x1234, mips.DATA_MEMORY.READ_DATA);
    }

    @Test
    void testExecuteITypeLw() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "8E6C000F"); // lw $t4, 0x000F($s3)
        mips.BIT32_INSTRUCTION = "10001110011011000000000000001111";
        mips.REGISTERS.put("$s3", 0x10010000); //base

        mips.instruction_decode();
        mips.execute();

        assertEquals(0x1001000F, mips.get_REG().WRITE_DATA);
    }

    @Test
    void testInstructionDecodeITypeLw() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "8E6C000F"); // lw $t4, 0x000F($s3)
        mips.BIT32_INSTRUCTION = "10001110011011000000000000001111";
        mips.instruction_decode();  // base address will be $s3, offset added always with rs register, write back in rt reg

        assertEquals("100011", mips.BIT32_INSTRUCTION.substring(0, 6));
        assertEquals("0000000000001111", mips.BIT32_INSTRUCTION.substring(16, 32));
        assertEquals("10011", mips.RS);
        assertEquals("01100", mips.RT);

        // are the control signals correctly set?
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().PCSrc);
        assertEquals("00", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // rs register should have the base
        assertEquals(0, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertEquals("$t4", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testWriteBackITypeSw() {
        mips.testing_mode = true;
        mips.REGISTERS.put("$t0", 0x87654321);
        mips.REGISTERS.put("$t1", 0x10010000);
        mips.MEMORY_AND_WORDS.put(0x10010000, "0x12345678");
        mips.INSTRUCTIONS.put(0x00400010, "0xad280000"); // sw $t0, 0($t1) aka sw $8, 0x00000000($9)
        mips.BIT32_INSTRUCTION = "10101101001010000000000000000000";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.execute();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);

        assertEquals(0x87654321, mips.REGISTERS.get("$t0"));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }

        assertEquals("0x87654321", mips.MEMORY_AND_WORDS.get(0x10010000));
    }

    @Test
    void testMemoryITypeSw() {
        mips.testing_mode = true;
        mips.REGISTERS.put("$t0", 0x87654321);
        mips.REGISTERS.put("$t1", 0x10010000);
        mips.MEMORY_AND_WORDS.put(0x10010000, "0x12345678");
        mips.INSTRUCTIONS.put(0x00400010, "0xad280000"); // sw $t0, 0($t1) aka sw $8, 0x00000000($9)
        mips.BIT32_INSTRUCTION = "10101101001010000000000000000000";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);

        assertEquals(0x10010000, mips.get_DATA_MEMORY().WRITE_ADDRESS);
    }

    @Test
    void testExecuteITypeSw() {
        mips.testing_mode = true;
        mips.REGISTERS.put("$t0", 0x87654321);
        mips.REGISTERS.put("$t1", 0x10010000);
        mips.MEMORY_AND_WORDS.put(0x10010000, "0x12345678");
        mips.INSTRUCTIONS.put(0x00400010, "0xad280000"); // sw $t0, 0($t1) aka sw $8, 0x00000000($9)
        mips.BIT32_INSTRUCTION = "10101101001010000000000000000000";
        mips.instruction_decode();
        mips.execute();

        assertEquals(0x10010000, mips.get_REG().WRITE_DATA);
    }

    @Test
    void testInstructionDecodeITypeSw() {
        mips.testing_mode = true;
        mips.REGISTERS.put("$t0", 0x87654321);
        mips.REGISTERS.put("$t1", 0x10010000);
        mips.MEMORY_AND_WORDS.put(0x10010000, "0x12345678");
        mips.INSTRUCTIONS.put(0x00400010, "0xad280000"); // sw $t0, 0($t1) aka sw $8, 0x00000000($9)
        mips.BIT32_INSTRUCTION = "10101101001010000000000000000000";
        mips.instruction_decode();

        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(-1, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().PCSrc);
        assertEquals("00", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // rs register should have the base
        assertEquals(0x10010000, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");
        assertEquals(0x87654321, mips.get_REG().READ_REGISTER_2, "read_register_2 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertNull(mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testWriteBackRTypeAdd() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "014b4820"); // add $t1, $t2, $t3
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.REGISTERS.put("$t3", 0x00000032);
        mips.BIT32_INSTRUCTION = "00000001010010110100100000100000";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals("$t1", mips.get_REG().WRITE_REGISTER); // was the write_register correctly chosen?
        assertEquals(0x00000048, mips.REGISTERS.get("$t1")); // was the expected register's value correctly modified?
        assertEquals(0x00000048, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RD))); //  was the mips' object itself correctly modified?

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t1")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryRTypeAdd() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "014b4820"); // add $t1, $t2, $t3
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.REGISTERS.put("$t3", 0x00000032);
        mips.BIT32_INSTRUCTION = "00000001010010110100100000100000";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteRTypeAdd() {
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
    void testWriteBackRTypeAnd() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "02114024"); // and $t0, $s0, $s1
        mips.REGISTERS.put("$t0", 0x00000000);
        mips.REGISTERS.put("$s0", 0x00000064);
        mips.REGISTERS.put("$s1", 0x00000012);
        mips.BIT32_INSTRUCTION = "00000010000100010100000000100100";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals("$t0", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x00000000, mips.REGISTERS.get("$t0"));
        assertEquals(0x00000000, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RD)));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryRTypeAnd() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "02114024"); // and $t0, $s0, $s1
        mips.REGISTERS.put("$t0", 0x00000000);
        mips.REGISTERS.put("$s0", 0x00000064);
        mips.REGISTERS.put("$s1", 0x00000012);
        mips.BIT32_INSTRUCTION = "00000010000100010100000000100100";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteRTypeAnd() {
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
    void testWriteBackRTypeOr() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "0272B025"); // or $s6, $s3, $s2
        mips.REGISTERS.put("$s6", 0x00000000);
        mips.REGISTERS.put("$s3", 0x00000128);
        mips.REGISTERS.put("$s2", 0x00000032);
        mips.BIT32_INSTRUCTION = "00000010011100101011000000100101";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals("$s6", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x0000013A, mips.REGISTERS.get("$s6"));
        assertEquals(0x0000013A, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RD)));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$s6")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryRTypeOr() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "0272B025"); // or $s6, $s3, $s2
        mips.REGISTERS.put("$s6", 0x00000000);
        mips.REGISTERS.put("$s3", 0x00000128);
        mips.REGISTERS.put("$s2", 0x0000032);
        mips.BIT32_INSTRUCTION = "00000010011100101011000000100101";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteRTypeOr() {
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
    void testWriteBackRTypeSlt() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "016A702A"); // slt $t6, $t3, $t2
        mips.REGISTERS.put("$t6", 0x00000000);
        mips.REGISTERS.put("$t3", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000008);
        mips.BIT32_INSTRUCTION = "00000001011010100111000000101010";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals("$t6", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x00000001, mips.REGISTERS.get("$t6"));
        assertEquals(0x00000001, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RD)));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t6")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryRTypeSlt() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "016A702A"); // slt $t6, $t3, $t2
        mips.REGISTERS.put("$t6", 0x00000000);        int read_data_1 = mips.get_REG().read_data_1();
        int read_data_2 = mips.get_REG().read_data_2();
        mips.REGISTERS.put("$t3", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000008);
        mips.BIT32_INSTRUCTION = "00000001011010100111000000101010";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteRTypeSlt() {
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
    void testWriteBackRTypeSub() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "01336822"); // sub $t5, $t1, $s3
        mips.REGISTERS.put("$t5", 0x00000000);
        mips.REGISTERS.put("$t1", 0x00000012);
        mips.REGISTERS.put("$s3", 0x00000006);
        mips.BIT32_INSTRUCTION = "00000001001100110110100000100010";

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);

        mips.instruction_decode();
        mips.execute();
        mips.memory();
        mips.write_back();

        assertEquals("$t5", mips.get_REG().WRITE_REGISTER);
        assertEquals(0x0000000C, mips.REGISTERS.get("$t5"));
        assertEquals(0x0000000C, mips.REGISTERS.get(mips.get_register_from_bit5(mips.RD)));

        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t5")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg),
                        "Register " + reg + " was unexpectedly modified");
            }
        }
    }

    @Test
    void testMemoryRTypeSub() {
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400000, "01336822"); // sub $t5, $t1, $s3
        mips.REGISTERS.put("$t5", 0x00000000);
        mips.REGISTERS.put("$t1", 0x00000012);
        mips.REGISTERS.put("$s3", 0x00000006);
        mips.BIT32_INSTRUCTION = "00000001001100110110100000100010";
        mips.instruction_decode();
        mips.execute();
        mips.memory();

        assertEquals(1, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
    }

    @Test
    void testExecuteRTypeSub() {
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
    void testWriteBackRTypeSyscall() {  // not done yet
        mips.testing_mode = true;

    }

    @Test
    void testMemoryRTypeSyscall() {  // not done yet
        mips.testing_mode = true;

    }

    @Test
    void testExecuteRTypeSyscall() {  // not done yet
        mips.testing_mode = true;

    }

    @Test
    void testInstructionDecodeRTypeSyscall() {  // not done yet
        mips.testing_mode = true;
        mips.INSTRUCTIONS.put(0x00400004, "0000000c"); // syscall
        mips.REGISTERS.put("$v0", 10);
        mips.BIT32_INSTRUCTION = "00000000000000000000000000001100";
        mips.instruction_decode();

        assertEquals("000000", mips.BIT32_INSTRUCTION.substring(0, 6), "Opcode has been incorrectly parsed");  // opcode
        assertEquals("001100", mips.BIT32_INSTRUCTION.substring(26, 32), "Funct has been incorrectly parsed");  // funct

        // are the control signals correctly set?
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegDst);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Branch);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemRead);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemtoReg);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().ALUSrc);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().MemWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().RegWrite);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().Jump);
        assertEquals(0, mips.get_MAIN_CONTROL_UNIT().LUICtr);
        assertEquals("XX", mips.get_MAIN_CONTROL_UNIT().ALUOp);

        // what did the ALU yield?
        assertEquals("0110", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // what values do the registers read?
        assertEquals(0x00000016, mips.get_REG().READ_REGISTER_1, "read_register_1 seems to be incorrect");
        assertEquals(0x00000032, mips.get_REG().READ_REGISTER_2, "read_register_2 seems to be incorrect");

        // what happens in the regdst_mux()?
        assertEquals("$t1", mips.get_REG().WRITE_REGISTER);
    }

    @Test
    void testInstructionMemory() {
        mips.testing_mode = true;
        int test_pc = 0x00400000;
        String test_instruction = "01716020";   // add $t4, $t3, $s1
        mips.INSTRUCTIONS.put(test_pc, test_instruction);

        mips.PC = test_pc;
        mips.instruction_memory();

        assertEquals("00000001011100010110000000100000", mips.BIT32_INSTRUCTION,
                "The instruction should be converted to a 32 bit binary string");
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