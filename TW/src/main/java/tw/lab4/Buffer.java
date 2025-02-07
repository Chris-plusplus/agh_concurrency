package tw.lab4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {

    private final List<Integer> cells;
    private final Lock lock = new ReentrantLock();
    private final List<Condition> conditions;

    public Buffer(int bufSize, int processors){
        cells = new ArrayList<>();
        conditions = new ArrayList<>();
        for(int i = 0; i != bufSize; ++i){
            cells.add(-1);
        }
        for(int i = 0; i != processors; ++i) {
            conditions.add(lock.newCondition());
        }
    }

    public void process(int cellIdx, int processorId){
        lock.lock();
        try{
            while (cells.get(cellIdx) != processorId - 1){
                conditions.get(processorId).await();
            }
            if(processorId + 1 < conditions.size()){
                cells.set(cellIdx, processorId);
                System.out.println("(%d@%d):\n%s".formatted(processorId, cellIdx, Arrays.toString(cells.toArray())));
                Thread.sleep(new Random().nextInt(50, 250));
                conditions.get(processorId + 1).signal();
            }
            else{
                System.out.println("Komórka %d = %d".formatted(cellIdx, cells.get(cellIdx)));
            }
        }
        catch (InterruptedException e) {}
        finally {
            lock.unlock();
        }
    }

    public int getSize() {
        return cells.size();
    }
}
