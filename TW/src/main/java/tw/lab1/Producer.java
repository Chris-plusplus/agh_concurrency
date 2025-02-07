package tw.lab1;

public class Producer implements Runnable {
    private Buffer buffer;

    private static final int ILOSC = 10;

    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {

        for(int i = 0;  i < ILOSC;   i++) {
            buffer.put("message "+i);
        }

    }
}
