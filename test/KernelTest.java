import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KernelTest {
    Kernel krnl;
    Map<Integer, String> MEMORY_AND_WORDS;
    Map<String, Integer> REGISTERS;

    boolean testing_mode = false;

    // capturing System.out
    private final ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
    private final PrintStream original_out = System.out;

    // providing System.in input
    private final ByteArrayInputStream input_stream = new ByteArrayInputStream("42\n".getBytes());
    private final java.io.InputStream original_in = System.in;

    @BeforeEach
    void setUp() {
        krnl = new Kernel();

        MEMORY_AND_WORDS = new HashMap<>();
        // Hello, world!
        MEMORY_AND_WORDS.put(0x10010000, "6c6c6548");  // lleH
        MEMORY_AND_WORDS.put(0x10010004, "77202c6f");   // w ,o
        MEMORY_AND_WORDS.put(0x10010008, "646c726f");   // dlro
        MEMORY_AND_WORDS.put(0x1001000C, "6e550021");   // nU \0 !
        MEMORY_AND_WORDS.put(0x10010010, "67696c61"); // gila
        MEMORY_AND_WORDS.put(0x10010014, "2064656e"); //  den
        MEMORY_AND_WORDS.put(0x10010018, "64726f77"); // drow
        MEMORY_AND_WORDS.put(0x1001001C, "00000000"); // \0

        REGISTERS = new HashMap<>();
        REGISTERS.put("$v0", 0x00000000);
        REGISTERS.put("$a0", 0x00000000);
        REGISTERS.put("$a1", 0x00000000);
        REGISTERS.put("$a2", 0x00000000);
        REGISTERS.put("$a3", 0x00000000);
        REGISTERS.put("$t0", 0x00000000);
        REGISTERS.put("$t1", 0x00000000);

        // redirect System.out to capture output
        System.setOut(new PrintStream(output_stream));
    }

    @AfterEach
    void tearDown() {
        // restores original System.out and System.in
        System.setOut(original_out);
        System.setIn(original_in);
    }

    @Test
    void testHandlerPrintInteger() {
        REGISTERS.put("$v0", 1);
        REGISTERS.put("$a0", 15);

        krnl.handler(REGISTERS, MEMORY_AND_WORDS);

        String output = output_stream.toString().trim();
        assertEquals("15", output);
    }

    @Test
    void testHandlerPrintString() {
        REGISTERS.put("$v0", 4);
        REGISTERS.put("$a0", 0x10010000);

        krnl.handler(REGISTERS, MEMORY_AND_WORDS);

        String output = output_stream.toString();
        assertEquals("Hello, world!\n", output, "Not outputting correct string");
    }

    @Test
    void testHandlerPrintStringUnalignedWord() {
        testing_mode = true;
        REGISTERS.put("$v0", 4);
        REGISTERS.put("$a0", 0x1001000E);

        krnl.handler(REGISTERS, MEMORY_AND_WORDS);

        String output = output_stream.toString();
        assertEquals("Unaligned word\n", output, "Not outputting correct string");
    }

    @Test
    void testHandleMemoryReading() {
        REGISTERS.put("$v0", 4);

        int memory_address = 0x10010000;
        int address_recalculation = 0;
        boolean isModded = false;

        krnl.handle_memory_reading(MEMORY_AND_WORDS, memory_address, isModded, address_recalculation);

        String output = output_stream.toString();
        assertEquals("Hello, world!\n", output, "Not outputting correct string");
    }

    @Test
    void testHandlerReadInteger() {
        REGISTERS.put("$v0", 5);
        krnl.testing_mode = true;

        System.setIn(new ByteArrayInputStream("42\n".getBytes()));

        krnl.handler(REGISTERS, MEMORY_AND_WORDS);

        assertEquals(42, REGISTERS.get("$v0").intValue());
    }

    @Test
    void testHandlerExit() {
        REGISTERS.put("$v0", 10);
        krnl.testing_mode = true;
        krnl.handler(REGISTERS, MEMORY_AND_WORDS);

        String output = output_stream.toString().trim();
        assertEquals("-- program is finished running --", output);
    }
}
