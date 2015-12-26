import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipFile;

public class Epub {

    ArrayList<String> list = new ArrayList<String>();

    public Epub (String filename) throws ParserConfigurationException, IOException, SAXException, XMLStreamException {
        runEpub(filename);
    }

    private void runEpub(String filename) throws XMLStreamException, ParserConfigurationException, IOException, SAXException {

        ZipFile zipFile = new ZipFile(filename);
        InputStream stream = zipFile.getInputStream(zipFile.getEntry("META-INF/container.xml"));

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(stream);

        returnINF(reader, "rootfile", "full-path");
        String contentPath = list.get(0);

        stream = zipFile.getInputStream(zipFile.getEntry(contentPath));
        reader = inputFactory.createXMLStreamReader(stream);
        returnINF(reader, "itemref", "idref");

        stream = zipFile.getInputStream(zipFile.getEntry(contentPath));
        reader = inputFactory.createXMLStreamReader(stream);

        HTMLPathAdder(reader, "item", pathCheker(contentPath));
        FirstClass app = new FirstClass();
        app.textArea.setText("");

        for (int i = 0; i < list.size(); i++) {
            reader = inputFactory.createXMLStreamReader(stream);
            writeText(reader, app);
        }
        app.setVisible(true);
        app.pack();
    }

    private void writeText(XMLStreamReader reader, FirstClass app) throws XMLStreamException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.CHARACTERS:
                    if (reader.isWhiteSpace())
                        break;
                    app.setTextWithEnter(reader.getText());
                    break;
            }
        }
    }

    private void HTMLPathAdder(XMLStreamReader reader, String item, String addPath) throws XMLStreamException {
        String elementName;
        String AttribVal;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    elementName = reader.getLocalName();
                    if (elementName.equals(item)) {
                        AttribVal = reader.getAttributeValue(null, "id");
                        for (int i = 0; i < list.size(); i++) {
                            if (AttribVal.equals(list.get(i))) {
                                list.set(i, addPath + reader.getAttributeValue(null, "href"));
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void returnINF(XMLStreamReader reader, String LocalName, String AttributeVal) throws XMLStreamException {
        list.clear();
        String elementName;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    elementName = reader.getLocalName();
                    if (elementName.equals(LocalName)) {
                        list.add(reader.getAttributeValue(null, AttributeVal));
                    }
                    break;
            }
        }
    }

    private String pathCheker(String contentPath) {
        if (contentPath.equals("content.opf")) {
            return "";
        } else {
            contentPath = contentPath.substring(0, contentPath.length() - "content.opf".length());
            return contentPath;
        }
    }
}
