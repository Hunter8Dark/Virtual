import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.Map;
import java.util.Vector;


public class PageTable extends AbstractTableModel{
    public Vector data = new Vector();
    private final int rows = 16;
    private final String[] columns = {"Present bit",
            "Modify bit",
            "Last access time",
            "Framenummer"};


    public PageTable() {
        for(int i = 0; i < rows; i++){
            Page p = new Page(0,0,0, i);
            data.add(p);
        }
    }

    public void setData(Vector  d) {
        data = d;
        fireTableChanged(new TableModelEvent(this));
    }
    public void setData(PageTable pt) {
        data = pt.data;
        fireTableChanged(new TableModelEvent(this));
    }

    public void addPage(Page p){
        data.add(p);
        fireTableChanged(new TableModelEvent(this));
    }

    public void removePage(int framenummer){
        Page p = getPage(framenummer);
        data.remove(p);
        fireTableChanged(new TableModelEvent(this));
    }

    public void updatePage(){
        fireTableChanged(new TableModelEvent(this));
    }

    public Page getPage(int framenummer){
        for(Object i : data){
            Page p = (Page)i;
            if(p.getFramenummer() == framenummer){
                return p;
            }
        }
        return null;
    }

    boolean containsPage(int framenummer){
        if(getPage(framenummer) != null){
            return true;
        }
        return false;
    }


    //Standaard code voor JTable Template, niet verwijderen!
    public int getColumnCount() {
        return columns.length;
    }
    public int getRowCount() {
        return data.size();
    }
    public void setValueAt(Object value, int row, int col) {
        Page page = (Page) (data.elementAt(row));

        switch (col) {
            case 0 -> page.setPresentBit((Integer) value);
            case 1 -> page.setModifyBit((Integer) value);
            case 2 -> page.setLastAccessTime((Integer) value);
            case 3 -> page.setFramenummer((Integer) value);
        }
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public Object getValueAt(int row, int col) {
        Page page = (Page) (data.elementAt(row));

        return switch (col) {
            case 0 -> page.getPresentBit();
            case 1 -> page.getModifyBit();
            case 2 -> page.getLastAccessTime();
            case 3 -> page.getFramenummer();
            default -> new String();
        };

    }

}
