package tw.lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintersMonitor {
    private final Lock lock;
    private final Condition full;
    private final List<Boolean> avaliableList = new ArrayList<>();

    public PrintersMonitor(int count){
        lock = new ReentrantLock();
        full = lock.newCondition();
        for(int i = 0; i != count; ++i){
            avaliableList.add(true);
        }
    }

    public int reserve(){
        lock.lock();
        try{
            int avalNum = getAvaliable();
            while (avalNum == -1){
                full.await();
                avalNum = getAvaliable();
            }

            avaliableList.set(avalNum, false);
            return avalNum;
        } catch (InterruptedException ignored) {} finally {
            lock.unlock();
        }
        // nie dojdzie tutaj, ale kompilator wymaga
        return -1;
    }

    public void reuse(int num){
        lock.lock();
        try{
            avaliableList.set(num, true);
            full.signal();
        } finally {
            lock.unlock();
        }
    }

    private int getAvaliable() {
        for(int i = 0; i != avaliableList.size(); ++i){
            if(avaliableList.get(i)){
                return i;
            }
        }
        return -1;
    }
}
