import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Kernel {
    boolean testing_mode = false;

    void handler(Map<String, Integer> REGISTERS, Map<Integer, String> MEMORY_AND_WORDS) {
        int syscall_code = REGISTERS.get("$v0");

        Set<Integer> valid_syscall_codes = Set.of(1, 4, 5, 10);
        assert valid_syscall_codes.contains(syscall_code);

        switch(syscall_code) {
            case 1: // print integer
                System.out.println(REGISTERS.get("$a0"));
                break;
            case 4: // print string
                handle_print_string(REGISTERS, MEMORY_AND_WORDS);
                break;
            case 5: // read integer
                Scanner scanner = new Scanner(System.in);
                REGISTERS.put("$v0", scanner.nextInt());
                break;
            case 10: // exit
                System.out.println("-- program is finished running --");
                if (!testing_mode) { System.exit(0); }
                break;
            default:
                throw new IllegalArgumentException("Syscall code is not supported");
        }
    }

    void handle_print_string(Map<String, Integer> REGISTERS, Map<Integer, String> MEMORY_AND_WORDS) {
        int memory_address = REGISTERS.get("$a0");
        int mod_result = (memory_address % 4 == 0) ? 0 : 1;
        int address_recalculation = 0;
        boolean isModded = false;

        switch (mod_result) {
            case 0: // if divisible by 4, the memory address can be referenced with no problem
                handle_memory_reading(MEMORY_AND_WORDS, memory_address, isModded, address_recalculation);
                break;
            case 1: // if not divisible by 4, find where the memory address starts, remember, the memory address starts every 4s
                isModded = true;
                int unaligned_memory_address = memory_address;

                while ((unaligned_memory_address % 4) != 0) {
                    unaligned_memory_address--;
                    address_recalculation++;
                }

                handle_memory_reading(MEMORY_AND_WORDS, unaligned_memory_address, isModded, address_recalculation);
                break;
            default:
                throw new IllegalStateException("mod_result seems to have calculated something else");
        }
    }

    void handle_memory_reading(Map<Integer, String> MEMORY_AND_WORDS, int memory_address, boolean isModded, int address_recalculation) {
        StringBuilder unaligned_memory = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        String words_of_2;  // 8 character string represents 2 words or 8 bytes
        boolean isNull = false;

        if (isModded) { memory_address = handle_unaligned_memory_reading(MEMORY_AND_WORDS, memory_address, address_recalculation, unaligned_memory); }

        while (!isNull) {
            StringBuilder hexa_to_deci = new StringBuilder();
            words_of_2 = MEMORY_AND_WORDS.get(memory_address);

            assert words_of_2.length() <= 8 : "words_of_2 has a length of: " + words_of_2.length();

            for (int i = 0; i < words_of_2.length(); i += 2) {  // this for loop extracts in reverse every 2 chars
                String two_chars = words_of_2.substring(i, i + 2);
                hexa_to_deci.insert(0, two_chars);
            }

            for (int i = 0; i < hexa_to_deci.length(); i += 2) {
                String two_chars = hexa_to_deci.substring(i, i + 2);

                if (two_chars.equals("00")) { isNull = true; break; }

                int two_chars_to_decimal = Integer.parseInt(two_chars, 16);  // need this to then convert it to ascii
                String decimal_to_ascii = String.valueOf((char) two_chars_to_decimal);

                ascii.append(decimal_to_ascii);
            }
            memory_address += 4;
        }

        if (isModded) {
            unaligned_memory.append(ascii);
            System.out.println(unaligned_memory);
        }
        else {
            System.out.println(ascii);
        }
    }

    int handle_unaligned_memory_reading(Map<Integer, String> MEMORY_AND_WORDS, int memory_address, int address_recalculation, StringBuilder unaligned_memory) {
        StringBuilder recalculated_hex_to_deci = new StringBuilder();
        String words_of_2 = MEMORY_AND_WORDS.get(memory_address);
        boolean foundNull = false;

        for (int i = 0; i < words_of_2.length(); i+= 2) {
            String two_chars = words_of_2.substring(i, i + 2);
            recalculated_hex_to_deci.insert(0, two_chars);
        }

        for (int k = 0; k < words_of_2.length(); k += 2) {
            String two_chars = recalculated_hex_to_deci.substring(k, k + 2);
            if (two_chars.equals("00")) {
                foundNull = true;
                continue;
            }
            if (foundNull) {
                int two_chars_to_decimal = Integer.parseInt(two_chars, 16);
                String decimal_to_ascii = String.valueOf((char) two_chars_to_decimal);
                unaligned_memory.append(decimal_to_ascii);
            }
        }

        memory_address = memory_address + address_recalculation;
        address_recalculation = 4 - address_recalculation;
        memory_address = memory_address + address_recalculation;
        return memory_address;
    }
}
