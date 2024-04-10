
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgeEncodeThread implements Runnable {

    private BlockingQueue<Student> readQueue;
    private BlockingQueue<String> encodeQueue;

    public AgeEncodeThread(BlockingQueue<Student> readQueue, BlockingQueue<String> encodeQueue) {
        this.readQueue = readQueue;
        this.encodeQueue = encodeQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Student student = readQueue.take();

                String dateOfBirth = student.getDateOfBirth();
                // Tính tuổi sinh viên
                LocalDate dob = LocalDate.parse(dateOfBirth);
                LocalDate today = LocalDate.now();
                Period period = Period.between(dob, today);

                int years = period.getYears();
                int months = period.getMonths();
                int days = period.getDays();
                // Giả sử mã hóa là đảo ngày tháng năm sinh
                StringBuilder encodedAge = new StringBuilder();
                for (char c : dateOfBirth.toCharArray()) {
                    if (Character.isDigit(c) && c!='0') {
                        encodedAge.append(c);
                    }
                }

                encodeQueue.put(encodedAge.toString());

                // In thông tin
                System.out.println("Tên sinh viên: " + student.getName());
                System.out.println("Tuổi: " + years + " năm " + months + " tháng " + days + " ngày");
                System.out.println("Mã hóa chữ số: " + encodedAge);
                System.out.println("");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}
