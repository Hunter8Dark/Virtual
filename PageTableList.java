import javax.swing.event.TableModelEvent;
import java.util.HashMap;
import java.util.Map;

public class PageTableList {

    public static Map<Integer,PageTable> pages = new HashMap<Integer, PageTable>();

    PageTableList(){

    }

    void addPageTable(int processID, PageTable pageTable){
        pages.put(processID, pageTable);
        pageTable.updatePage();
    }
    void updatePageTable(int processID, PageTable pageTable) {
        pages.replace(processID, pageTable);
        pageTable.updatePage();
    }
    void removePageTable(int processId){
        pages.remove(processId);
    }

    void getPageTable(int processID){
        pages.remove(processID);
    }

    static PageTable get(int processId){
        return pages.get(processId);
    }

    void reset(){
        pages.clear();
    }
}
