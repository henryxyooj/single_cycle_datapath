import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class IntegrationTest {
    private MIPS mips;

    @BeforeEach
    void setUp() {
        mips = new MIPS();
    }

    @Test
    void testRTypeAddMultipleInstruction() throws IOException {
        Path temp_file = Files.createTempFile("test_instructions", ".text");
        Files.write(temp_file, Arrays.asList(
                "014b4820", // add $t1, $t2, $t3
                "012b5020", // add $t2, $t1, $t3
                "016A6020" // add $t4, $t3, $t2
        ));

        // are the instructions stored in the correct address in the INSTRUCTIONS hashmap?
        // incremented correctly too?
        mips.load_instructions(temp_file.toString());
        mips.REGISTERS.put("$t1", 0x00000004);
        mips.REGISTERS.put("$t2", 0x00000016);
        mips.REGISTERS.put("$t3", 0x00000032);

        mips.program_counter();

        // instruction_memory()
        // read_address() correctly?
        // check conversion?

        // instruction_decode()
        // did it decode the fields correctly?
        assertEquals("00000", mips.BIT32_INSTRUCTION.substring(0, 6));  // opcode
        assertEquals("00000", mips.BIT32_INSTRUCTION.substring(6, 11));  // rs
        assertEquals("00000", mips.BIT32_INSTRUCTION.substring(11, 16));  // rt
        assertEquals("00000", mips.BIT32_INSTRUCTION.substring(16, 21));  // rd
        assertEquals("00000", mips.BIT32_INSTRUCTION.substring(16, 32));  // immediate
        assertEquals("00000", mips.BIT32_INSTRUCTION.substring(26, 32));  // funct

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

        // are the ALU control signals correct?  what alu action will i get?
        assertEquals("0010", mips.get_MAIN_CONTROL_UNIT().get_ALU_CONTROL_UNIT().get_ALU_control_signal());

        // are the registers read correctly?  datawise?

        // execute()
        //

        // final check for that pc and the current register's state/values
    }
}
