import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import java.awt.*;

import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


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
    static JLabel nextVirtualLabel = new JLabel("1", SwingConstants.CENTER);
    static JLabel prevVirtualLabel = new JLabel("1", SwingConstants.CENTER);
    static JLabel nextFysicalLabel = new JLabel("1", SwingConstants.CENTER);
    static JLabel prevFysicalLabel = new JLabel("1", SwingConstants.CENTER);
    static JLabel nextFrameLabel = new JLabel("1", SwingConstants.CENTER);
    static JLabel prevFrameLabel = new JLabel("1", SwingConstants.CENTER);
    static JLabel nextOffsetLabel = new JLabel("1", SwingConstants.CENTER);
    static JLabel prevOffsetLabel = new JLabel("1", SwingConstants.CENTER);


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, SAXException {
        list = getInstructionList("Instructions_30_3.xml");
        pagetable.getTableHeader().setReorderingAllowed(false);
        ramtable.getTableHeader().setReorderingAllowed(false);

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
        m1.add(m11);
        m1.add(m12);
        m1.add(m13);
        m1.add(m14);
        m11.addActionListener(e -> runOneInstruction());
        m12.addActionListener(e -> rewindOneInstruction());
        m13.addActionListener(e -> runAllInstructions());
        m14.addActionListener(e -> reset());

        KeyStroke nextKey = KeyStroke.getKeyStroke("RIGHT");
        m11.setAccelerator(nextKey);
        KeyStroke prevKey = KeyStroke.getKeyStroke("LEFT");
        m12.setAccelerator(prevKey);

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

        JPanel ptpanel = new JPanel(null);
        ptpanel.setLayout(new BoxLayout(ptpanel, BoxLayout.LINE_AXIS));
        ptpanel.add(ptscroll);
        ptpanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Pagetable", TitledBorder.CENTER,
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
        JPanel infoInstruction = new JPanel(new GridLayout(0,5));
        infoInstruction.add(new JLabel("", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Virtual", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Fysiek", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Frame", SwingConstants.CENTER));
        infoInstruction.add(new JLabel("Offset", SwingConstants.CENTER));
        info.add(infoInstruction);
        JPanel nextInstruction = new JPanel(new GridLayout(0,5));
        nextInstruction.add(new JLabel("Volgende: ", SwingConstants.CENTER));
        nextInstruction.add(nextVirtualLabel);
        nextInstruction.add(nextFysicalLabel);
        nextInstruction.add(nextFrameLabel);
        nextInstruction.add(nextOffsetLabel);
        info.add(nextInstruction);
        JPanel prevInstruction = new JPanel(new GridLayout(0,5));
        prevInstruction.add(new JLabel("Vorige: ", SwingConstants.CENTER));
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

    public static void runOneInstruction(){
        if(instructionIndex == list.instructions.size()){
            return;
        };

        Instruction currentInstruction = list.instructions.get( instructionIndex++ );
        int address = currentInstruction.address;
        int process = currentInstruction.processID;
        String operation = currentInstruction.operation;

        PageTable instructionIndexTable;
        if(operation.equals("Start")){
            PageTable pageTable = new PageTable();
            instructionIndexTable = pageTable;
            pages.addPageTable(process,pageTable);
        }
        else{
            instructionIndexTable = PageTableList.get(process);
        }

        int pageFramenummer = getPageframe(address);
        RamFrame currentFrame = ram.getFrame(pageFramenummer, process);
        if(currentFrame == null){
            RamFrame newFrame = new RamFrame(process, pageFramenummer);
            ram.addFrame(newFrame);
        }

        ptmodel.setData(instructionIndexTable);

        if(operation.equals("Terminate")){
            pages.removePageTable(process);
        }

        setInfo();
        System.out.println(instructionIndex);
    }


    public static void rewindOneInstruction(){
        System.out.println(instructionIndex);

        int previousIndex = instructionIndex - 1;
        instructionIndex = 0;


        if(previousIndex == 0){
            setInfo();
            return;
        }

        for(int i = 0; i < previousIndex; i++){
            runOneInstruction();
        }
    }

    public static void runAllInstructions(){
        for(int i = instructionIndex; i < list.instructions.size(); i++){
            runOneInstruction();
        }
        ram.sort();
    }

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

    private static void reset() {
        instructionIndex = 0;
        pages.reset();
        ram.reset();
        setInfo();
    }

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

    public static int getPageframe(int address){
        return address / pageSize;
    }

    public static void setInfo(){
        Instruction prevInstruction = list.get(instructionIndex - 1);
        Instruction nextInstruction = list.get(instructionIndex);

        if(nextInstruction != null){
            nextVirtualLabel.setText(Integer.toString(nextInstruction.address));
            //nextFysicalLabel.setText(Integer.toString());
            nextFrameLabel.setText(Integer.toString(getPageframe(nextInstruction.address)));
            //nextOffsetLabel.setText(Integer.toString());
        }
        else{
            nextVirtualLabel.setText("/");
            nextFysicalLabel.setText("/");
            nextFrameLabel.setText("/");
            nextOffsetLabel.setText("/");
        }
        if(prevInstruction != null){
            prevVirtualLabel.setText(Integer.toString(prevInstruction.address));
            //prevFysicalLabel.setText(Integer.toString());
            prevFrameLabel.setText(Integer.toString(getPageframe(prevInstruction.address)));
            //prevOffsetLabel.setText(Integer.toString());
        }
        else{
            prevVirtualLabel.setText("/");
            prevFysicalLabel.setText("/");
            prevFrameLabel.setText("/");
            prevOffsetLabel.setText("/");
        }

        instructionLabel.setText("Systeemklok: " + Integer.toString(instructionIndex + 1));
    }


    /*public static void FCFS(NodeList nodelist){
        System.out.println("Running FCFS...");
        Processes processes = new Processes(nodelist);

        int grootte = processes.processes.size();
        int done = 0;
        System.out.print("0%");

        processes.sortArrivaltime();

        int time = 0;
        for(Process process : processes.processes){
            if(time < process.arrivaltime){
                time = process.arrivaltime;
            }

            time += process.servicetime;
            process.await(time);
            done++;
            System.out.print("\r" + done/grootte*100 +"%");
        }

        PlotGraphs(processes,"FCFS");
    }*/
}


