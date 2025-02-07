package tw.lab2;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        var sem = new BinarySemaphore();

        var thread1 = new Thread(() -> {
            sem.opusc();
            for(int i = 0; i != 10; ++i){
                System.out.println("thread1 %d".formatted(i));
            }
            sem.podnies();
        });
        var thread2 = new Thread(() -> {
            sem.opusc();
            for(int i = 0; i != 10; ++i){
                System.out.println("thread2 %d".formatted(i));
            }
            sem.podnies();
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
