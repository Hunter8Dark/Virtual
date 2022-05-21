import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Ram extends AbstractTableModel{

    public static Map<Integer,RamFrame> frames = new HashMap<Integer, RamFrame>();
    private final int maxFrames = 12;
    private final int maxProcesses = 4;
    private List<Integer> processes = new ArrayList<Integer>();
    private final String[] columns = {"ProcessID", "Pagenummer"};
    private String emptyChar = "";


    public Ram() {
        for(int i = 0; i < maxFrames; i++){
            RamFrame f = new RamFrame(emptyChar,emptyChar);
            frames.put(i, f);
        }
    }

    void addProcess(int processId){
        if(processes.size() == maxProcesses - 1){
            replaceProcess();
        }
        else{
            processes.add(processId);
        }
        allocateProcess();
    }

    void removeProcess(int processId){
        processes.remove(new Integer(processId));
        allocateProcess();
    }

    void allocateProcess(){
        if(processes.size() == 0){
            for(int i = 0; i < maxFrames; i++){
                RamFrame f = new RamFrame(emptyChar,emptyChar);
                frames.put(i, f);
            }
        }
        else{
            //TODO Replace full processes
            int aantalProcesses = maxFrames / processes.size();
            int verwijderen = aantalProcesses - maxFrames / (processes.size() - 1);
            for(int process:processes){
                Map<Integer, RamFrame> processFrames = getProcessFrames(process);

                RamFrame replacement = new RamFrame();
                int pos = 0;
                for (Map.Entry<Integer, RamFrame> entry : processFrames.entrySet()) {
                    RamFrame oldFrame = frames.get(entry.getKey());
                    if(oldFrame.getLastused() < replacement.getLastused()){
                        replacement = oldFrame;
                        pos = entry.getKey();
                    }
                }
                RamFrame f = new RamFrame(String.valueOf(process),emptyChar);
                frames.replace(process * aantalProcesses, f);

            }
        }

        fireTableChanged(new TableModelEvent(this));
    }

    void replaceProcess(){
        int replacement;
        RamFrame help = new RamFrame();
        for(int i = 0; i < maxProcesses; i++){
            for(int j = 0; j < 3; j++){
                RamFrame oldFrame = frames.get((i+1)*(j+1));
                if(oldFrame.getLastused() < help.getLastused()){
                    help = oldFrame;
                }
            }
        }
        replacement = help.getProcessIDInt();
        Map<Integer, RamFrame> processFrames = getProcessFrames(replacement);
        for (Map.Entry<Integer, RamFrame> entry : processFrames.entrySet()){
            removeFrame(entry.getKey());
        }

        //TODO Search best process to erase out of RAM

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

    public void addFrame(RamFrame f, int index){
        frames.put(index,f);
        fireTableChanged(new TableModelEvent(this));
    }

    public void removeFrame(int framenummer){
        RamFrame f = new RamFrame(emptyChar,emptyChar);
        frames.replace(framenummer, f);
        fireTableChanged(new TableModelEvent(this));
    }

    public PageTableList replaceFrame(RamFrame f, PageTableList pagetables){
        int processID = f.getProcessIDInt();
        PageTable pagetable = pagetables.get(processID);
        int pagenummer = f.getPagenummerInt();

        int freeFrame = getFreeFrame(processID);
        if(freeFrame != -1){
            frames.replace(freeFrame, f);

            pagetable.getPage(pagenummer).setPresentBit(1);
            pagetable.getPage(pagenummer).setFysicalFramenummer(String.valueOf(freeFrame));
            pagetable.getPage(pagenummer).addIn();
        }
        else{
            Map<Integer, RamFrame> processFrames = getProcessFrames(processID);
            RamFrame replacement = new RamFrame();
            int pos = 0;
            for (Map.Entry<Integer, RamFrame> entry : processFrames.entrySet()) {
                RamFrame oldFrame = frames.get(entry.getKey());
                if(oldFrame.getLastused() < replacement.getLastused()){
                    replacement = oldFrame;
                    pos = entry.getKey();
                }
            }
            frames.replace(pos, f);
            int oldPagenummer = replacement.getPagenummerInt();
            pagetable.getPage(oldPagenummer).setPresentBit(0);
            pagetable.getPage(oldPagenummer).setFysicalFramenummer("-");
            pagetable.getPage(oldPagenummer).addOut();


            pagetable.getPage(pagenummer).setPresentBit(1);
            pagetable.getPage(pagenummer).setFysicalFramenummer(String.valueOf(pos));
            pagetable.getPage(pagenummer).addIn();
        }

        fireTableChanged(new TableModelEvent(this));

        pagetables.updatePageTable(processID, pagetable);
        return pagetables;
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
            if(f.getProcessIDInt() == processID){
                tmp.put(entry.getKey(),f);
            }
        }
        return tmp;
    }

    public int getFreeFrame(int processID){
        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getPagenummer().equals(emptyChar) && f.getProcessIDInt() == processID){
                return entry.getKey();
            }
        }
        return -1;
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
