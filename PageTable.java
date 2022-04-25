import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.Map;
import java.util.Vector;


public class PageTable extends AbstractTableModel{
    public Vector data = new Vector();
    private final String[] columns = {"Present bit",
            "Modify bit",
            "Last access time",
            "Framenummer"};


    public PageTable() {}
    public PageTable(Vector  d) {
        super();
        data = d;
    }

    public int getColumnCount() {
        return columns.length;
    }
    public int getRowCount() {
        return data.size();
    }
    public void setValueAt(Object value, int row, int col) {
        Page page = (Page) (data.elementAt(row));

        switch (col) {
            case 0:
                page.setPresentBit((Integer) value);
                break;
            case 1:
                page.setModifyBit((Integer) value);
                break;
            case 2:
                page.setLastAccessTime((Integer) value);
                break;
            case 3:
                page.setFramenummer((Integer) value);
                break;
        }
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public Object getValueAt(int row, int col) {
        Page page = (Page) (data.elementAt(row));

        switch (col) {
            case 0:
                return page.getPresentBit();
            case 1:
                return page.getModifyBit();
            case 2:
                return page.getLastAccessTime();
            case 3:
                return page.getFramenummer();
        }

        return new String();
    }

    public void setData(Vector  d) {
        data = d;
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

}
