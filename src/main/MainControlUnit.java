public class MainControlUnit {
    public int RegDst;
    public int Branch;
    public int MemRead;
    public int MemtoReg;
    public int ALUSrc;
    public int MemWrite;
    public int RegWrite;
    public int Jump;
    public int LUICtr;
    public int PCSrc;
    public String ALUOp;
    public String instruction;
    ALUControlUnit ALU_CONTROL;

    public MainControlUnit() {
        this.ALU_CONTROL = new ALUControlUnit();
    }

    // 1 = true, 0 = false, -1 = don't care
    public void set_control_signal(String opcode, String funct) {
        assert opcode != null && opcode.length() == 6 : "Opcode must be 6 bits long";

        boolean valid_opcode = switch (opcode) {
            case "000000", "001000", "001001", "001100", "001101", "001111",
                 "000100", "000101", "100011", "101011", "000011", "000010" -> true;
            default -> false;
        };
        assert valid_opcode : "Invalid or unsupported opcode : " + opcode;

        switch (opcode) {
            case "000000":  //rtype, syscall, or jr
                if (funct.equals("001000")) {
                    this.RegDst = -1;
                    this.Branch = 0;
                    this.MemRead = 0;
                    this.MemtoReg = -1;
                    this.ALUSrc = -1;
                    this.MemWrite = 0;
                    this.RegWrite = 0;
                    this.Jump = 1;
                    this.LUICtr = 0;
                    this.PCSrc = 2;
                    this.ALUOp = "XX";  // ALU operation determined by funct
                    this.instruction = "jr";
                }
                else {
                    this.RegDst = 1;
                    this.Branch = 0;
                    this.MemRead = 0;
                    this.MemtoReg = 0;
                    this.ALUSrc = 0;
                    this.MemWrite = 0;
                    this.RegWrite = 1;
                    this.Jump = 0;
                    this.LUICtr = 0;
                    this.PCSrc = 0;
                    this.ALUOp = "10";  // ALU operation determined by funct
                    this.instruction = "rtype or syscall";
                }
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "001000":  // addi
                this.RegDst = 0;
                this.Branch = 0;
                this.MemRead = 0;
                this.MemtoReg = 0;
                this.ALUSrc = 1;
                this.MemWrite = 0;
                this.RegWrite = 1;
                this.Jump = 0;
                this.LUICtr = 0;
                this.PCSrc = 0;
                this.ALUOp = "00";  // ALU operation determined by funct
                this.instruction = "addi";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "001001":  // addiu
                this.RegDst = 0;
                this.Branch = 0;
                this.MemRead = 0;
                this.MemtoReg = 0;
                this.ALUSrc = 1;
                this.MemWrite = 0;
                this.RegWrite = 1;
                this.Jump = 0;
                this.LUICtr = 0;
                this.PCSrc = 0;
                this.ALUOp = "00";  // ALU operation determined by funct
                this.instruction = "addiu";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "001100": // andi
                this.RegDst = 0;
                this.Branch = 0;
                this.MemRead = 0;
                this.MemtoReg = 0;
                this.ALUSrc = 1;
                this.MemWrite = 0;
                this.RegWrite = 1;
                this.Jump = 0;
                this.LUICtr = 0;
                this.PCSrc = 0;
                this.ALUOp = "11";  // ALU operation determined by funct
                this.instruction = "andi";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "001101": // ori
                this.RegDst = 0;
                this.Branch = 0;
                this.MemRead = 0;
                this.MemtoReg = 0;
                this.ALUSrc = 1;
                this.MemWrite = 0;
                this.RegWrite = 1;
                this.Jump = 0;
                this.LUICtr = 0;
                this.PCSrc = 0;
                this.ALUOp = "11";  // ALU operation determined by funct
                this.instruction = "ori";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "001111": // lui
                this.RegDst = 0;
                this.Branch = 0;
                this.MemRead = -1;
                this.MemtoReg = -1;
                this.ALUSrc = -1;
                this.MemWrite = 0;
                this.RegWrite = 1;
                this.Jump = 0;
                this.LUICtr = 1;
                this.PCSrc = 0;
                this.ALUOp = "XX";  // ALU operation determined by funct
                this.instruction = "lui";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "000100": // beq
            case "000101": // bne
                this.RegDst = -1;
                this.Branch = 1;
                this.MemRead = 0;
                this.MemtoReg = -1;
                this.ALUSrc = 0;
                this.MemWrite = 0;
                this.RegWrite = 0;
                this.Jump = 0;
                this.LUICtr = 0;
                this.PCSrc = 0;
                this.ALUOp = "01";  // ALU operation determined by funct
                this.instruction = "branch";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "100011": // lw
                this.RegDst = 0;
                this.Branch = 0;
                this.MemRead = 1;
                this.MemtoReg = 1;
                this.ALUSrc = 1;
                this.MemWrite = 0;
                this.RegWrite = 1;
                this.Jump = 0;
                this.LUICtr = 0;
                this.PCSrc = 0;
                this.ALUOp = "00";  // ALU adds base + offset
                this.instruction = "lw";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "101011": // sw
                this.RegDst = -1;
                this.Branch = -1;
                this.MemRead = 0;
                this.MemtoReg = -1;
                this.ALUSrc = 1;
                this.MemWrite = 1;
                this.RegWrite = 0;
                this.Jump = 0;
                this.LUICtr = 0;
                this.PCSrc = 0;
                this.ALUOp = "00";  // ALU adds base + offset
                this.instruction = "sw";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "000010":  // j
                this.RegDst = -1;
                this.Branch = 0;
                this.MemRead = 0;
                this.MemtoReg = -1;
                this.ALUSrc = -1;
                this.MemWrite = 0;
                this.RegWrite = 0;
                this.Jump = 1;
                this.LUICtr = 0;
                this.PCSrc = 0;
                this.ALUOp = "XX";  // ALU adds base + offset
                this.instruction = "j";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            case "000011": // jal
                this.RegDst = 2;
                this.Branch = 0;
                this.MemRead = 0;
                this.MemtoReg = 2;
                this.ALUSrc = -1;
                this.MemWrite = 0;
                this.RegWrite = 1;
                this.Jump = 1;
                this.LUICtr = 0;
                this.PCSrc = 3;
                this.ALUOp = "XX";  // ALU adds base + offset
                this.instruction = "jal";
                this.ALU_CONTROL.set_ALU_control_signals(this.ALUOp, opcode, funct);
                break;
            default:
                throw new IllegalStateException("Unexpected opcode: " + opcode);
        }
    }

    String get_instruction() { return this.instruction; }
    String get_ALUOp() { return this.ALUOp; }
    void set_PCSRc(int value) { this.PCSrc = value; }
    ALUControlUnit get_ALU_CONTROL_UNIT() { return this.ALU_CONTROL; }
}

