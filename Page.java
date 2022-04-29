public class Page {

    private int virtualPagenummer;

    private int presentBit;

    private int modifyBit;

    private int lastAccessTime;

    private String fysicalFramenummer;

    private int pageIn;
    private int pageOut;

    public Page(int vpn ,int pb, int mb, int lat, String ffn) {
        virtualPagenummer = vpn;
        presentBit = pb;
        modifyBit = mb;
        lastAccessTime = lat;
        fysicalFramenummer = ffn;
        pageIn = 0;
        pageOut = 0;
    }

    public int getPresentBit() {
        return presentBit;
    }

    public int getModifyBit() {
        return modifyBit;
    }

    public int getLastAccessTime() {
        return lastAccessTime;
    }

    public int getVirtualPagenummer(){ return virtualPagenummer; }

    public String getFysicalFramenummer() {
        return fysicalFramenummer;
    }


    public void setPresentBit(int pb) {
        presentBit = pb;
    }

    public void setModifyBit(int mb) {
        modifyBit = mb;
    }

    public void setLastAccessTime(int lat) { lastAccessTime = lat; }

    public void setVirtualPagenummer(int vpn) {virtualPagenummer = vpn;}

    public void setFysicalFramenummer(String ffn) {
        fysicalFramenummer = ffn;
    }

    public void addOut(){
        pageOut++;
    }
    public void addIn(){
        pageIn++;
    }

    public int getOut(){
        return pageOut;
    }
    public int getIn(){
        return pageIn;
    }

}
