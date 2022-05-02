public class RamFrame {

    private String processID;

    private String pagenummer;

    public RamFrame() {
    }

    public RamFrame(String pid, String pn) {
        processID = pid;
        pagenummer = pn;
    }

    public String getProcessID() {
        return processID;
    }

    public int getProcessIDInt() {
        return Integer.parseInt(processID);
    }


    public String getFramenummer() {
        return pagenummer;
    }

    public int getFramenummerInt() {
        return Integer.parseInt(pagenummer);
    }


    public void setProcessID(String pid) {
        processID = pid;
    }

    public void setFramenummer(String pn) {
        pagenummer = pn;
    }

}
