import javax.swing.event.TableModelEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageTableList {

    public static Map<Integer,PageTable> pages = new HashMap<Integer, PageTable>();

    PageTableList(){

    }


    void addPageTable(int processID, PageTable pageTable){
        pages.put(processID, pageTable);
    }
    void updatePageTable(int processID, PageTable pageTable) {
        pages.replace(processID, pageTable);
    }
    void removePageTable(int processId){
        pages.remove(processId);
    }

    void removeFrameFromRam(RamFrame f, String emptyChar){
        if(f.getProcessID().equals(emptyChar) ||f.getPagenummer().equals(emptyChar)){
            return;
        }

        int processID = f.getProcessIDInt();
        int pagenummer = f.getPagenummerInt();

        pages.get(processID).removedFromRam(pagenummer);

    }

    void addFrameToRam(RamFrame f, int index, String emptyChar){
        if(f.getProcessID().equals(emptyChar) ||f.getPagenummer().equals(emptyChar)){
            return;
        }

        int processID = f.getProcessIDInt();
        int pagenummer = f.getPagenummerInt();

        pages.get(processID).addToRam(pagenummer, index);
    }

    static PageTable get(int processId){
        return pages.get(processId);
    }

    void reset(){
        pages.clear();
    }

}
