import java.io.IOException;

public class driver {
    public static void main(String[] args) throws IOException {

        String data_file = "/home/henry-xiong/IdeaProjects/single_cycle_datapath/src/asm_text_data_files/EvenOrOdd.data";
        String text_file = "/home/henry-xiong/IdeaProjects/single_cycle_datapath/src/asm_text_data_files/basic_rtype_test.text";

        MIPS isa = new MIPS();
        start_cycle(isa, data_file, text_file);
    }

    public static void start_cycle(MIPS isa, String data_file, String text_file) throws IOException {
        isa.load_memory(data_file);
        isa.load_instructions(text_file);

        isa.program_counter();
    }
}
