
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

    public static void main(String[] args) {
        BlockingQueue<Student> readQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> encodeQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>();

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Thread readThread = new Thread(new ReadThread(readQueue));
        Thread ageEncodeThread = new Thread(new AgeEncodeThread(readQueue, encodeQueue));
        Thread sumCheckThread = new Thread(new SumCheckThread(encodeQueue, resultQueue));


        executorService.submit(readThread);
        executorService.submit(ageEncodeThread);
        executorService.submit(sumCheckThread);
        
        // Tạo file kết quả
        createResultFile(resultQueue);
        
        // Đọc và giải mã kết quả từ file
//        readAndDecodeResultFile();

        executorService.shutdown();
    }

    private static void createResultFile(BlockingQueue<String> resultQueue) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            
            Element rootElement = doc.createElement("students");
            doc.appendChild(rootElement);

            while (!resultQueue.isEmpty()) {
                String[] resultData = resultQueue.take().split("\\|");

                Element studentElement = doc.createElement("student");
                
                Element ageElement = doc.createElement("age");
                ageElement.appendChild(doc.createTextNode(resultData[0]));
                studentElement.appendChild(ageElement);

                Element sumElement = doc.createElement("sum");
                sumElement.appendChild(doc.createTextNode(resultData[1]));
                studentElement.appendChild(sumElement);
                
                Element isDigitElement = doc.createElement("isDigit");
                isDigitElement.appendChild(doc.createTextNode(resultData[2]));
                studentElement.appendChild(isDigitElement);
                
                rootElement.appendChild(studentElement);
            }

            // Ghi dữ liệu ra file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            
            StreamResult result = new StreamResult(new File("kq.xml"));
            transformer.transform(source, result);

            System.out.println("File kết quả đã được tạo thành công.");
        } catch (ParserConfigurationException | TransformerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void readAndDecodeResultFile() {
        try {
            File file = new File("kq.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("student");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String age = element.getElementsByTagName("age").item(0).getTextContent();
                int sum = Integer.parseInt(element.getElementsByTagName("sum").item(0).getTextContent());
                boolean isDigit = Boolean.parseBoolean(element.getElementsByTagName("isDigit").item(0).getTextContent());

                System.out.println("Student " + (i + 1) + ":");
                System.out.println("Age: " + age);
                System.out.println("Sum: " + sum);
                System.out.println("IsDigit: " + isDigit);
                System.out.println();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
