package tw.lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scenario2_1 {
    public static void main(String[] args) {
        var seed = new Random().nextLong();
        System.out.println(seed);
        doFor(1_000, 10, 10, seed);
        doFor(10_000, 100, 100, seed);
        doFor(100_000, 1_000, 1_000, seed);
    }

    public static void doFor(int M, int P, int K, long seed){
        var buffer = new Buffer2M(M, P, K);
        var random = new Random(seed);

        List<Thread> producers = new ArrayList<>();
        for(int i = 0; i != P; ++i){
            producers.add(new Thread(() -> {
                // nextInt() należy do [1; M]
                buffer.produce(random.nextInt(1, M + 1));
            }));
        }
        List<Thread> consumers = new ArrayList<>();
        for(int i = 0; i != P; ++i){
            consumers.add(new Thread(() -> {
                // nextInt() należy do [1; M]
                buffer.consume(random.nextInt(1, M + 1));
            }));
        }

        for(var th : producers){
            th.start();
        }
        for(var th : consumers){
            th.start();
        }
        for(var th : producers){
            try {
                th.join();
            } catch (InterruptedException e) {}
        }
        for(var th : consumers){
            try {
                th.join();
            } catch (InterruptedException e) {}
        }
        System.out.println("%d done".formatted(M));
    }
}
