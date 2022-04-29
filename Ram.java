import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;

public class Ram extends AbstractTableModel{

    public static Map<Integer,RamFrame> frames = new HashMap<Integer, RamFrame>();
    private final int rows = 12;
    private final String[] columns = {"ProcessID", "Framenummer"};


    public Ram() {

    }

    public void setData(Ram f) {
        frames = f.frames;
        fireTableChanged(new TableModelEvent(this));
    }

    void reset(){
        frames.clear();
        fireTableChanged(new TableModelEvent(this));
    }

    public int addFrame(RamFrame f){
        frames.put(frames.size(),f);
        fireTableChanged(new TableModelEvent(this));

        return frames.size() - 1;
    }

    public int removeFrame(int framenummer){
        int pagenummer = frames.get(framenummer).getFramenummer();
        frames.remove(framenummer);
        fireTableChanged(new TableModelEvent(this));

        return pagenummer;
    }

    public int replaceFrame(int framenummer, RamFrame f){
        int pagenummer = frames.get(framenummer).getFramenummer();
        frames.replace(framenummer, f);
        fireTableChanged(new TableModelEvent(this));

        return pagenummer;
    }


    public void removePage(int pagenummer){
        RamFrame f = getFrame(pagenummer);
        frames.remove(f);
        fireTableChanged(new TableModelEvent(this));
    }

    public RamFrame getFrame(int framenummer){
        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getFramenummer() == framenummer){
                return f;
            }
        }
        return null;
    }
    public RamFrame getFrame(int framenummer, int process){
        for (Map.Entry<Integer, RamFrame> entry : frames.entrySet()) {
            RamFrame f = entry.getValue();
            if(f.getFramenummer() == framenummer && f.getProcessID() == process){
                return f;
            }
        }
        return null;
    }

    public boolean isFull(){
        return rows == frames.size();
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
            case 0 -> frame.setProcessID((Integer) value);
            case 1 -> frame.setFramenummer((Integer) value);
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
