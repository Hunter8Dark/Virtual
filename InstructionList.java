import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InstructionList {
    ArrayList<Instruction> instructions = new ArrayList<Instruction>();

    InstructionList(){}

    InstructionList(NodeList nodelist){
        for(int i = 0; i < nodelist.getLength(); i++){
            Node node = nodelist.item(i);
            Element element = (Element)node;
            int p = Integer.parseInt(element.getElementsByTagName("processID").item(0).getTextContent());
            String o = element.getElementsByTagName("operation").item(0).getTextContent();
            int a = Integer.parseInt(element.getElementsByTagName("address").item(0).getTextContent());
            Instruction instruction = new Instruction(p, o, a);
            instructions.add(instruction);
        }
    }

    InstructionList(InstructionList array){
        instructions = array.instructions;
    }

    Instruction get(int index){
        if(index == instructions.size() || index < 0){
            return null;
        }
        return instructions.get(index);
    }

    /*public void sortProcessID(){
        Collections.sort(instructions, Comparator.comparingInt(p -> p.processID));
    }

    public void sortServicetime(){
        Collections.sort(instructions, Comparator.compa(p -> p.operation));
    }

    public void sortResponsetime(){
        Collections.sort(instructions, Comparator.comparingInt(p -> -p.responsetime));
    }

    public void sortRemainingtime(){
        Collections.sort(instructions, Comparator.comparingInt(p -> p.remainingtime));
    }

    public void add(Instruction instruction){
        instructions.add(instruction);
    }
    public void remove(Instruction instruction){
        instructions.remove(instruction);
    }

    public int averageResponstime(){
        int total = 0;
        for (Process process : instructions) {
            total += process.responsetime;
        }
        return total / instructions.size();
    }

    public int averageTat(){
        int total = 0;
        for (Process process : instructions) {
            total += process.tat;
        }
        return total / instructions.size();
    }

    public int averageWaitingtime(){
        int total = 0;
        for (Process process : instructions) {
            total += process.waitingtime;
        }
        return total / instructions.size();
    }

    public double[] getWaitingtime(){
        double[] times = new double[instructions.size()];
        for(int i = 0; i < instructions.size(); i++){
            times[i] = instructions.get(i).waitingtime;
        }
        return times;
    }
    public double[] getResponsetime(){
        double[] times = new double[instructions.size()];
        for(int i = 0; i < instructions.size(); i++){
            times[i] = instructions.get(i).responsetime;
        }
        return times;
    }
    public double[] getServicetime(){
        double[] times = new double[instructions.size()];
        for(int i = 0; i < instructions.size(); i++){
            times[i] = instructions.get(i).servicetime;
        }
        return times;
    }

    public int getResponsetime(int index){
        return instructions.get(index).responsetime;
    }
    public int getWaitingtime(int index){
        return instructions.get(index).waitingtime;
    }
    public int getServicetime(int index){return instructions.get(index).servicetime;*/
}
