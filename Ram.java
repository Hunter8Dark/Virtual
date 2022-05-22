import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Ram extends AbstractTableModel{

    public static Map<Integer,RamFrame> frames = new HashMap<>();
    private final int maxFrames = 12;
    private final int maxProcesses = 4;
    private List<Integer> processes = new ArrayList<Integer>();
    private final String[] columns = {"ProcessID", "Pagenummer"};
    private String emptyChar = "";


    public Ram() {
        reset();
    }

    void addProcess(int processId, PageTableList pages){
        if(processes.size() == maxProcesses){
            System.out.println("Error: Trying to add to many processes!");
        }
        else if(processes.size() == 0){
            processes.add(processId);

            for(int i = 0; i < maxFrames; i++){
                RamFrame f = new RamFrame(String.valueOf(processId), emptyChar);
                frames.put(i, f);
            }
        }
        else{
            processes.add(processId);

            allocateRemovedFrames(pages);

            List<Integer> nullFrames = getNullFrames();
            for(int index : nullFrames){
                RamFrame f = new RamFrame(String.valueOf(processId), emptyChar);
                frames.put(index, f);
            }
        }

        fireTableChanged(new TableModelEvent(this));
    }

    void removeProcess(Integer processId, PageTableList pages){
        processes.remove(processId);

        Map<Integer, RamFrame> processFrames = getProcessFrames(processId);
        for (Map.Entry<Integer, RamFrame> entry : processFrames.entrySet()) {

            RamFrame f = new RamFrame(emptyChar, emptyChar);
            pages.removeFrameFromRam(entry.getValue(), emptyChar);
            frames.put(entry.getKey(), f);
        }

        allocateAddedFrames();

        fireTableChanged(new TableModelEvent(this));
    }

    void allocateRemovedFrames(PageTableList pages){
        int aantalProcesses = maxFrames / processes.size();
        int processesToRemove = maxFrames / (processes.size() - 1) - aantalProcesses;

        for(int i = 0; i < processes.size(); i++){

            int processID = processes.get(i);
            RamFrame f = new RamFrame(emptyChar,emptyChar);
            removeNumberOfFrames(f, processID, processesToRemove, pages);
        }

    }

    void allocateAddedFrames(){
        if(processes.isEmpty()){
            reset();
        }
        else{

            List<Integer> nullFrames = getNullFrames();
            int aantalProcesses = maxFrames / processes.size();
            int processesToAdd = aantalProcesses - maxFrames / (processes.size() + 1);
            int start = 0;

            for(int processID : processes){

                for (int i = 0; i < processesToAdd; i++) {

                    RamFrame f = new RamFrame(String.valueOf(processID), emptyChar);
                    frames.put(nullFrames.get(0), f);
                    nullFrames.remove(start);
                }
            }
        }
    }

    public void addFrame(RamFrame f, PageTableList  pages){
        int processID = f.getProcessIDInt();
        int freeFrame = getFreeFrame(processID);

        if(freeFrame != -1){
            frames.replace(freeFrame, f);
            pages.addFrameToRam(f, freeFrame, emptyChar);
        }
        else{
            replaceFrame(f, processID, pages);
        }

        fireTableChanged(new TableModelEvent(this));
    }

    void replaceFrame(RamFrame f, int processID, PageTableList pages){
        Map<Integer, RamFrame> processFrames = getProcessFrames(processID);
        PageTable pageTable = pages.get(processID);

        RamFrame replaceFrame = new RamFrame();
        int currentLastAccesed = 1000000;
        int replaceIndex = -1;

        for (Map.Entry<Integer, RamFrame> entry : processFrames.entrySet()) {

            RamFrame frame = frames.get(entry.getKey());
            Page p =  pageTable.getPage(frame.getPagenummerInt());
            int lastAccesed = p.getLastAccessTime();

            if(currentLastAccesed > lastAccesed){

                replaceFrame = frame;
                currentLastAccesed = lastAccesed;
                replaceIndex = entry.getKey();
            }
        }

        if(replaceIndex != -1){
            frames.replace(replaceIndex, f);
            pages.addFrameToRam(f, replaceIndex, emptyChar);
            pages.removeFrameFromRam(replaceFrame, emptyChar);
        }
        else{
            System.out.println("Error replacing frame, no index found!");
        }

    }

    void removeNumberOfFrames(RamFrame f,int processID, int aantal, PageTableList pages){
        int max = maxFrames / processes.size();
        for(int i = 0; i < aantal; i++){

            int numberOfFrames = getNumberOfFrames(processID);
            if(numberOfFrames > max){

                int freeFrame = getFreeFrame(processID);
                if(freeFrame != -1){
                    frames.replace(freeFrame, f);
                    pages.addFrameToRam(f, freeFrame, emptyChar);
                }
                else{
                    replaceFrame(f, processID, pages);
                }
            }
        }
    }

    void reset(){
        frames.clear();
        for(int i = 0; i < maxFrames; i++){
            RamFrame f = new RamFrame(emptyChar,emptyChar);
            frames.put(i, f);
        }
        processes.clear();

        fireTableChanged(new TableModelEvent(this));
    }

    public RamFrame getFrame(int pagenummer, int processID){
        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getPagenummer().equals(emptyChar)){
                continue;
            }
            if(f.getPagenummerInt() == pagenummer && f.getProcessIDInt() == processID){
                return f;
            }
        }
        return null;
    }

    public Map<Integer, RamFrame> getProcessFrames(int processID){
        Map<Integer, RamFrame> tmp = new HashMap<>();
        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getProcessID().equals(emptyChar))
            {
                continue;
            }

            if(f.getProcessIDInt() == processID){
                tmp.put(entry.getKey(),f);
            }
        }
        return tmp;
    }

    public int getFreeFrame(int processID){
        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getProcessID().equals(emptyChar)){
                continue;
            }

            if(f.getPagenummer().equals(emptyChar) && f.getProcessIDInt() == processID){
                return entry.getKey();
            }
        }
        return -1;
    }
    public List<Integer> getNullFrames(){
        List<Integer> nullFrames = new ArrayList<>();

        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getProcessID().equals(emptyChar)){
                nullFrames.add(entry.getKey());
            }
        }
        return nullFrames;
    }

    public int getNumberOfFrames(int processID){
        Map<Integer, RamFrame> processFrames = getProcessFrames(processID);
        return  processFrames.size();
    }


    //Standaard code voor JTable Template, niet verwijderen!
    public int getColumnCount() {
        return columns.length;
    }
    public int getRowCount() {
        return frames.size();
    }
    public void setValueAt(Object value, int row, int col) {
        RamFrame frame = frames.get(row);

        switch (col) {
            case 0 -> frame.setProcessID((String) value);
            case 1 -> frame.setPagenummer((String) value);
        }
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public Object getValueAt(int row, int col) {
        RamFrame frame = frames.get(row);

        return switch (col) {
            case 0 -> frame.getProcessID();
            case 1 -> frame.getPagenummer();
            default -> "";
        };
    }



}
