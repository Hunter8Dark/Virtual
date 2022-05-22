import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import java.awt.*;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;


public class Main {

    static InstructionList list;
    static PageTableList pages = new PageTableList();

    static int instructionIndex = 0;
    static int pageSize =  4096;

    static PageTable ptmodel = new PageTable();
    static Ram ram = new Ram();

    static JTable pagetable = new JTable(ptmodel){
        public boolean editCellAt(int row, int column, java.util.EventObject e) {
            return false;
        }
    };
    static JTable ramtable = new JTable(ram){
        public boolean editCellAt(int row, int column, java.util.EventObject e) {
            return false;
        }
    };
    static JLabel instructionLabel = new JLabel("Systeemklok: 1", SwingConstants.CENTER);
    static JLabel nextTypeLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel prevTypeLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel nextVirtualLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel prevVirtualLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel nextFysicalLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel prevFysicalLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel nextFrameLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel prevFrameLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel nextOffsetLabel = new JLabel("?", SwingConstants.CENTER);
    static JLabel prevOffsetLabel = new JLabel("?", SwingConstants.CENTER);
    static JPanel ptpanel = new JPanel(null);


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, SAXException {
        list = getInstructionList("Instructions_30_3.xml");

        pagetable.getTableHeader().setReorderingAllowed(false);
        for(Integer i = 0; i < pagetable.getColumnCount(); i++){
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( JLabel.CENTER );
            pagetable.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }

        // Set page info via a toast on double mouseclick at table row
        pagetable.setFocusable(false);
        pagetable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {     // to detect doble click events
                    Page p = ptmodel.getPage(pagetable.getSelectedRow());
                    Toast message = new Toast("Page in: " + p.getIn() + " Page out: " + p.getOut());
                    message.display();
                }
            }
        });

        //Disable table editing
        ramtable.getTableHeader().setReorderingAllowed(false);
        for(Integer i = 0; i < ramtable.getColumnCount(); i++){
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( JLabel.CENTER );
            ramtable.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );

        }

        //Creating the Frame
        JFrame frame = new JFrame("Virtual memory");
        frame.setSize(new Dimension(600, 600));
        frame.setFocusable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();

        JMenu m1 = new JMenu("Run");
        mb.add(m1);
        JMenuItem m11 = new JMenuItem("Volgende instructie");
        JMenuItem m12 = new JMenuItem("Vorige instructie");
        JMenuItem m13 = new JMenuItem("Alle instructies");
        JMenuItem m14 = new JMenuItem("Reset");
        JMenuItem m15 = new JMenuItem("Specifieke instructie");
        m1.add(m11);
        m1.add(m12);
        m1.add(m15);
        m1.add(m13);
        m1.add(m14);
        m11.addActionListener(e -> runOneInstruction());
        m12.addActionListener(e -> rewindOneInstruction());
        m13.addActionListener(e -> runAllInstructions());
        m14.addActionListener(e -> reset());
        m15.addActionListener(e -> {
            String result = JOptionPane.showInputDialog(frame, "Geef systeemklok van de gewenste instructie in:");
            runSpecificInstruction(result);
        });

        KeyStroke nextKey = KeyStroke.getKeyStroke("RIGHT");
        m11.setAccelerator(nextKey);
        KeyStroke prevKey = KeyStroke.getKeyStroke("LEFT");
        m12.setAccelerator(prevKey);
        KeyStroke specifiekKey = KeyStroke.getKeyStroke("UP");
        m15.setAccelerator(specifiekKey);
        KeyStroke allKey = KeyStroke.getKeyStroke("A");
        m13.setAccelerator(allKey);
        KeyStroke resetKey = KeyStroke.getKeyStroke("R");
        m14.setAccelerator(resetKey);

        JMenu m2 = new JMenu("Set File");
        mb.add(m2);
        JMenuItem m21 = new JMenuItem("Set 1");
        JMenuItem m22 = new JMenuItem("Set 2");
        JMenuItem m23 = new JMenuItem("Set 3");
        m2.add(m21);
        m2.add(m22);
        m2.add(m23);
        m21.addActionListener(e -> { list = pickSet(1); reset();});
        m22.addActionListener(e -> { list = pickSet(2); reset();});
        m23.addActionListener(e -> { list = pickSet(3); reset();});

        Icon infoIcon = new ImageIcon("infoIcon.png");
        JButton infoButton = new JButton(infoIcon);
        infoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Een beetje info bij dit programma: \n\n " +
                    "Bovenaan in het menu heeft u twee keuzes: \n" +
                    "   - Run: hier kunt u instructies uitvoeren...\n" +
                    "   - Set: hier kunt de gewenste instructieset kiezen...\n\n" +
                    "U kunt deze knoppen ook bedienen met uw toetsen:\n" +
                    "   - Right arrow: run volgende instructie\n " +
                    "   - Left arrow: run vorige instructie\n" +
                    "   - Up arrow: run specifieke instructie die u moet ingeven\n" +
                    "   - A: run alle instructies in de huidige set\n" +
                    "   - R: reset alles\n\n" +
                    "De layout: \n" +
                    "   - Pagetable: hier verschijnt de pagetable van het huidige proces\n" +
                    "     Merk op: van welk proces de tabel is ziet u boven de tabel in de titel,\n" +
                    "              als een tabel verwijderd wordt zal deze leeg zijn en staat dit ook bovenaan\n" +
                    "              U kan ook dubbel klikken op een page in de pagetable om het aantal page ins en outs te zien\n" +
                    "   - Ram: de tabel met alle ramframes\n" +
                    "   - Info (onderaan): hier verschijnt alle info van de huidige en vorige instructie\n" +
                    "     Merk op: fysiek adres, frame en offset van volgende instructie worden nooit berekend!"
            );
        });
        Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
        infoButton.setBorder(emptyBorder);
        mb.add(Box.createHorizontalGlue());
        mb.add(infoButton);


        //Creating panel with PageTable and RAMTable
        JPanel tables = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 10, 10, 10);

        JScrollPane ptscroll = new JScrollPane((pagetable));
        ptscroll.setMinimumSize(new Dimension(100, 300));
        ptscroll.setPreferredSize(new Dimension(300, 450));
        ptscroll.setMaximumSize(new Dimension(300, 600));

        ptpanel.setLayout(new BoxLayout(ptpanel, BoxLayout.LINE_AXIS));
        ptpanel.add(ptscroll);
        ptpanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Pagetable Start", TitledBorder.CENTER,
                TitledBorder.TOP));

        tables.add(ptpanel, constraints);


        JScrollPane ramscroll = new JScrollPane((ramtable));
        ramscroll.setMinimumSize(new Dimension(100, 300));
        ramscroll.setPreferredSize(new Dimension(300, 450));
        ramscroll.setMaximumSize(new Dimension(300, 600));

        JPanel rampanel = new JPanel(null);
        rampanel.setLayout(new BoxLayout(rampanel, BoxLayout.LINE_AXIS));
        rampanel.add(ramscroll);
        rampanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "RAM Memory", TitledBorder.CENTER,
                TitledBorder.TOP));

        tables.add(rampanel, constraints);



        //Creating panel with info about instruction, next and previous instruction
        JPanel info = new JPanel(new GridLayout(4,0));
        info.add(instructionLabel);
        JPanel infoInstruction = new JPanel(new GridLayout(0,6));
        infoInstruction.add(new JLabel("", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Type", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Virtual", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Fysiek", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Frame", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Offset", SwingConstants.CENTER));
        info.add(infoInstruction);
        JPanel nextInstruction = new JPanel(new GridLayout(0,6));
        nextInstruction.add(new JLabel("Volgende: ", SwingConstants.CENTER));
        nextInstruction.add(nextTypeLabel);
        nextInstruction.add(nextVirtualLabel);
        nextInstruction.add(nextFysicalLabel);
        nextInstruction.add(nextFrameLabel);
        nextInstruction.add(nextOffsetLabel);
        info.add(nextInstruction);
        JPanel prevInstruction = new JPanel(new GridLayout(0,6));
        prevInstruction.add(new JLabel("Vorige: ", SwingConstants.CENTER));
        prevInstruction.add(prevTypeLabel);
        prevInstruction.add(prevVirtualLabel);
        prevInstruction.add(prevFysicalLabel);
        prevInstruction.add(prevFrameLabel);
        prevInstruction.add(prevOffsetLabel);
        info.add(prevInstruction);

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, tables);
        frame.getContentPane().add(BorderLayout.SOUTH, info);
        frame.setVisible(true);


        //Set the first info in the infopanel
        setInfo();
    }

    //Run only one instruction and adjust al the information
    public static void runOneInstruction(){
        //Check if already completed all instructions
        if(instructionIndex == list.instructions.size()){
            return;
        };


        //Get information of upcoming instruction
        Instruction currentInstruction = list.instructions.get( instructionIndex++ );
        int address = currentInstruction.address;
        int process = currentInstruction.processID;
        String operation = currentInstruction.operation;

        //Find pagetable from this process or make on if this is the first instruction from this process
        if(operation.equals("Start")){
            PageTable pageTable = new PageTable();

            pages.addPageTable(process,pageTable);
            ram.addProcess(process, pages);
        }
        else if(operation.equals("Write") || operation.equals("Read")){
            //Find the page in RAM, if not found run the LRU algorithm to put this page in RAM
            int pagenummer = getPagenummer(address);
            PageTable currentPagetable = pages.get(process);
            RamFrame currentFrame = ram.getFrame(pagenummer, process);

            if(currentFrame == null){
                RamFrame newFrame = new RamFrame(String.valueOf(process), String.valueOf(pagenummer));
                ram.addFrame(newFrame, pages);
            }

            currentPagetable.setLastAccesed(pagenummer,instructionIndex);

            if(operation.equals("Write")){
                currentPagetable.setModified(pagenummer);
            }

            //Updating the page table from this process
            ptmodel.setData(currentPagetable);
            ptpanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), "Pagetable Process " + process, TitledBorder.CENTER,
                    TitledBorder.TOP));

        }
        //If this is a termination instruction delete all its pages stored in RAM and also the process' pagetable
        else{
            ram.removeProcess(process, pages);
            pages.removePageTable(process);
            ptmodel.setData(new PageTable(true));
            ptpanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), "Pagetable " +process+" removed!", TitledBorder.CENTER,
                    TitledBorder.TOP));

        }

        //Setting the info
        setInfo();
        System.out.println(instructionIndex);
    }

    public static void runSpecificInstruction(String input){
        reset();

        if(isNumeric(input)){
            int index = Integer.parseInt(input);

            if(index == 0){
                setInfo();
                return;
            }
            else if(index > list.instructions.size()){
                index = list.instructions.size();
            }

            for(int i = 0; i < index; i++){
                runOneInstruction();
            }
        }
    }


    //Rewind one instruction by running al the instruction again till the previous one
    public static void rewindOneInstruction(){
        System.out.println(instructionIndex);

        int previousIndex = instructionIndex - 1;
        reset();

        if(previousIndex == 0){
            setInfo();
            return;
        }

        for(int i = 0; i < previousIndex; i++){
            runOneInstruction();
        }
    }

    //Run al the instructions left in the system
    public static void runAllInstructions(){
        for(int i = instructionIndex; i < list.instructions.size(); i++){
            runOneInstruction();
        }
    }

    //Pick a instruction set, used in the GUI
    public static InstructionList pickSet(int setNumber){
        if(setNumber == 1){
            return getInstructionList("Instructions_30_3.xml");
        }
        else if(setNumber == 2){
            return getInstructionList("Instructions_20000_4.xml");
        }
        else{
            return getInstructionList("Instructions_20000_20.xml");
        }
    }

    //Resetting the pagetables, RAM and info
    private static void reset() {
        instructionIndex = 0;
        ptmodel.setData(new PageTable());
        pages.reset();
        ram.reset();
        setInfo();
    }

    //Read the XML instruction set and convert it to a Instruction List
    public static InstructionList getInstructionList(String name){
        File file = new File(name);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            NodeList instructionXml = document.getElementsByTagName("instruction");
            InstructionList tmp = new InstructionList(instructionXml);
            return tmp;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Calculate the gamenummer from an address
    public static int getPagenummer(int address){
        return address / pageSize;
    }

    //Set al the info from the next and previous instruction if possible
    public static void setInfo(){
        //Get next and previous instruction
        Instruction prevInstruction = list.get(instructionIndex - 1);
        Instruction nextInstruction = list.get(instructionIndex);

        //If there is a next instruction set the info !!! offset and fysical adress can't be set because we don't know in which  RAMframe this will be stored
        if(nextInstruction != null){
            if(nextInstruction.operation.equals("Start") || nextInstruction.operation.equals("Terminate")){
                nextVirtualLabel.setText("/");
                nextTypeLabel.setText(nextInstruction.operation);
            }
            else{
                nextVirtualLabel.setText(Integer.toString(nextInstruction.address));
                nextTypeLabel.setText(nextInstruction.operation);
            }
        }
        else{
            nextVirtualLabel.setText("/");
            nextTypeLabel.setText("/");
        }
        //If there is a previous instruction set the info, here we calculate some things
        if(prevInstruction != null){
            if(prevInstruction.operation.equals("Start") || prevInstruction.operation.equals("Terminate")){
                prevTypeLabel.setText(prevInstruction.operation);
                prevVirtualLabel.setText("/");
                prevFysicalLabel.setText("/");
                prevFrameLabel.setText("/");
                prevOffsetLabel.setText("/");
            }
            else{
                int address = prevInstruction.address;
                int pagenummer = getPagenummer(prevInstruction.address);
                int framenummer = Integer.parseInt(ptmodel.getPage(pagenummer).getFysicalFramenummer());
                int offset = address - address / pageSize * pageSize;
                int faddress = framenummer * pageSize + offset;

                prevTypeLabel.setText(prevInstruction.operation);
                prevVirtualLabel.setText(Integer.toString(address));
                prevFysicalLabel.setText(Integer.toString(faddress));
                prevFrameLabel.setText(Integer.toString(framenummer));
                prevOffsetLabel.setText(Integer.toString(offset));
            }
        }
        else{
            prevVirtualLabel.setText("/");
            prevFysicalLabel.setText("/");
            prevFrameLabel.setText("/");
            prevOffsetLabel.setText("/");
            prevTypeLabel.setText("/");
        }

        instructionLabel.setText("Systeemklok: " + Integer.toString(instructionIndex + 1));
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}


