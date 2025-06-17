import java.io.IOException;

public class driver {
    public static void main(String[] args) throws IOException {

        String data_file = "C:\\Users\\Henry\\IdeaProjects\\single_cycle_datapath\\src\\basic_instructions.data";
        String text_file = "C:\\Users\\Henry\\IdeaProjects\\single_cycle_datapath\\src\\jump.text";

        MIPS isa = new MIPS();
        start_cycle(isa, data_file, text_file);
    }

    public static void start_cycle(MIPS isa, String data_file, String text_file) throws IOException {
        isa.load_memory(data_file);
        isa.load_instructions(text_file);

        isa.program_counter();
    }
}
