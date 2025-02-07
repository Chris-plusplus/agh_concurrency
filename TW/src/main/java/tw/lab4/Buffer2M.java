package tw.lab4;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer2M {

    private final Lock lock = new ReentrantLock();
    private final int M;
    private final int PK;
    private int size = 0;
    private FileWriter fileProducer;
    private FileWriter fileConsumer;

    private final Condition producerCondition = lock.newCondition();
    private final Condition consumerCondition = lock.newCondition();

    public Buffer2M(int M, int P, int K){
        this.PK = P;
        this.M = M;
        try {
            // format plkików:
            // amount <tab> time_ns <new-line>
            this.fileProducer = new FileWriter("producer_M_%d_P_%d_K_%d.txt".formatted(M, P, K));
            this.fileConsumer = new FileWriter("consumer_M_%d_P_%d_K_%d.txt".formatted(M, P, K));
        } catch (IOException e) {}
    }

    public void produce(int amount){
        boolean starved = false;
        var start = System.nanoTime();
        lock.lock();
        try{
            while (size + amount > 2*M){
                if(!producerCondition.await(100L * PK, TimeUnit.MILLISECONDS)){
                    // wątek został zagłodzony
                    starved = true;
                    throw new IllegalStateException();
                }
            }

            size += amount;
            consumerCondition.signalAll();
        }
        catch (IllegalStateException e) {
            try {
                fileProducer.write("%d\tstarved\n".formatted(amount));
                fileProducer.flush();
            } catch (IOException e2) {}
        }
        catch (Exception e) {}
        finally {
            lock.unlock();
            var end = System.nanoTime();
            if(!starved){
                try {
                    fileProducer.write("%d\t%d\n".formatted(amount, end - start));
                    fileProducer.flush();
                } catch (IOException e) {}
            }
        }
    }

    public void consume(int amount){
        var start = System.nanoTime();
        boolean starved = false;
        lock.lock();
        try{
            while (size < amount){
                if(!consumerCondition.await(100L * PK, TimeUnit.MILLISECONDS)){
                    // wątek został zagłodzony
                    starved = true;
                    throw new IllegalStateException();
                }
            }

            size -= amount;
            producerCondition.signalAll();
        }
        catch (IllegalStateException e) {
            try {
                fileConsumer.write("%d\tstarved\n".formatted(amount));
                fileConsumer.flush();
            } catch (IOException e2) {}
        }
        catch (InterruptedException e) {}
        finally {
            lock.unlock();
            var end = System.nanoTime();
            if(!starved){
                try {
                    fileConsumer.write("%d\t%d\n".formatted(amount, end - start));
                    fileConsumer.flush();
                } catch (IOException e) {}
            }
        }
    }
}
