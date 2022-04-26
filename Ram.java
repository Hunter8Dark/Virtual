import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class Ram extends AbstractTableModel{

    public static Vector<RamFrame> frames = new Vector<RamFrame>();
    private final int rows = 12;
    private final String[] columns = {"ProcessID", "Framenummer"};


    public Ram() {

    }

    public void setData(Vector  f) {
        frames = f;
        fireTableChanged(new TableModelEvent(this));
    }
    public void setData(Ram f) {
        frames = f.frames;
        fireTableChanged(new TableModelEvent(this));
    }

    void reset(){
        frames.clear();
        fireTableChanged(new TableModelEvent(this));
    }

    public void addFrame(RamFrame f){
        frames.add(f);
        fireTableChanged(new TableModelEvent(this));
    }

    public void removePage(int framenummer){
        RamFrame f =  getFrame(framenummer);
        frames.remove(f);
        fireTableChanged(new TableModelEvent(this));
    }

    public RamFrame getFrame(int framenummer){
        for(Object i : frames){
            RamFrame f = (RamFrame)i;
            if(f.getFramenummer() == framenummer){
                return f;
            }
        }
        return null;
    }
    public RamFrame getFrame(int framenummer, int process){
        for(Object i : frames){
            RamFrame f = (RamFrame)i;
            if(f.getFramenummer() == framenummer && f.getProcessID() == process){
                return f;
            }
        }
        return null;
    }

    public void sort(){
        Collections.sort(frames, new Comparator() {

            public int compare(Object o1, Object o2) {

                Integer x1 = ((RamFrame) o1).getProcessID();
                Integer x2 = ((RamFrame) o2).getProcessID();
                int sComp = x1.compareTo(x2);

                if (sComp != 0) {
                    return sComp;
                }

                Integer x3 = ((RamFrame) o1).getFramenummer();
                Integer x4 = ((RamFrame) o2).getFramenummer();
                return x3.compareTo(x4);
            }});
        fireTableChanged(new TableModelEvent(this));
    }


    //Standaard code voor JTable Template, niet verwijderen!
    public int getColumnCount() {
        return columns.length;
    }
    public int getRowCount() {
        return frames.size();
    }
    public void setValueAt(Object value, int row, int col) {
        RamFrame frame = (RamFrame) (frames.elementAt(row));

        switch (col) {
            case 0 -> frame.setProcessID((Integer) value);
            case 1 -> frame.setFramenummer((Integer) value);
        }
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public Object getValueAt(int row, int col) {
        RamFrame frame = (RamFrame) (frames.elementAt(row));

        return switch (col) {
            case 0 -> frame.getProcessID();
            case 1 -> frame.getFramenummer();
            default -> "";
        };

    }



}
