import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DataMemoryTest {
    private DataMemory dm;

    @BeforeEach
    void setUp() {
        dm = new DataMemory();
    }

    @Test
    void testReadAddress() {
        dm.read_address(0x1001000F);
        assertEquals(0x1001000F, dm.READ_ADDRESS, "read_address() is incorrectly taking in alu_result");
    }

    @Test
    void testReadData() {
        Map<Integer, String> MEMORY_AND_WORDS = new HashMap<>();
        MEMORY_AND_WORDS.put(0x1001000F, "00001234");

        dm.read_address(0x1001000F);
        dm.read_data(MEMORY_AND_WORDS);
        assertEquals(0x1234, dm.READ_DATA);
    }
}
