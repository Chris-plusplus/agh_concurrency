package tw.lab3;

import java.util.ArrayList;

public class Scenario1 {
    public static final int N = 8;
    public static final int M = 4;
    public static void main(String[] args) {
        var printers = new ArrayList<Printer>();
        for(int i = 0; i != M; ++i){
            printers.add(new Printer(i));
        }
        var printerMonitor = new PrintersMonitor(M);

        var threads = new ArrayList<Thread>();
        for(int i = 0; i != N; ++i){
            final int threadNum = i;
            threads.add(new Thread(() -> {
                for(int msgNum = 0;; ++msgNum){
                    String message = "%d wiadomość od wątku %d".formatted(msgNum, threadNum);
                    int printerNum = printerMonitor.reserve();
                    printers.get(printerNum).print(message);
                    printerMonitor.reuse(printerNum);
                }
            }));
        }

        for(var thread : threads){
            thread.start();
        }
        for(var thread : threads){
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }
}
