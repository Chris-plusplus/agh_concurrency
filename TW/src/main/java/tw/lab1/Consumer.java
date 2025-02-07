package tw.lab1;

public class Consumer implements Runnable {
    private Buffer buffer;

    private static final int ILOSC = 10;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {

        for(int i = 0;  i < ILOSC;   i++) {
            String message = buffer.take();

            System.out.println(message);
        }

    }
}
