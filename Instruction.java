public class Instruction {

    public int processID;
    public String operation;
    public int address;


    Instruction(int p, String o, int a){
        processID = p;
        operation = o;
        address = a;
    }
}
