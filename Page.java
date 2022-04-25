public class Page {

    private Integer presentBit;

    private Integer modifyBit;

    private Integer lastAccessTime;

    private Integer framenummer;

    public Page() {
    }

    public Page(Integer pb, Integer mb, Integer lat, Integer fn) {
        presentBit = pb;
        modifyBit = mb;
        lastAccessTime = lat;
        framenummer = fn;
    }

    public Integer getPresentBit() {
        return presentBit;
    }

    public Integer getModifyBit() {
        return modifyBit;
    }

    public Integer getLastAccessTime() {
        return lastAccessTime;
    }

    public Integer getFramenummer() {
        return framenummer;
    }


    public void setPresentBit(Integer pb) {
        presentBit = pb;
    }

    public void setModifyBit(Integer mb) {
        modifyBit = mb;
    }

    public void setLastAccessTime(Integer lat) {
        lastAccessTime = lat;
    }

    public void setFramenummer(Integer fn) {
        framenummer = fn;
    }

}
