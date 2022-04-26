public class RamFrame {

    private Integer processID;

    private Integer framenummer;

    public RamFrame() {
    }

    public RamFrame(Integer pid, Integer fn) {
        processID = pid;
        framenummer = fn;
    }

    public Integer getProcessID() {
        return processID;
    }

    public Integer getFramenummer() {
        return framenummer;
    }


    public void setProcessID(Integer pid) {
        processID = pid;
    }

    public void setFramenummer(Integer fn) {
        framenummer = fn;
    }

}
