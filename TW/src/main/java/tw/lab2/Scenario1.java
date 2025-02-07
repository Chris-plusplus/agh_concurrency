package tw.lab2;

import java.util.Random;

public class Scenario1 {
    static final int COUNT = 5;

    static int leftFork(int philIdx){
        return philIdx;
    }
    static int rightFork(int philIdx){
        return (philIdx + 1) % COUNT;
    }

    public static void main(String[] args) {
        BinarySemaphore[] forks = new BinarySemaphore[COUNT];
        for(int i = 0; i != COUNT; ++i){
            forks[i] = new BinarySemaphore();
        }

        Thread[] philosophers = new Thread[COUNT];
        for(int i = 0; i != COUNT; ++i){
            int finalI = i;
            philosophers[i] = new Thread(() -> {
                //try {
                    for (; ; ) {
                        // wez prawy widelec
                        //System.out.println("Filizof %d szuka prawego widelca".formatted(finalI));
                        forks[rightFork(finalI)].opusc();
                        System.out.println("Filizof %d podnosi prawy widelec".formatted(finalI));
                        //System.out.println("Filizof %d szuka lewego widelca".formatted(finalI));
                        // wez lewy widelec
                        forks[leftFork(finalI)].opusc();
                        //System.out.println("Filizof %d podnosi lewy widelec".formatted(finalI));

                        // jedz
                        //Thread.sleep(new Random().nextInt(10, 50));

                        // odstaw widelce
                        forks[rightFork(finalI)].podnies();
                        //System.out.println("Filizof %d odkłada prawy widelec".formatted(finalI));
                        forks[leftFork(finalI)].podnies();
                        System.out.println("Filizof %d odkłada widelce".formatted(finalI));
                    }
                //} catch (InterruptedException ignored) {}
            });
        }
        for(var thread : philosophers){
            thread.start();
        }
        for(var thread : philosophers){
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }
}
