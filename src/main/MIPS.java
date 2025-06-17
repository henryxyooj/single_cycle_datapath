import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class MIPS {
    private static final Logger logger = Logger.getLogger(MIPS.class.getName());
    static {
        // OFF, INFO, WARNING, SEVERE
        //logger.setLevel(Level.OFF);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LoggerFormatter());
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }
    boolean testing_mode = false;

    static final int DATA_START_ADDRESS = 0x10010000;
    static final int TEXT_START_ADDRESS = 0x00400000;

    final MainControlUnit MAIN_CONTROL_UNIT;
    final Registers REG;

    final Map<String, Integer> REGISTERS;
    final Map<Integer, String> INSTRUCTIONS;
    final Map<Integer, String> PC_AND_REGISTERS;
    final Map<Integer, String> MEMORY_AND_WORDS;

    int PC;
    int MEMORY;
    String BIT32_INSTRUCTION;
    String OPCODE;
    String FUNCT;
    String RS;
    String RT;
    String RD;
    String IMMEDIATE;
    int OFFSET;
    String TARGET;
    int JUMP_ADDRESS;

    public MIPS() {
        this.REGISTERS = new HashMap<>();
        REGISTERS.put("$zero", 0x00000000); REGISTERS.put("$at", 0x00000000);
        REGISTERS.put("$v0", 0x00000000); REGISTERS.put("$v1", 0x00000000);
        REGISTERS.put("$a0", 0x00000000); REGISTERS.put("$a1", 0x00000000);
        REGISTERS.put("$a2", 0x00000000); REGISTERS.put("$a3", 0x00000000);
        REGISTERS.put("$t0", 0x00000000); REGISTERS.put("$t1", 0x00000000);
        REGISTERS.put("$t2", 0x00000000); REGISTERS.put("$t3", 0x00000000);
        REGISTERS.put("$t4", 0x00000000); REGISTERS.put("$t5", 0x00000000);
        REGISTERS.put("$t6", 0x00000000); REGISTERS.put("$t7", 0x00000000);
        REGISTERS.put("$s0", 0x00000000); REGISTERS.put("$s1", 0x00000000);
        REGISTERS.put("$s2", 0x00000000); REGISTERS.put("$s3", 0x00000000);
        REGISTERS.put("$s4", 0x00000000); REGISTERS.put("$s5", 0x00000000);
        REGISTERS.put("$s6", 0x00000000); REGISTERS.put("$s7", 0x00000000);
        REGISTERS.put("$t8", 0x00000000); REGISTERS.put("$t9", 0x00000000);
        REGISTERS.put("$k0", 0x00000000); REGISTERS.put("$k1", 0x00000000);
        REGISTERS.put("$gp", 0x10008000); REGISTERS.put("$sp", 0x7fffeffc);
        REGISTERS.put("$fp", 0x00000000); REGISTERS.put("$ra", 0x00000000);

        this.MAIN_CONTROL_UNIT = new MainControlUnit();
        this.REG = new Registers();

        this.MEMORY = DATA_START_ADDRESS;
        this.MEMORY_AND_WORDS = new HashMap<>();    //(MEM ADDRESS [0X1000100], WORD [64e50054])

        this.PC = TEXT_START_ADDRESS;
        this.INSTRUCTIONS = new HashMap<>();

        this.PC_AND_REGISTERS = new HashMap<>();    // (PC [0x00400000], REGISTERS [ALL OF THEM])

        BIT32_INSTRUCTION = null;
        OPCODE = null;
        FUNCT = null;
        RS = null;
        RT = null;
        RD = null;
        IMMEDIATE = null;
        OFFSET = 0;
        TARGET = null;
        JUMP_ADDRESS = 0;
    }

    void write_back() {
        if (get_MAIN_CONTROL_UNIT().RegWrite == 1) {
            set_register_value_with_bit5(Mappings.REG_TO_BIT5.get(get_REG().WRITE_REGISTER), get_REG().WRITE_DATA);
            logger.info("storing into hashmap: reg: " + get_REG().WRITE_REGISTER + ", val: " + get_REG().WRITE_DATA);
            this.PC_AND_REGISTERS.put(this.PC, this.REGISTERS.toString());
            print_pc_and_registers();  // prints out before pc + 4 and the registers
        }
        else {
            logger.info("write back skipped, RegWrite = 0");
        }
    }

    void memory() {
        if (get_MAIN_CONTROL_UNIT().MemWrite == 0 && get_MAIN_CONTROL_UNIT().MemRead == 0) {
            memtoreg_mux();
        }
        else {
            logger.info("unsupported operations for now");
        }
    }

    void memtoreg_mux() {
        if (get_MAIN_CONTROL_UNIT().MemtoReg == 0) {
            write_back();
        }
        else {
            logger.info("unspported oeprations for now");
        }
    }

    void execute() {
        assert get_ALU().get_ALU_control_signal() != null : "ALU control signal shouldn't be null";

        get_ALU().set_ALU_control_signals(get_MAIN_CONTROL_UNIT().ALUOp, this.OPCODE, this.FUNCT);
        logger.info("ALU operation control signal: " + get_ALU().get_ALU_control_signal() + " = " + get_MAIN_CONTROL_UNIT().instruction);

        int alu_result = 0;
        int read_data_1 = get_REG().read_data_1();
        int read_data_2 = alusrc_mux();
        logger.info("read_data_1 (RS): " + read_data_1);
        logger.info("read_data_2 (Immediate or RT): " + read_data_2);

        switch(get_ALU().get_ALU_control_signal()) {
            case "0010":    // addition
                alu_result = read_data_1 + read_data_2;
                logger.info("updated [add] register value: " + alu_result);
                break;
            case "0110":    // subtraction
                alu_result = read_data_1 - read_data_2;
                logger.info("updated [sub] register value: " + alu_result);
                break;
            case "0000":    // and
                alu_result = read_data_1 & read_data_2;
                logger.info("updated [and] register value: " + alu_result);
                break;
            case "0001":    // or
                alu_result = read_data_1 | read_data_2;
                logger.info("updated [or] register value: " + alu_result);
                break;
            case "0111":    // slt
                alu_result = read_data_1 < read_data_2 ? 1 : 0;
                logger.info("updated [slt] register value: " + alu_result);
                break;
            default:
                logger.info("unsupported ALU operation");
                break;
        }

        get_REG().write_data(alu_result);
        logger.info("Updating " + get_REG().WRITE_REGISTER + " with new value: " + alu_result);

        if (testing_mode) { return; }
        memory();
    }

    // ALUSrc is going to select "read data 2" for immediate or the second source register RT
    int alusrc_mux() {
        if (get_MAIN_CONTROL_UNIT().ALUSrc == 0) {  // r type
            return get_REG().read_data_2();
        }
        else {  // i type
            return Integer.parseInt(this.IMMEDIATE);
        }
    }

    void instruction_decode() {
        assert this.BIT32_INSTRUCTION != null : "instruction_31_0 shouldn't be null";
        assert this.BIT32_INSTRUCTION.length() == 32 : "instruction_31_0 length is incorrect";

        // decode the opcode, funct, and immediate
        this.OPCODE = this.BIT32_INSTRUCTION.substring(0, 6);
        this.FUNCT = this.BIT32_INSTRUCTION.substring(26, 32);
        this.IMMEDIATE = this.BIT32_INSTRUCTION.substring(16, 32);
        logger.info("retrieved opcode: " + this.OPCODE);
        logger.info("retrieved funct: " + this.FUNCT);
        logger.info("retrieved immediate: " + this.IMMEDIATE);

        get_MAIN_CONTROL_UNIT().set_control_signal(this.OPCODE, this.FUNCT);

        if (get_MAIN_CONTROL_UNIT().Jump == 1) {
            this.TARGET = this.BIT32_INSTRUCTION.substring(4, 32);
            logger.info("retrieved target for jump : " + this.TARGET);
            jump_mux();
            return;
        }

        this.RS = this.BIT32_INSTRUCTION.substring(6, 11);
        get_REG().read_register_1(get_register_value_from_bit5(this.RS));
        regdst_mux();

        if (get_MAIN_CONTROL_UNIT().ALUSrc == 1) {
            sign_extend();
        }

        if (get_MAIN_CONTROL_UNIT().LUICtr == 1) {
            luictr_mux();
            return;
        }

        if (testing_mode) { return; }
        execute();
    }

    void jump_mux() {
        logger.info("Entered jump_mux with 26 bits: " + this.TARGET);
        int target = Integer.parseInt(this.TARGET, 2);
        target = target << 2;

        int upper_bits = (this.PC + 4) & 0xF0000000;
        int jump_address = upper_bits | target;



    }

    void luictr_mux() {
        if (get_MAIN_CONTROL_UNIT().LUICtr == 1) {
            int imm_shifted_16bits = Integer.parseInt(this.IMMEDIATE, 2) << 16;
            get_REG().write_data(imm_shifted_16bits);

            if (testing_mode) { return; }
            write_back();
        }
    }

    void sign_extend() {
        int immediate = Integer.parseInt(this.IMMEDIATE, 2);
        logger.info("parsing bit16: " + this.IMMEDIATE + " to immediate: " + immediate);

        if ((immediate & 0x8000) != 0) {
            logger.info("immediate neg extended: " + (immediate | 0xFFFF0000));
            this.IMMEDIATE = String.valueOf(immediate | 0xFFFF0000);
        }
        else {
            logger.info("immediate pos extended: " + immediate);
            this.IMMEDIATE = String.valueOf(immediate);
        }
    }

    void regdst_mux() {
        this.RT = this.BIT32_INSTRUCTION.substring(11, 16); // instruction [20-16]
        this.RD = this.BIT32_INSTRUCTION.substring(16, 21); // instruction [15-11]

        if (get_MAIN_CONTROL_UNIT().RegDst == 1) {
            get_REG().read_register_2(get_register_value_from_bit5(this.RT));
            get_REG().write_register(get_register_from_bit5(this.RD));
            logger.info("read register 2 retrieved: " + get_register_from_bit5(this.RT));
            logger.info("rtype with destination register (RD): " + get_register_from_bit5(this.RD));
        }
        else if (get_MAIN_CONTROL_UNIT().RegDst == 0){
            get_REG().write_register(get_register_from_bit5(this.RT));
            logger.info("itype with destination register (RT): " + get_register_from_bit5(this.RT));
        }
    }

    void instruction_memory() {
        String mips_instruction = read_address();
        StringBuilder instruction_in_bit32 = new StringBuilder();

        for (int curr_char_ind = 0; curr_char_ind < mips_instruction.length(); curr_char_ind++) {
            String curr_hex_char = Character.toString(mips_instruction.charAt(curr_char_ind));

            if (Mappings.HEX_TO_BIT4.containsKey(curr_hex_char)) {
                String bit4_conversion_from_hex = Mappings.HEX_TO_BIT4.get(curr_hex_char);
                instruction_in_bit32.append(bit4_conversion_from_hex);
            }
        }

        this.BIT32_INSTRUCTION = instruction_in_bit32.toString();
        logger.info("instruction_31_0: " + this.BIT32_INSTRUCTION);

        instruction_decode();
    }

    void program_counter() {
        while (this.PC < this.INSTRUCTIONS.size() * 4 + TEXT_START_ADDRESS) {
            program_counter_adder();
            instruction_memory();
        }
        System.out.println("-- program finished running (dropped off bottom) --");
    }

    void program_counter_adder() {
        if (get_MAIN_CONTROL_UNIT().Jump == 1) {
            //
            //jump_mux();
        }
        else {
            this.PC += 4;
            //jump_mux();
        }
    }

    public void load_memory(String data_file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(data_file))) {
            String currline;
            while ((currline = reader.readLine()) != null && !currline.equals("00000000")) {
                if (!currline.matches("^[a-f0-9A-F]{8}$")) {
                    throw new IllegalArgumentException("Invalid memory format: " + currline);
                }
                // stores the memory address with it's associated value: (0x10010000, 0x65746e45)
                this.MEMORY_AND_WORDS.put(this.MEMORY, currline);
                this.MEMORY += 4;
            }
        }
    }

    public void load_instructions(String text_file) throws IOException {
        assert text_file != null : "text_file is null";

        try (BufferedReader reader = new BufferedReader(new FileReader(text_file))) {
            String line;
            int address = TEXT_START_ADDRESS;
            while ((line = reader.readLine()) != null) {
                if (!line.matches("^[0-9a-fA-F]{8}$")) {
                    throw new IllegalArgumentException("Invalid instruction format: " + line);
                }
                // stores the address with it's associated value: (4194304, 24020004)
                this.INSTRUCTIONS.put(address, line);
                address += 4;
            }
        }
    }

    Registers get_REG() { return this.REG; }
    MainControlUnit get_MAIN_CONTROL_UNIT() { return this.MAIN_CONTROL_UNIT; }
    ALUControlUnit get_ALU() { return this.MAIN_CONTROL_UNIT.ALU_CONTROL; }

    void set_register_value_with_bit5(String register_in_bit5, int updated_register_value) { this.REGISTERS.put(Mappings.BIT5_TO_REG.get(register_in_bit5), updated_register_value); }
    int get_register_value_from_bit5(String register_in_bit5) { return this.REGISTERS.get(Mappings.BIT5_TO_REG.get(register_in_bit5)); }
    String get_register_from_bit5(String register_in_bit5) { return Mappings.BIT5_TO_REG.get(register_in_bit5); }
    String read_address() { return this.INSTRUCTIONS.get(this.PC); }
    void print_pc_and_registers() { System.out.println(this.PC_AND_REGISTERS.get(this.PC)); }
}
