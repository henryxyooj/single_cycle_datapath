import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MIPS {
    private static final Map<String, String> HEX_TO_4BIT = new HashMap<>();
    private static final Map<String, String> REG_TO_5BIT = new HashMap<>();
    private static final int DATA_START_ADDRESS = 0x10010000;
    private static final int TEXT_START_ADDRESS = 0x00400000;

    private final Map<String, Integer> REGISTERS;
    private final Map<Integer, String> MEMORY_AND_WORDS;
    private final Map<Integer, String> PC_AND_REGISTERS;
    private final Map<Integer, String> INSTRUCTIONS;

    private int PC;
    private int MEMORY;
    private int JUMPED_ADDRESS;
    private int JR_ADDRESS;
    private boolean JUMPED;
    private boolean JR;

    static {
        HEX_TO_4BIT.put("0", "0000");
        HEX_TO_4BIT.put("1", "0001");
        HEX_TO_4BIT.put("2", "0010");
        HEX_TO_4BIT.put("3", "0011");
        HEX_TO_4BIT.put("4", "0100");
        HEX_TO_4BIT.put("5", "0101");
        HEX_TO_4BIT.put("6", "0110");
        HEX_TO_4BIT.put("7", "0111");
        HEX_TO_4BIT.put("8", "1000");
        HEX_TO_4BIT.put("9", "1001");
        HEX_TO_4BIT.put("a", "1010");
        HEX_TO_4BIT.put("b", "1011");
        HEX_TO_4BIT.put("c", "1100");
        HEX_TO_4BIT.put("d", "1101");
        HEX_TO_4BIT.put("e", "1110");
        HEX_TO_4BIT.put("f", "1111");

        REG_TO_5BIT.put("00000", "$zero");
        REG_TO_5BIT.put("00001", "$at");
        REG_TO_5BIT.put("00010", "$v0");
        REG_TO_5BIT.put("00011", "$v1");
        REG_TO_5BIT.put("00100", "$a0");
        REG_TO_5BIT.put("00101", "$a1");
        REG_TO_5BIT.put("00110", "$a2");
        REG_TO_5BIT.put("00111", "$a3");
        REG_TO_5BIT.put("01000", "$t0");
        REG_TO_5BIT.put("01001", "$t1");
        REG_TO_5BIT.put("01010", "$t2");
        REG_TO_5BIT.put("01011", "$t3");
        REG_TO_5BIT.put("01100", "$t4");
        REG_TO_5BIT.put("01101", "$t5");
        REG_TO_5BIT.put("01110", "$t6");
        REG_TO_5BIT.put("01111", "$t7");
        REG_TO_5BIT.put("10000", "$s0");
        REG_TO_5BIT.put("10001", "$s1");
        REG_TO_5BIT.put("10010", "$s2");
        REG_TO_5BIT.put("10011", "$s3");
        REG_TO_5BIT.put("10100", "$s4");
        REG_TO_5BIT.put("10101", "$s5");
        REG_TO_5BIT.put("10110", "$s6");
        REG_TO_5BIT.put("10111", "$s7");
        REG_TO_5BIT.put("11000", "$t8");
        REG_TO_5BIT.put("11001", "$t9");
        REG_TO_5BIT.put("11010", "$k0");
        REG_TO_5BIT.put("11011", "$k1");
        REG_TO_5BIT.put("11100", "$gp");
        REG_TO_5BIT.put("11101", "$sp");
        REG_TO_5BIT.put("11110", "$fp");
        REG_TO_5BIT.put("11111", "$ra");
    }

    public MIPS() {
        this.JUMPED_ADDRESS = 0;
        this.JUMPED = false;
        this.JR = false;

        this.REGISTERS = new HashMap<>();
        REGISTERS.put("$zero", 0x00000000);
        REGISTERS.put("$at", 0x00000000);
        REGISTERS.put("$v0", 0x00000000);
        REGISTERS.put("$v1", 0x00000000);
        REGISTERS.put("$a0", 0x00000000);
        REGISTERS.put("$a1", 0x00000000);
        REGISTERS.put("$a2", 0x00000000);
        REGISTERS.put("$a3", 0x00000000);
        REGISTERS.put("$t0", 0x00000000);
        REGISTERS.put("$t1", 0x00000008);
        REGISTERS.put("$t2", 0x00000000);
        REGISTERS.put("$t3", 0x00000000);
        REGISTERS.put("$t4", 0x00000000);
        REGISTERS.put("$t5", 0x00000000);
        REGISTERS.put("$t6", 0x00000000);
        REGISTERS.put("$t7", 0x00000000);
        REGISTERS.put("$s0", 0x00000000);
        REGISTERS.put("$s1", 0x00000000);
        REGISTERS.put("$s2", 0x00000000);
        REGISTERS.put("$s3", 0x00000000);
        REGISTERS.put("$s4", 0x00000000);
        REGISTERS.put("$s5", 0x00000000);
        REGISTERS.put("$s6", 0x00000000);
        REGISTERS.put("$s7", 0x00000000);
        REGISTERS.put("$t8", 0x00000000);
        REGISTERS.put("$t9", 0x00000000);
        REGISTERS.put("$k0", 0x00000000);
        REGISTERS.put("$k1", 0x00000000);
        REGISTERS.put("$gp", 0x10008000);
        REGISTERS.put("$sp", 0x7fffeffc);
        REGISTERS.put("$fp", 0x00000000);
        REGISTERS.put("$ra", 0x00000000);

        this.MEMORY = DATA_START_ADDRESS;
        this.MEMORY_AND_WORDS = new HashMap<>();

        this.PC = TEXT_START_ADDRESS;
        this.PC_AND_REGISTERS = new HashMap<>();    // (PC [0x00400000], REGISTERS [ALL OF THEM])
        this.INSTRUCTIONS = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Error: Two arguments needed: <input_file.data> <input_file.text>");
        }

        MIPS isa = new MIPS();

        loadMemory(isa, args[0]);
        loadInstructions(isa, args[1]);

        IF(isa);
    }

    public static void IF(MIPS isa) {
        while (isa.PC < isa.INSTRUCTIONS.size() * 4 + TEXT_START_ADDRESS) {
            ID(isa);
            if (isa.JUMPED) {
                isa.PC = isa.JUMPED_ADDRESS;
            } else {
                isa.PC += 4;
            }
            isa.JUMPED = false;
        }
        System.out.println("-- program finished running (dropped off bottom) --");  // next instruction = 0x00000000
    }

    private static void ID(MIPS isa) {
        String textInstruction32Bits = String.valueOf(hexToBit(isa.INSTRUCTIONS.get(isa.PC)));
        String opcode = textInstruction32Bits.substring(0, 6);
        String funct = textInstruction32Bits.substring(26, 32);

        switch (opcode) {
            case "000000":  //rtype and syscall
                if (funct.equals("001100")) {
                    EX_Syscall(isa);
                } else {
                    EX_RType(isa, textInstruction32Bits, opcode, funct);
                }
                break;
            case "001000":
            case "001001":
            case "240200":
            case "340200": // addi, addiu - iType
            case "001100": // andi - iType
            case "001101": // ori - iType
            case "001111": // lui - iType
            case "000100": // beq - iType
            case "000101": // bne - iType
                EX_iType(isa, textInstruction32Bits, opcode);
                break;
            case "100011": // lw
                EX_lw(isa, textInstruction32Bits);
                break;
            case "101011": // sw
                EX_sw(isa, textInstruction32Bits);
                break;
            case "000010":
            case "000011": // j, jal, jr
                EX_jType(isa, textInstruction32Bits, opcode);
                break;
            default:
                break; // Ignore unsupported instructions
        }
    }

    public static void EX_RType(MIPS isa, String textInstruction32Bits, String opcode, String funct) {
        String rs = textInstruction32Bits.substring(6, 11);
        String rt = textInstruction32Bits.substring(11, 16);
        String rd = textInstruction32Bits.substring(16, 21);

        int rsValue = isa.REGISTERS.get(REG_TO_5BIT.get(rs));
        int rtValue = isa.REGISTERS.get(REG_TO_5BIT.get(rt));
        int rtUpdate = 0;

        switch (funct) {
            case "100000":  // add
            case "100001":  // addu
                rtUpdate = rsValue + rtValue;
                break;
            case "100010":  // sub
                rtUpdate = rsValue - rtValue;
                break;
            case "100100":  // and
                rtUpdate = rsValue & rtValue;
                break;
            case "100101":  // or
                rtUpdate = rsValue | rtValue;
                break;
            case "101010":  // slt
                rtUpdate = (rsValue < rtValue) ? 1 : 0;
                break;
            case "001000":  //move + jr
                if (isa.JR) {
                    isa.PC = isa.JR_ADDRESS;
                } else {
                    rtUpdate = isa.REGISTERS.get(REG_TO_5BIT.get(rs));
                }
                break;
            default:
                break;
        }
        isa.JR = false;
        isa.REGISTERS.put(REG_TO_5BIT.get(rd), rtUpdate);
        WB(isa);
    }

    public static void EX_iType(MIPS isa, String textInstruction32Bits, String opcode) {
        String rs = textInstruction32Bits.substring(6, 11);
        String rt = textInstruction32Bits.substring(11, 16);
        String immediate = textInstruction32Bits.substring(16);

        int rsValue = isa.REGISTERS.get(REG_TO_5BIT.get(rs)); // value of rs register
        int rtValue = isa.REGISTERS.get(REG_TO_5BIT.get(rt)); // value of rt register
        int imm = Integer.parseInt(immediate, 2);
        int offset = signExtend(imm, 16);
        int rtUpdate = 0;  // we're going to store this value into to the isa REGISTER, to keep them updated

        switch (opcode) {
            case "001000":
            case "001001":
            case "240200":
            case "340200":
                rtUpdate = rsValue + signExtend(imm, 16);
                isa.REGISTERS.put(REG_TO_5BIT.get(rt), rtUpdate);
                break; // addi, addiu
            case "001100":
                rtUpdate = rsValue & signExtend(imm, 16);
                isa.REGISTERS.put(REG_TO_5BIT.get(rt), rtUpdate);
                break; // andi
            case "001101":
                rtUpdate = rsValue | signExtend(imm, 16);
                isa.REGISTERS.put(REG_TO_5BIT.get(rt), rtUpdate);
                break; // ori
            case "001111":
                rtUpdate = imm << 16;
                isa.REGISTERS.put(REG_TO_5BIT.get(rt), rtUpdate);
                break; // lui
            case "000100": // beq
                if (rsValue == rtValue) {
                    isa.JUMPED = true;
                    int shifted = offset << 2;
                    isa.JUMPED_ADDRESS = shifted + (isa.PC + 4);
                }
                break;
            case "000101": // bne
                if (rsValue != rtValue) {
                    isa.JUMPED = true;
                    int shifted = offset << 2;
                    isa.JUMPED_ADDRESS = shifted + (isa.PC + 4);
                }
                break;
            default:
                // Handle other i-type instructions...
                break;
        }
        WB(isa);
    }

    private static void EX_lw(MIPS isa, String textInstructions32Bits) {
        String rs = textInstructions32Bits.substring(6, 11);
        String rt = textInstructions32Bits.substring(11, 16);
        int offset = Integer.parseInt(textInstructions32Bits.substring(16), 2);

        // Calculate the effective memory address from the base register and offset
        int rsVal = isa.REGISTERS.get(REG_TO_5BIT.get(rs));  // 0($s0) ---- base address is $s0, getting the register value from it 0x10010000
        int rtVal = rsVal + offset;  // register value getting stored in
        isa.REGISTERS.put(REG_TO_5BIT.get(rt), rtVal);

        // Call MEM to perform the load operation
        MEM(isa, "100011", rtVal, rt, null);
    }

    private static void EX_sw(MIPS isa, String textInstructions32Bits) {
        String rs = textInstructions32Bits.substring(6, 11);
        String rt = textInstructions32Bits.substring(11, 16);
        int offset = Integer.parseInt(textInstructions32Bits.substring(16), 2);

        int rsVal = isa.REGISTERS.get(REG_TO_5BIT.get(rs));
        int memAddrDest = rsVal + offset;

        // Call MEM to perform the store operation, passing the data to store
        MEM(isa, "101011", memAddrDest, rt, rs);
    }

    private static void EX_jType(MIPS isa, String textInstructions32Bits, String opcode) {
        String target = textInstructions32Bits.substring(6);

        // The target address needs to be shifted left by 2 (word-aligned) and combined with the upper bits of the current PC
        int jumpAddress = (isa.PC & 0xF0000000) | (Integer.parseInt(target, 2) << 2);

        switch (opcode) {
            case "000010": // j
                isa.JUMPED = true;
                isa.JUMPED_ADDRESS = jumpAddress;
                break;
            case "000011": // jal
                // Store the return address in the $ra register ($31)
                // Return address is the address of the next instruction (current PC + 4)
                isa.JR = true;
                isa.JR_ADDRESS = (isa.PC + 4);
                isa.JUMPED_ADDRESS = jumpAddress;
                isa.REGISTERS.put("$ra", (isa.JR_ADDRESS));
                break;
        }
        MEM(isa, opcode, 0, null, null);
    }

    private static void MEM(MIPS isa, String opcode, int memAddrDest, String rt, String rs) {
        switch (opcode) {
            case "100011": // lw
                writeToRegister(isa, rt);
                break;
            case "101011": // sw
                writeToMemory(isa, rt, rs, memAddrDest);
                break;
        }
        WB(isa);
    }

    private static void WB(MIPS isa) {
        //System.out.println("Hex = " + "0x" + String.format("%08X", isa.PC) + "\tDecimal: " + isa.PC);
        isa.PC_AND_REGISTERS.put(isa.PC, isa.REGISTERS.toString()); // current pc, hashmap of the registers and their values
        //System.out.println(isa.PC_AND_REGISTERS.get(isa.PC));  // prints out before pc + 4 and the registers
    }

    private static void EX_Syscall(MIPS isa) {
        int syscall = isa.REGISTERS.get("$v0");

        switch (syscall) {
            case 1: // print integer
                System.out.println(isa.REGISTERS.get("$a0"));
                break;
            case 4: // print string
                printString(isa);
                break;
            case 5: // read integer
                Scanner scanner = new Scanner(System.in);
                isa.REGISTERS.put("$v0", scanner.nextInt());
                break;
            case 10:    // end the program
                System.out.println("-- program is finished running --");
                System.exit(0);
                break;
        }
    }

    private static void printString(MIPS isa) {
        int memoryAddress = isa.REGISTERS.get("$a0");   // register $a0 value
        int memoryAddressMod4 = 0;
        int mod = (memoryAddress % 4 == 0) ? 0 : 1;
        boolean isModded = false;

        switch (mod) {
            case 0: //if divisible by 4, the memory address can be referenced
                readMemory(isa, memoryAddress, isModded, 0);
                break;
            case 1: //if not divisible by 4,
                isModded = true;
                memoryAddressMod4 = memoryAddress;
                int subtractCounted = 0;
                while ((memoryAddressMod4 % 4) != 0) {
                    memoryAddressMod4--;
                    subtractCounted++;
                }
                readMemory(isa, memoryAddressMod4, isModded, subtractCounted);
                break;
        }
    }

    private static void writeToMemory(MIPS isa, String rt, String rs, int memAddrDest) {
        int rtVal = isa.REGISTERS.get(REG_TO_5BIT.get(rt)); // contents of the rt register
        String contentsOfRt = Integer.toHexString(rtVal);   // parse it as String
        isa.REGISTERS.put(REG_TO_5BIT.get(rs), memAddrDest);    // store the contents of rt into the new memory address
        isa.MEMORY_AND_WORDS.put(memAddrDest, contentsOfRt);    // update memory address and the words associated with memory
    }

    private static void writeToRegister(MIPS isa, String rt) {
        StringBuilder decToHex = new StringBuilder();
        int memoryAddress = isa.REGISTERS.get(REG_TO_5BIT.get(rt));
        String word = isa.MEMORY_AND_WORDS.get(memoryAddress);  // words inside the memory address

        for (int i = 0; i < word.length(); i += 2) {
            String twoChars = word.substring(i, i + 2);
            decToHex.append(twoChars);
        }
        String hexString = decToHex.toString();
        int hexParsed = Integer.parseInt(hexString);

        isa.REGISTERS.put(REG_TO_5BIT.get(rt), hexParsed);
    }

    private static void readMemory(MIPS isa, int memoryAddress, boolean isModded, int subtractCounted) {
        StringBuilder modMemory = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        String word;  // words inside the memory address
        boolean isNull = false;

        if (isModded) {
            memoryAddress = moddedMemoryAddress(isa, memoryAddress, subtractCounted, modMemory);
        }

        while (!isNull) {
            StringBuilder hexToDec = new StringBuilder();
            word = isa.MEMORY_AND_WORDS.get(memoryAddress);  // words inside the memory address

            if (word == null) isNull = true;
            for (int i = 0; i < word.length(); i += 2) {
                String twoChars = word.substring(i, i + 2);
                hexToDec.insert(0, twoChars);
            }

            for (int i = 0; i < hexToDec.length(); i += 2) {
                String twoChars = hexToDec.substring(i, i + 2);
                if (twoChars.equals("00")) {
                    isNull = true;
                    break;
                }
                int twoCharsToDec = Integer.parseInt(twoChars, 16);
                String hexToDecimal = String.valueOf((char) twoCharsToDec);
                ascii.append(hexToDecimal);
            }
            memoryAddress += 4;
        }
        modMemory.append(ascii);
        System.out.println(modMemory);
    }

    private static int moddedMemoryAddress(MIPS isa, int moddedMemoryAddress, int subtractCounted, StringBuilder modMemory) {
        StringBuilder moddedHexToDec = new StringBuilder();
        String word = isa.MEMORY_AND_WORDS.get(moddedMemoryAddress);
        boolean foundNull = false;

        for (int i = 0; i < word.length(); i += 2) {
            String twoChars = word.substring(i, i + 2);
            moddedHexToDec.insert(0, twoChars);
        }
        for (int k = 0; k < moddedHexToDec.length(); k += 2) {
            String moddedTwoChars = moddedHexToDec.substring(k, k + 2);
            if (moddedTwoChars.equals("00")) {
                foundNull = true;
                continue;
            }
            if (foundNull) {
                int twoCharsToDec = Integer.parseInt(moddedTwoChars, 16);
                String moddedHexToDecimal = String.valueOf((char) twoCharsToDec);
                modMemory.append(moddedHexToDecimal);
            }
        }

        moddedMemoryAddress = moddedMemoryAddress + subtractCounted;
        subtractCounted = 4 - subtractCounted;
        moddedMemoryAddress = moddedMemoryAddress + subtractCounted;
        isa.MEMORY_AND_WORDS.put((isa.MEMORY), String.valueOf(moddedMemoryAddress));
        return moddedMemoryAddress;
    }

    private static void loadMemory(MIPS isa, String dataInstructions) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataInstructions))) {
            String currline;
            while ((currline = reader.readLine()) != null && !currline.equals("00000000")) {
                // stores the memory address with it's associated value: (0x10010000, 0x65746e45)
                isa.MEMORY_AND_WORDS.put(isa.MEMORY, currline);
                isa.MEMORY += 4;
            }
        }
    }

    private static void loadInstructions(MIPS isa, String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int address = TEXT_START_ADDRESS;
            while ((line = reader.readLine()) != null) {
                isa.INSTRUCTIONS.put(address, line);
                address += 4;
            }
        }
    }

    private static StringBuilder hexToBit(String MIPSinstruction) {
        StringBuilder bit32str = new StringBuilder();   // this keeps track of the 4 bit per 1 hex value

        // loop through each char of the MIPSinstruction, and compare it to the HEX_TO_BINARY to get your 4 bits
        for (int i = 0; i < MIPSinstruction.length(); i++) {
            if (MIPS.HEX_TO_4BIT.containsKey(Character.toString(MIPSinstruction.charAt(i)))) {
                bit32str.append(MIPS.HEX_TO_4BIT.get(Character.toString(MIPSinstruction.charAt(i))));
            }
        }
        return bit32str;
    }

    private static int signExtend(int immediate, int bits) {
        int shift = 32 - bits;
        return (immediate << shift) >> shift;
    }
}