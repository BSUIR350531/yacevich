import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

import java.awt.*;
import javax.swing.*;
import javax.xml.stream.XMLStreamException;

public class FirstClass extends JFrame {


    JPanel panel;
    String filename;
    public JTextArea textArea = new JTextArea();

    public FirstClass() throws ParserConfigurationException, IOException, SAXException, XMLStreamException {
        super("Reader");
        initPanel();
        initListeners();
    }

    public void Newlbl(String str) {
        textArea.setText(textArea.getText() + str);
    }

    public void setTextWithEnter(String str) {
        textArea.setText(textArea.getText() + "\n" + "      " + str);
    }


    public void initPanel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setFocusable(true);
        textArea.setEditable(false);
        textArea.setCursor(null);
        textArea.setOpaque(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        panel.add(textArea, BorderLayout.NORTH);

        final JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 600));
        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void initListeners() throws ParserConfigurationException, IOException, SAXException, XMLStreamException{
        panel.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 79 && e.isControlDown()) {
                    try {
                        createChoiceDialog();
                    } catch (ParserConfigurationException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (SAXException e1) {
                        e1.printStackTrace();
                    } catch (XMLStreamException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void createChoiceDialog() throws ParserConfigurationException, IOException, SAXException, XMLStreamException {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "FB2 & EPUB files", "fb2", "epub");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filename = selectedFile.getAbsolutePath();
            if (getFileExtension(selectedFile.getName()).equals("fb2")) {
                run(filename);
            }
            else if (getFileExtension(selectedFile.getName()).equals("epub")) {
                Epub pub = new Epub(filename);
            }
            else System.out.println("Errorwrongfileextention");
        }
    }

    private String getFileExtension(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    private static void run (String filename) throws ParserConfigurationException, IOException, SAXException, XMLStreamException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new File(filename));
        document.getDocumentElement().normalize();

        NodeList nList = document.getElementsByTagName("body");

        FirstClass app = new FirstClass();
        app.textArea.setText("");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                app.Newlbl(eElement.getTextContent());
            }
        }
        app.setVisible(true);
        app.pack();
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, XMLStreamException {
        run("Hobbit.fb2");
    }
}