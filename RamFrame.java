public class RamFrame {

    private String processID;

    private String pagenummer;

    private int lastused;
    public RamFrame() {
        lastused = 1000000;
    }

    public RamFrame(String pid, String pn) {
        processID = pid;
        pagenummer = pn;
        lastused = 1000000;
    }

    public RamFrame(String pid, String pn, int l) {
        processID = pid;
        pagenummer = pn;
        lastused = l;
    }

    public String getProcessID() {
        return processID;
    }

    public int getProcessIDInt() {
        return Integer.parseInt(processID);
    }


    public String getPagenummer() {
        return pagenummer;
    }

    public int getPagenummerInt() {
        return Integer.parseInt(pagenummer);
    }

    public int getLastused(){return lastused;}

    public void setProcessID(String pid) {
        processID = pid;
    }

    public void setPagenummer(String pn) {
        pagenummer = pn;
    }

    public void setLastused(int l){lastused = l;}

}