class ALUControlUnit {
    String ALU_CONTROL_SIGNAL;

    void set_ALU_control_signals(String ALUOp, String opcode, String funct) {
        switch (ALUOp) {
            case "00": // add (used for `lw`, `sw`, `addi`)
                this.ALU_CONTROL_SIGNAL = "0010"; // ALU performs addition
                break;
            case "01": // subtract (used for `beq`, `bne`)
                this.ALU_CONTROL_SIGNAL = "0110"; // ALU performs subtraction
                break;
            case "10": // r-type (funct field determines operation)
                this.ALU_CONTROL_SIGNAL = get_rtype_signal(funct);
                break;
            case "11": // and/or (`andi`, `ori`)
                if (opcode.equals("001100")) {   // andi
                    this.ALU_CONTROL_SIGNAL = "0000";
                }
                else if (opcode.equals("001101")) { // ori
                    this.ALU_CONTROL_SIGNAL = "0001";
                }
                break;
            default:
                this.ALU_CONTROL_SIGNAL = "XXXX"; // don't care
                break;
        }
    }

    String get_rtype_signal(String funct) {  // desired ALU action
        return switch (funct) {
            case "100000" ->  // add
                    "0010"; // alu control inputs
            case "100001" ->  // addu
                    "0010";
            case "100010" ->  // sub
                    "0110";
            case "100100" ->  // and
                    "0000";
            case "100101" ->  // or
                    "0001";
            case "101010" ->  // slt
                    "0111";
            case "001000" ->  // jr
                    "XXXX";
            default -> "XXXX";
        };
    }

    String get_ALU_control_signal() { return this.ALU_CONTROL_SIGNAL; }
}