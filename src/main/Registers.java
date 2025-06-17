public class Registers {
    int READ_REGISTER_1;
    int READ_REGISTER_2;
    String WRITE_REGISTER;
    int WRITE_DATA;
    int READ_DATA_1;
    int READ_DATA_2;

    Registers() {
        READ_REGISTER_1 = 0;
        READ_REGISTER_2 = 0;
        WRITE_REGISTER = null;
        WRITE_DATA = 0;
        READ_DATA_1 = 0;
        READ_DATA_2 = 0;
    }

    void read_register_1(int val) { this.READ_REGISTER_1 = val; }
    void read_register_2(int val) { this.READ_REGISTER_2 = val; }
    void write_register(String register) { this.WRITE_REGISTER = register; }

    int read_data_1() {
        this.READ_DATA_1 = this.READ_REGISTER_1;
        return this.READ_DATA_1;
    }

    int read_data_2() {
        this.READ_DATA_2 = this.READ_REGISTER_2;
        return this.READ_DATA_2;
    }

    void write_data(int val) {
        this.WRITE_DATA = val;
    }
}
