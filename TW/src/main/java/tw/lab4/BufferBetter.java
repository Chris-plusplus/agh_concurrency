package tw.lab4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferBetter {

    private final List<Integer> cells;
    private final List<Lock> cellLock;
    private final List<Condition> conditions;

    public BufferBetter(int bufSize, int processors){
        cells = new ArrayList<>();
        cellLock = new ArrayList<>();
        conditions = new ArrayList<>();
        for(int i = 0; i != bufSize; ++i){
            cells.add(-1);
            cellLock.add(new ReentrantLock());
        }
        for(int i = 0; i != processors; ++i) {
            conditions.add(cellLock.get(0).newCondition());
        }
    }

    public void process(int cellIdx, int processorId){
        cellLock.get(cellIdx).lock();
        try{
            while (cells.get(cellIdx) != processorId - 1){
                conditions.get(processorId).await();
            }
            if(processorId + 1 < conditions.size()){
                cells.set(cellIdx, processorId);
                if(cellIdx + 1 < cells.size()){
                    conditions.set(processorId, cellLock.get(cellIdx + 1).newCondition());
                }
                System.out.println("(%d@%d):\n%s".formatted(processorId, cellIdx, Arrays.toString(cells.toArray())));
                Thread.sleep(new Random().nextInt(50, 250));
                try {
                    conditions.get(processorId + 1).signal();
                }
                catch (Exception e) {}
            }
            else{
                System.out.println("Komórka %d = %d".formatted(cellIdx, cells.get(cellIdx)));
            }
        }
        catch (InterruptedException e) {}
        finally {
            cellLock.get(cellIdx).unlock();
        }
    }

    public int getSize() {
        return cells.size();
    }
}
