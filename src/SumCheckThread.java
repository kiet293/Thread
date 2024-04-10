
import java.util.concurrent.BlockingQueue;

public class SumCheckThread implements Runnable {
    private BlockingQueue<Student> readQueue;
    private BlockingQueue<String> encodeQueue;
    private BlockingQueue<String> resultQueue;

    public SumCheckThread(BlockingQueue<String> encodeQueue, BlockingQueue<String> resultQueue) {
        this.encodeQueue = encodeQueue;
        this.resultQueue = resultQueue;
    }

    public SumCheckThread(BlockingQueue<Student> readQueue, BlockingQueue<String> encodeQueue, BlockingQueue<String> resultQueue) {
        this.readQueue = readQueue;
        this.encodeQueue = encodeQueue;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String encodedAge = encodeQueue.take();

                int sum = 0;
                for (int i = 0; i < encodedAge.length(); i++) {
                    sum += Character.getNumericValue(encodedAge.charAt(i));
                }

                // Kiểm tra xem tổng có phải là số nguyên tố không
                boolean isDigit = isPrime(sum);
                System.out.println("Tổng các chữ số trong ngày tháng năm sinh: " + sum);
                System.out.println("Là số nguyên tố: " + isDigit);
                System.out.println();
                // Gửi kết quả đến hàng đợi kết quả
                resultQueue.put(String.format("%s|%d|%s", encodedAge, sum, isDigit));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
