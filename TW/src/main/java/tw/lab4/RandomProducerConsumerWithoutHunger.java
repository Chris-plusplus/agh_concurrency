package tw.lab4;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RandomProducerConsumerWithoutHunger {

    private static class Buffer {
        private ReentrantLock lock = new ReentrantLock();
        private int maxCapacity;
        private int counter = 0;
        private FileWriter fileWriter;
        private Condition[] conditions = new Condition[4];

        public Buffer(int limit, FileWriter fileWriter) {
            this.maxCapacity = 2 * limit;
            this.fileWriter = fileWriter;
            for (int i = 0; i < 4; i++) {
                conditions[i] = lock.newCondition();
            }
        }

        public void get(int amount) {
            lock.lock();
            try {
                long start = System.nanoTime();
                if (lock.hasWaiters(conditions[2])) {
                    conditions[3].await();
                }
                while (counter < amount) {
                    if (!conditions[2].await(100, TimeUnit.MILLISECONDS)) {
                        conditions[3].signal();
                        return;
                    }
                }
                long end = System.nanoTime();
                counter -= amount;
                fileWriter.append("Consumer," + amount + "," + (end - start) + "\n");
                conditions[0].signal();
                conditions[3].signal();
            } catch (InterruptedException e) {
                try {
                    fileWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void put(int amount) {
            lock.lock();
            try {
                long start = System.nanoTime();
                if (lock.hasWaiters(conditions[0])) {
                    conditions[1].await();
                }
                while (counter + amount > maxCapacity) {
                    if (!conditions[0].await(100, TimeUnit.MILLISECONDS)) {
                        conditions[1].signal();
                        return;
                    }
                }
                long end = System.nanoTime();
                counter += amount;
                fileWriter.append("Producer," + amount + "," + (end - start) + "\n");
                conditions[2].signal();
                conditions[1].signal();
            } catch (InterruptedException e) {
                try {
                    fileWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }

    private abstract static class Worker implements Runnable {
        protected Buffer buffer;
        private int limit;

        public Worker(Buffer buffer, int limit) {
            this.buffer = buffer;
            this.limit = limit;        }

        public void run() {
            int randomInt = (int) (Math.random() * limit);
            doIt(randomInt);
        }

        public abstract void doIt(int randomInt);

    }

    private static class Consumer extends Worker {

        public Consumer(Buffer buffer, int limit) {
            super(buffer, limit);
        }

        @Override
        public void doIt(int randomInt) {
            this.buffer.get(randomInt);
        }

    }

    private static class Producer extends Worker {

        public Producer(Buffer buffer, int limit) {
            super(buffer, limit);
        }

        @Override
        public void doIt(int randomInt) {
            this.buffer.put(randomInt);
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FileWriter fileWriter = new FileWriter("secondResults.csv");
        go(1000, 10, fileWriter);
        go(10000, 100, fileWriter);
        go(100000, 1000, fileWriter);
        fileWriter.close();
    }

    private static void go(int bufferSize, int numberOfWorkers, FileWriter fileWriter)
            throws IOException, InterruptedException {
        Buffer buffer = new Buffer(bufferSize, fileWriter);
        Thread[] workers = new Thread[numberOfWorkers * 2];
        for (int i = 0; i < numberOfWorkers; i++) {
            workers[i] = new Thread(new Producer(buffer, bufferSize));
            workers[i + numberOfWorkers] = new Thread(new Consumer(buffer, bufferSize));
        }
        for (int i = 0; i < 2 * numberOfWorkers; i++) {
            workers[i].start();
        }
        for (int i = 0; i < 2 * numberOfWorkers; i++) {
            workers[i].join();
        }
    }

}

