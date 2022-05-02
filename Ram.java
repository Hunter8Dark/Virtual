import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;

public class Ram extends AbstractTableModel{

    public static Map<Integer,RamFrame> frames = new HashMap<Integer, RamFrame>();
    private final int rows = 12;
    private final String[] columns = {"ProcessID", "Framenummer"};
    private String emptyChar = "";


    public Ram() {
        for(int i = 0; i < rows; i++){
            RamFrame f = new RamFrame(emptyChar,emptyChar);
            frames.put(i, f);
        }
    }

    public void setData(Ram f) {
        frames = f.frames;
        fireTableChanged(new TableModelEvent(this));
    }

    void reset(){
        frames.clear();
        for(int i = 0; i < rows; i++){
            RamFrame f = new RamFrame(emptyChar,emptyChar);
            frames.put(i, f);
        }
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

    public int[] replaceFrame(int framenummer, RamFrame f){
        int pagenummer = frames.get(framenummer).getFramenummerInt();
        int process = frames.get(framenummer).getProcessIDInt();
        frames.replace(framenummer, f);
        fireTableChanged(new TableModelEvent(this));

        int[] tmp = new int[3];

        tmp[0] = pagenummer;
        tmp[1] = process;
        tmp[2] = framenummer;

        return tmp;
    }

    public RamFrame getFrame(int pagenummer, int process){
        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getFramenummer().equals(emptyChar)){
                continue;
            }
            if(f.getFramenummerInt() == pagenummer && f.getProcessIDInt() == process){
                return f;
            }
        }
        return null;
    }

    public int isFull(){
        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getFramenummer().equals(emptyChar)){
                return entry.getKey();
            }
        }
        return -1;
    }

    public int getSize(){
        return frames.size();
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
            case 1 -> frame.setFramenummer((String) value);
        }
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public Object getValueAt(int row, int col) {
        RamFrame frame = frames.get(row);

        return switch (col) {
            case 0 -> frame.getProcessID();
            case 1 -> frame.getFramenummer();
            default -> "";
        };
    }



}
