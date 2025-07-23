import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IntegrationTest {
    private MIPS mips;

    private final ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
    private final PrintStream original_out = System.out;

    private final ByteArrayInputStream input_stream = new ByteArrayInputStream("42\n".getBytes());
    private final java.io.InputStream original_in = System.in;

    @BeforeEach
    void setUp() { mips = new MIPS(); }

    @AfterEach
    void tearDown() {
        System.setOut(original_out);
        System.setIn(original_in);
    }

    @Test
    void testBasicRTypeInstructions() throws IOException {
        Path text_temp_file = Files.createTempFile("text_test", ".text");
        Files.write(text_temp_file, Arrays.asList(
                "2408000a", // li $t0, 10
                "24090005", // li $t1, 5
                "240a0003", // li $t2, 3
                "240b0001", // li $t3, 1
                "240c001e", // li $t4, 30
                "018b6820", // add $t5, $t4, $t3
                "01887022", // sub $t6, $t4, $t0
                "012c7824", // and $t7, $t1, $t4
                "0149c025", // or $t8, $t2, $t1
                "0168c82a" // slt $t9, $t3, $t0
        ));

        Path data_temp_file = Files.createTempFile("data_test", ".data");
        Files.write(data_temp_file, Arrays.asList(
                "00000000",
                "00000000"
        ));

        mips.testing_one_instruction = true;
        mips.load_memory(String.valueOf(data_temp_file));
        mips.load_instructions(String.valueOf(text_temp_file));

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // li $t0, 10
        assertEquals(0x00400000, mips.PC - 4);
        assertEquals(0x0000000a, mips.REGISTERS.get("$t0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // li $t1, 5
        assertEquals(0x00400004, mips.PC - 4);
        assertEquals(0x00000005, mips.REGISTERS.get("$t1"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t1")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // li $t2, 3
        assertEquals(0x00400008, mips.PC - 4);
        assertEquals(0x00000003, mips.REGISTERS.get("$t2"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t2")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // li $t3, 1
        assertEquals(0x0040000c, mips.PC - 4);
        assertEquals(0x00000001, mips.REGISTERS.get("$t3"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t3")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // li $t4, 30
        assertEquals(0x00400010, mips.PC - 4);
        assertEquals(0x0000001e, mips.REGISTERS.get("$t4"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t4")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // add $t5, $t4, $t3
        assertEquals(0x00400014, mips.PC - 4);
        assertEquals(0x0000001f, mips.REGISTERS.get("$t5"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t5")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // sub $t6, $t4, $t0
        assertEquals(0x00400018, mips.PC - 4);
        assertEquals(0x00000014, mips.REGISTERS.get("$t6"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t6")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // and $t7, $t1, $t4
        assertEquals(0x0040001c, mips.PC - 4);
        assertEquals(0x00000004, mips.REGISTERS.get("$t7"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t7")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // or $t8, $t2, $t1
        assertEquals(0x00400020, mips.PC - 4);
        assertEquals(0x00000007, mips.REGISTERS.get("$t8"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t8")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        mips.instruction_fetch();  // slt $t9, $t3, $t0
        assertEquals(0x00400024, mips.PC - 4);
        assertEquals(0x00000001, mips.REGISTERS.get("$t9"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t9")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }
    }

    @Test
    void testBasicITypeInstructions() throws IOException {
        Path text_temp_file = Files.createTempFile("text_test", ".text");
        Files.write(text_temp_file, Arrays.asList(
                "2408000a", // li $t0, 10
                "24090005", // li $t1, 5
                "240a0003", // li $t2, 3
                "240b0001", // li $t3, 1
                "210c000f", // addi $t4, $t0, 15
                "20010019", // subi $t5, $t1, 25 [addi $1, $0, 0x00000019]
                "01216822", // [sub $13, $9, $1]
                "314e0023", // andi $t6, $t2, 35
                "356f002d" // ori $t7, $t3, 45
        ));

        Path data_temp_file = Files.createTempFile("data_test", ".data");
        Files.write(data_temp_file, Arrays.asList(
                "00000000",
                "00000000"
        ));

        mips.testing_one_instruction = true;
        mips.load_memory(String.valueOf(data_temp_file));
        mips.load_instructions(String.valueOf(text_temp_file));

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400000, mips.PC);
        mips.instruction_fetch();  // li $t0, 10
        assertEquals(0x0000000a, mips.REGISTERS.get("$t0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400004, mips.PC);
        mips.instruction_fetch();  // li $t1, 5
        assertEquals(0x00000005, mips.REGISTERS.get("$t1"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t1")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400008, mips.PC);
        mips.instruction_fetch();  // li $t2, 3
        assertEquals(0x00000003, mips.REGISTERS.get("$t2"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t2")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040000c, mips.PC);
        mips.instruction_fetch();  // li $t3, 1
        assertEquals(0x00000001, mips.REGISTERS.get("$t3"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t3")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400010, mips.PC);
        mips.instruction_fetch();  // addi $t4, $t0, 15
        assertEquals(0x00000019, mips.REGISTERS.get("$t4"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t4")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400014, mips.PC);
        mips.instruction_fetch();  // subi $t5, $t1, 25 [addi $1, $0, 0x00000019]
        assertEquals(0x00000019, mips.REGISTERS.get("$at"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$at")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400018, mips.PC);
        mips.instruction_fetch();  // [sub $13, $9, $1]
        assertEquals(0xffffffec, mips.REGISTERS.get("$t5"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t5")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040001c, mips.PC);
        mips.instruction_fetch();  // andi $t6, $t2, 35
        assertEquals(0x00000003, mips.REGISTERS.get("$t6"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t6")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400020, mips.PC);
        mips.instruction_fetch();  // ori $t7, $t3, 45
        assertEquals(0x0000002d, mips.REGISTERS.get("$t7"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t7")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }
    }

    @Test
    void testEvenOrOdd() throws IOException {
        Path text_temp_file = Files.createTempFile("text_test", ".text");
        Files.write(text_temp_file, Arrays.asList(
                "24020004", // addiu $2, $0, 0x00000004
                "3c011001", // lui $1, 0x00001001
                "34240000", // ori $4, $1, 0x00000000
                "0000000c", // syscall [print string] [Enter your integer: ]
                "24020005", // addiu $2, $0, 0x00000005
                "0000000c", // syscall [read user input] [even or odd integer is inputted, in this case, 42 will be]
                "00026020", // add $12, $0, $2
                "31880001", // andi $8, $12, 0x00000001
                "11000001", // beq $8, $0, 0x00000001 [branches to even label]
                "0810000f", // j 0x0040003c [unconditional jump to odd label]
                "24020004", // addiu $2, $0, 0x00000004 [start of even label]
                "3c011001", // lui $1, 0x00000015
                "34240015", // ori $4, $1, 0x00000015
                "0000000c", // syscall [print string]
                "08100013", // j 0x0040004c [jumps to exit]
                "24020004", // addiu $2, $0, 0x00000004 [start of odd label]
                "3c011001", // lui $1, 0x00001001
                "3424002b", // ori $4, $1, 0x0000002b
                "0000000c", // syscall [print string]
                "2402000a", // addiu $2, $0, 0x0000000a [exit label]
                "0000000c"  // syscall [exits]
        ));

        Path data_temp_file = Files.createTempFile("data_test", ".data");
        Files.write(data_temp_file, Arrays.asList(
                "65746e45", // e t n E
                "6f792072", // o y  r
                "69207275", // i  r u
                "6765746e", // g e t n
                "203a7265", //  : r e
                "756f5900", // u o Y \0
                "6e692072", // n i  r
                "65676574", // e g e t
                "73692072", // s i  r
                "45564520", // E V E
                "5900214e", // Y \0 ! N
                "2072756f", //  r u o
                "65746e69", // e t n i
                "20726567", //  r e g
                "4f207369", // O  s i
                "00214444" // \0 ! D D
        ));

        mips.testing_one_instruction = true;
        mips.KERNEL.testing_mode = true;
        mips.load_memory(String.valueOf(data_temp_file));
        mips.load_instructions(String.valueOf(text_temp_file));

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400000, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x00000004
        assertEquals(0x00000004, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400004, mips.PC);
        mips.instruction_fetch(); // lui $1, 0x00001001
        assertEquals(0x10010000, mips.REGISTERS.get("$at"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$at")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400008, mips.PC);
        mips.instruction_fetch(); // ori $4, $1, 0x00000000
        assertEquals(0x10010000, mips.REGISTERS.get("$a0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$a0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        mips.output_stream_mode = true;
        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040000c, mips.PC);
        mips.instruction_fetch(); // syscall [print string]
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        assertEquals("Enter your integer: \n", output_stream.toString());
        System.setOut(original_out);

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400010, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x00000005
        assertEquals(0x00000005, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }


        String test_input = "42\n";
        System.setIn(new ByteArrayInputStream(test_input.getBytes()));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400014, mips.PC);
        mips.instruction_fetch(); // syscall
        assertEquals(0x0000002a, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }
        System.setIn(original_in);

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400018, mips.PC);
        mips.instruction_fetch(); // add $12, $0, $2
        assertEquals(0x0000002a, mips.REGISTERS.get("$t4"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t4")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040001c, mips.PC);
        mips.instruction_fetch(); // andi $8, $12, 0x00000001
        assertEquals(0x00000000, mips.REGISTERS.get("$t0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400020, mips.PC);
        mips.instruction_fetch(); // beq $8, $0, 0x00000001
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }

        // skips jump because 42 is even
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400028, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x00000004
        assertEquals(0x00000004, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040002c, mips.PC);
        mips.instruction_fetch(); // lui $1, 0x00000015
        assertEquals(0x10010000, mips.REGISTERS.get("$at"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$at")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400030, mips.PC);
        mips.instruction_fetch(); // ori $4, $1, 0x00000015
        assertEquals(0x10010015, mips.REGISTERS.get("$a0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$a0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400034, mips.PC);
        mips.instruction_fetch(); // syscall
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        assertEquals("Enter your integer: \nYour integer is EVEN!\n", output_stream.toString());
        System.setOut(original_out);

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400038, mips.PC);
        mips.instruction_fetch(); // j 0x0040004c
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040004c, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x0000000a
        assertEquals(0x0000000a, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400050, mips.PC);
        mips.instruction_fetch(); // syscall
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        assertEquals("Enter your integer: \nYour integer is EVEN!\n-- program is finished running --\n", output_stream.toString());
        System.setOut(original_out);
    }

    @Test
    void testBeq() throws IOException {
        Path text_temp_file = Files.createTempFile("text_test", ".text");
        Files.write(text_temp_file, Arrays.asList(
                "2408002a", // addiu $8, $0, 0x0000002a
                "2409002a", // addiu $9, $0, 0x0000002a
                "11090001", // beq $8, $9, 0x00000001
                "08100008", // j 0x00400020
                "24020004", // addiu $2, $0, 0x00000004
                "3c011001", // lui $1, 0x00001001
                "34240000", // ori $4, $1, 0x00000000
                "0000000c", // syscall [print string]
                "2402000a", // addiu $2, $0, 0x0000000a
                "0000000c" // syscall [exit]
        ));

        Path data_temp_file = Files.createTempFile("data_test", ".data");
        Files.write(data_temp_file, Arrays.asList(
                "3a514542", // : Q E B
                "61724220", // a r B
                "2068636e", //   h c n
                "656b6174", // e k a t
                "00000a6e"  // \0 \0 \n n
        ));

        mips.testing_one_instruction = true;
        mips.KERNEL.testing_mode = true;
        mips.load_memory(String.valueOf(data_temp_file));
        mips.load_instructions(String.valueOf(text_temp_file));

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400000, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x00000004
        assertEquals(0x0000002a, mips.REGISTERS.get("$t0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400004, mips.PC);
        mips.instruction_fetch(); // addiu $9, $0, 0x0000002a
        assertEquals(0x0000002a, mips.REGISTERS.get("$t1"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t1")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400008, mips.PC);
        mips.instruction_fetch(); // beq $8, $9, 0x00000001
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }

        // skip jump instruction
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400010, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x00000004
        assertEquals(0x00000004, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400014, mips.PC);
        mips.instruction_fetch(); // lui $1, 0x00001001
        assertEquals(0x10010000, mips.REGISTERS.get("$at"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$at")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400018, mips.PC);
        mips.instruction_fetch(); // ori $4, $1, 0x00000000
        assertEquals(0x10010000, mips.REGISTERS.get("$a0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$a0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        mips.output_stream_mode = true;
        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040001c, mips.PC);
        mips.instruction_fetch(); // syscall [print string]
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        assertEquals("BEQ: Branch taken\n\n", output_stream.toString());
        System.setOut(original_out);

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400020, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x0000000a
        assertEquals(0x0000000a, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        mips.output_stream_mode = true;
        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400024, mips.PC);
        mips.instruction_fetch(); // syscall [exit]
        assertEquals(0x0000000a, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        assertEquals("BEQ: Branch taken\n\n-- program is finished running --\n", output_stream.toString());
        System.setOut(original_out);
    }

    @Test
    void testBne() throws IOException {
        Path text_temp_file = Files.createTempFile("text_test", ".text");
        Files.write(text_temp_file, Arrays.asList(
                "2408002a", // addiu $8, $0, 0x0000002a
                "2409000d", // addiu $9, $0, 0x0000000d
                "15090001", // bne $8, $9, 0x00000001
                "08100008", // j 0x00400020
                "24020004", // addiu $2, $0, 0x00000004
                "3c011001", // lui $1, 0x00001001
                "34240000", // ori $4, $1, 0x00000000
                "0000000c", // syscall [print string]
                "2402000a", // addiu $2, $0, 0x0000000a
                "0000000c" // syscall [exit]
        ));

        Path data_temp_file = Files.createTempFile("data_test", ".data");
        Files.write(data_temp_file, Arrays.asList(
                "3a454e42", // : E N B
                "61724220", // a r B
                "2068636e", //   h c n
                "656b6174", // e k a t
                "00000a6e"  // \0 \0 \n n
        ));

        mips.testing_one_instruction = true;
        mips.KERNEL.testing_mode = true;
        mips.load_memory(String.valueOf(data_temp_file));
        mips.load_instructions(String.valueOf(text_temp_file));

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400000, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x00000004
        assertEquals(0x0000002a, mips.REGISTERS.get("$t0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400004, mips.PC);
        mips.instruction_fetch(); // addiu $9, $0, 0x0000002a
        assertEquals(0x0000000d, mips.REGISTERS.get("$t1"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$t1")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400008, mips.PC);
        mips.instruction_fetch(); // beq $8, $9, 0x00000001
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }

        // skip jump instruction
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400010, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x00000004
        assertEquals(0x00000004, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400014, mips.PC);
        mips.instruction_fetch(); // lui $1, 0x00001001
        assertEquals(0x10010000, mips.REGISTERS.get("$at"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$at")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400018, mips.PC);
        mips.instruction_fetch(); // ori $4, $1, 0x00000000
        assertEquals(0x10010000, mips.REGISTERS.get("$a0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$a0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        mips.output_stream_mode = true;
        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040001c, mips.PC);
        mips.instruction_fetch(); // syscall [print string]
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        assertEquals("BNE: Branch taken\n\n", output_stream.toString());
        System.setOut(original_out);

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400020, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x0000000a
        assertEquals(0x0000000a, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        mips.output_stream_mode = true;
        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400024, mips.PC);
        mips.instruction_fetch(); // syscall [exit]
        assertEquals(0x0000000a, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        assertEquals("BNE: Branch taken\n\n-- program is finished running --\n", output_stream.toString());
        System.setOut(original_out);
    }

    @Test
    void testJalJr() throws IOException {
        Path text_temp_file = Files.createTempFile("text_test", ".text");
        Files.write(text_temp_file, Arrays.asList(
                "0c100007", // jal 0x0040001c
                "24020004", // addiu $2, $0, 0x00000004
                "3c011001", // lui $1, 0x00001001
                "34240014", // ori $4, $1, 0x00000014
                "0000000c", // syscall [print string]
                "2402000a", // addiu $2, $0, 0x0000000a
                "0000000c", // syscall [exit]
                "24020004", // addiu $2, $0, 0x0000004
                "3c011001", // lui $1, 0x00001001
                "34240000", // ori $4, $1, 0x00000000
                "0000000c", // syscall
                "03e00008"  // jr $31
        ));

        Path data_temp_file = Files.createTempFile("data_test", ".data");
        Files.write(data_temp_file, Arrays.asList(
                "65746e45", // e t n E
                "20646572", //   d e r
                "204c414a", //   L A J
                "67726174", // g r a t
                "000a7465", // \0 \n t e
                "75746552", // u t e R
                "64656e72", // d e n r
                "6f726620", // o r f
                "414a206d", // A J   m
                "00000a4c"  // \0 \0 \n L
        ));

        mips.testing_one_instruction = true;
        mips.output_stream_mode = true;
        mips.KERNEL.testing_mode = true;
        mips.load_memory(String.valueOf(data_temp_file));
        mips.load_instructions(String.valueOf(text_temp_file));

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400000, mips.PC);
        mips.instruction_fetch(); // jal 0x0040001c
        assertEquals(0x00400004, mips.REGISTERS.get("$ra"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$ra")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040001c, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x00000004
        assertEquals(0x00000004, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400020, mips.PC);
        mips.instruction_fetch(); // lui $1, 0x00001001
        assertEquals(0x10010000, mips.REGISTERS.get("$at"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$at")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400024, mips.PC);
        mips.instruction_fetch(); // ori $4, $1, 0x00000000
        assertEquals(0x10010000, mips.REGISTERS.get("$a0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$a0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400028, mips.PC);
        mips.instruction_fetch(); // syscall [print string]
        assertEquals("Entered JAL target\n\n", output_stream.toString());
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        System.setOut(new PrintStream(original_out));

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040002c, mips.PC);
        mips.instruction_fetch();   // jr $31
        assertEquals(0x00400004, mips.REGISTERS.get("$ra"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$ra")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400004, mips.PC);
        mips.instruction_fetch();   // addiu $2, $0, 0x00000004
        assertEquals(0x00000004, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400008, mips.PC);
        mips.instruction_fetch(); // lui $1, 0x00001001
        assertEquals(0x10010000, mips.REGISTERS.get("$at"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$at")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x0040000c, mips.PC);
        mips.instruction_fetch(); // ori $4, $1, 0x00000014
        assertEquals(0x10010014, mips.REGISTERS.get("$a0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$a0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400010, mips.PC);
        mips.instruction_fetch(); // syscall [print string]
        assertEquals("Entered JAL target\n\nReturned from JAL\n\n", output_stream.toString());
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        System.setOut(new PrintStream(original_out));

        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400014, mips.PC);
        mips.instruction_fetch(); // addiu $2, $0, 0x0000000a
        assertEquals(0x0000000a, mips.REGISTERS.get("$v0"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$v0")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }

        System.setOut(new PrintStream(output_stream));
        initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400018, mips.PC);
        mips.instruction_fetch(); // syscall [exit]
        assertEquals("Entered JAL target\n\nReturned from JAL\n\n-- program is finished running --\n", output_stream.toString());
        for (String reg : initial_registers.keySet()) {
            assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
        }
        System.setOut(new PrintStream(original_out));
    }

    @Test
    void testLwSw() throws IOException {
        Path text_temp_file = Files.createTempFile("text_test", ".text");
        Files.write(text_temp_file, Arrays.asList(
                "3c011234", // lui $1, 0x00001234
                "34285678", // ori $8, $1, 0x00005678
                "3c011001", // lui $1, 0x00001001
                "34290000", // ori $9, $1, 0x00000000
                "ad280000", // sw $8, 0x00000000($9)
                "24080000", // addiu $8, $0, 0x00000000
                "8d280000", // lw $8, 0x00000000($9)
                "24020001", // addiu $2, $0, 0x0000001
                "01002020", // add $4, $8, $0
                "0000000c", // syscall [print int]
                "2402000a", // addiu $2, $0, 0x0000000a
                "0000000c"  // syscall [exit]
        ));

        Path data_temp_file = Files.createTempFile("data_test", ".data");
        Files.write(data_temp_file, Arrays.asList(
                "00000000",
                "00000000"
        ));

        mips.testing_one_instruction = true;
        mips.output_stream_mode = true;
        mips.KERNEL.testing_mode = true;
        mips.load_memory(String.valueOf(data_temp_file));
        mips.load_instructions(String.valueOf(text_temp_file));

        Map<String, Integer> initial_registers = new HashMap<>(mips.REGISTERS);
        assertEquals(0x00400000, mips.PC);
        mips.instruction_fetch(); // lui $1, 0x00001234
        assertEquals(0x00400004, mips.REGISTERS.get("$ra"));
        for (String reg : initial_registers.keySet()) {
            if (!reg.equals("$ra")) {
                assertEquals(initial_registers.get(reg), mips.REGISTERS.get(reg));
            }
        }
    }
}
