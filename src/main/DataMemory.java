import java.util.Map;

public class DataMemory {
    int READ_ADDRESS;
    int WRITE_ADDRESS;
    int WRITE_DATA;
    int READ_DATA;

    public DataMemory() {
        this.READ_ADDRESS = 0;
        this.WRITE_ADDRESS = 0;
        this.WRITE_DATA = 0;
        this.READ_DATA = 0;
    }

    void read_address(int alu_result) {
        assert alu_result >= 0x10010000 : "Calculated alu result is below memory start address: 0x10010000";

        this.READ_ADDRESS = alu_result;
    }

    void read_data(Map<Integer, String> MEMORY_AND_WORDS) {
        assert MEMORY_AND_WORDS.containsKey(this.READ_ADDRESS) : "This address doesn't exist in MEMORY_AND_WORDS";

        StringBuilder dec_to_hex = new StringBuilder();

        if (MEMORY_AND_WORDS.containsKey(this.READ_ADDRESS)) {
            String word = MEMORY_AND_WORDS.get(this.READ_ADDRESS);
            assert word.length() == 8 : "Word is not 4 bytes";  // 8 hex digits, 32 bits in total

            for (int i = 0; i < word.length(); i += 2) {
                String two_chars = word.substring(i, i + 2);
                dec_to_hex.append(two_chars);
                if (i > 8) { throw new IllegalArgumentException("Memory word must be 4 bytes"); }
            }

            String hex_string = dec_to_hex.toString();
            this.READ_DATA = Integer.parseInt(hex_string, 16);
        }
        else {
            throw new IllegalArgumentException("Address doesn't exist in memory");
        }
    }

    void write_address(int alu_result) {
        assert alu_result >= 10010000 : "Calculated alu result is below memory start address: 0x10010000";

        this.WRITE_ADDRESS = alu_result;
    }

    void write_data(int rt_val, Map<Integer, String> MEMORY_AND_WORDS) {
        this.READ_DATA = rt_val;
        String READ_DATA_str_conversion = String.format("0x%08X", this.READ_DATA);
        MEMORY_AND_WORDS.put(this.WRITE_ADDRESS, READ_DATA_str_conversion);
    }
}