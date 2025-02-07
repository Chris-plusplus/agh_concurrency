package tw.lab4;

import java.util.ArrayList;

public class Scenario1 {

    private static final int PROCESSORS_COUNT = 5;

    public static void main(String[] args) {
        var buffer = new BufferBetter(20, PROCESSORS_COUNT + 2);

        var producent = new Thread(() -> {
            for(int i = 0; i != buffer.getSize(); ++i){
                buffer.process(i, 0);
            }
        });

        var processors = new ArrayList<Thread>();
        for(int i = 0; i != PROCESSORS_COUNT; ++i){
            final int finalI = i;
            processors.add(new Thread(() -> {
                for(int i2 = 0; i2 != buffer.getSize(); ++i2){
                    buffer.process(i2, finalI + 1);
                }
            }));
        }

        var consumer = new Thread(() -> {
            for(int i = 0; i != buffer.getSize(); ++i){
                buffer.process(i, PROCESSORS_COUNT + 1);
            }
        });

        producent.start();
        for(var processor : processors){
            processor.start();
        }
        consumer.start();

        try {
            producent.join();
            for(var processor : processors){
                processor.join();
            }
            consumer.join();
        } catch (InterruptedException e) {}
    }
}
