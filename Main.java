import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import java.awt.*;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


public class Main {

    public static InstructionList list;
    public static PageTableList pages = new PageTableList();

    public static int currentPage = 0;
    static int pageSize =  4096;
    static PageTable model = new PageTable();
    static JTable table = new JTable(model);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, SAXException {
        list = getInstructionList("Instructions_30_3.xml");

        //Creating the Frame
        JFrame frame = new JFrame("Virtual memory");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();

        JMenu m1 = new JMenu("Run");
        mb.add(m1);
        JMenuItem m11 = new JMenuItem("1 instructie");
        JMenuItem m12 = new JMenuItem("Alle instructies");
        JMenuItem m13 = new JMenuItem("Print");
        m1.add(m11);
        m1.add(m12);
        m1.add(m13);
        m11.addActionListener(e -> runOneInstruction());
        m12.addActionListener(e -> runAllInstructions());
        m13.addActionListener(e -> System.out.println(list.instructions.size()));

        JMenu m2 = new JMenu("Set File");
        mb.add(m2);
        JMenuItem m21 = new JMenuItem("Set 1");
        JMenuItem m22 = new JMenuItem("Set 2");
        JMenuItem m23 = new JMenuItem("Set 3");
        m2.add(m21);
        m2.add(m22);
        m2.add(m23);
        m21.addActionListener(e -> list = pickSet(1));
        m22.addActionListener(e -> list = pickSet(2));
        m23.addActionListener(e -> list = pickSet(3));

        JScrollPane scrollPanel = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Text Area at the Center
        JTextArea ta = new JTextArea();

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPanel);
        frame.setVisible(true);
    }

    public static void runOneInstruction(){
        Instruction currentInstruction = list.instructions.get( currentPage++ );
        int address = currentInstruction.address;
        int process = currentInstruction.processID;
        String operation = currentInstruction.operation;

        PageTable currentPageTable = null;
        if(operation.equals("Start")){
            PageTable pageTable = new PageTable();
            currentPageTable = pageTable;
            pages.addPageTable(process,pageTable);
        }
        else if(operation.equals("Terminate")){
            pages.removePageTable(process);
        }
        else if(operation.equals("Write")){
            currentPageTable = PageTableList.get(process);
        }
        else{
            currentPageTable = PageTableList.get(process);
        }

        int pageFramenummer = getPageframe(address);
        Page currentPage = currentPageTable.getPage(pageFramenummer);
        if(currentPage == null){
            Page newPage = new Page(0,0,0, pageFramenummer);
            currentPageTable.addPage(newPage);
        }

        pages.updatePageTable(process, currentPageTable);
        currentPageTable.updatePage();

        for (Map.Entry<Integer, PageTable> entry : pages.pages.entrySet()) {
            for(Object i : entry.getValue().data){
                System.out.println(entry.getKey()+ "  " + ((Page)i).getFramenummer());
            }
            System.out.println("---------------------------");
        }





    }

    public static void runAllInstructions(){
        for(int i = currentPage; i < list.instructions.size(); i++){
            Instruction currentInstruction = list.instructions.get( i );
            int address = currentInstruction.address;
            System.out.println(getPageframe(address));
        }
    }

    public static InstructionList pickSet(int setNumber){
        currentPage = 0;
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


