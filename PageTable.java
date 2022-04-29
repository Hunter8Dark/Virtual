import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class PageTable extends AbstractTableModel{
    public Map<Integer,Page> data = new HashMap<>();
    private final int rows = 16;
    private final String[] columns = {"VPN",
            "Present bit",
            "Modify bit",
            "Last access time",
            "FPN"};


    public PageTable() {
        for(int i = 0; i < rows; i++){
            Page p = new Page(i,0,0,0, "-");
            data.put(i, p);
        }
    }

    public PageTable(int process) {
        for(int i = 0; i < rows; i++){
            Page p = new Page(i, 0 ,0,0, "-");
            data.put(i, p);
        }
    }

    public void setData(PageTable pt) {
        data = pt.data;
        fireTableChanged(new TableModelEvent(this));
    }

    public void addPage(Page p){
        data.put(data.size(), p);
        fireTableChanged(new TableModelEvent(this));
    }

    public void removePage(int pagenummer){
        Page p = getPage(pagenummer);
        data.remove(p);
        fireTableChanged(new TableModelEvent(this));
    }

    public void updatePage(){
        fireTableChanged(new TableModelEvent(this));
    }

    public Page getPage(int pagenummer){
        for (Map.Entry<Integer, Page> entry : data.entrySet()) {
            Page p = entry.getValue();
            if(p.getVirtualPagenummer() == pagenummer){
                return p;
            }
        }
        return null;
    }


    //Standaard code voor JTable Template, niet verwijderen!
    public int getColumnCount() {
        return columns.length;
    }
    public int getRowCount() {
        return data.size();
    }
    public void setValueAt(Object value, int row, int col) {
        Page page = (Page) (data.get(row));

        switch (col) {
            case 0 -> page.setVirtualPagenummer((Integer) value);
            case 1 -> page.setPresentBit((Integer) value);
            case 2 -> page.setModifyBit((Integer) value);
            case 3 -> page.setLastAccessTime((Integer) value);
            case 4 -> page.setFysicalFramenummer((String) value);
        }
        fireTableChanged(new TableModelEvent(this));
    }

    public void setValue(Object value, int pagenummer, int col) {
        Page page = getPage(pagenummer);

        switch (col) {
            case 0 -> page.setVirtualPagenummer((Integer) value);
            case 1 -> page.setPresentBit((Integer) value);
            case 2 -> page.setModifyBit((Integer) value);
            case 3 -> page.setLastAccessTime((Integer) value);
            case 4 -> page.setFysicalFramenummer((String) value);
        }
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public Object getValueAt(int row, int col) {
        Page page = (Page) (data.get(row));

        return switch (col) {
            case 0 -> page.getVirtualPagenummer();
            case 1 -> page.getPresentBit();
            case 2 -> page.getModifyBit();
            case 3 -> page.getLastAccessTime();
            case 4 -> page.getFysicalFramenummer();
            default -> new String();
        };

    }

}
