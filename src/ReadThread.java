import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class ReadThread implements Runnable {
    private BlockingQueue<Student> queue;

    public ReadThread(BlockingQueue<Student> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            // Đọc file student.xml và thêm các thông tin vào hàng đợi
            File file = new File("student.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();
//            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("student");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String id = element.getAttribute("id");
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                String address = element.getElementsByTagName("address").item(0).getTextContent();
                String dateOfBirth = element.getElementsByTagName("dateOfBirth").item(0).getTextContent();

                Student student = new Student(id, name, address, dateOfBirth);
                queue.put(student);
            }
        } catch (ParserConfigurationException | SAXException | IOException  e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(ReadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
