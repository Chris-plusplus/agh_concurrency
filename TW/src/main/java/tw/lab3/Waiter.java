package tw.lab3;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Waiter {
    private final Lock lock;
    private final Condition reservedCondition;
    private int pairNum = -1;
    private final Map<Integer, Condition> waitFor2nd = new HashMap<>();
    private Map<Integer, Boolean> both = new HashMap<>();

    public Waiter(){
        lock = new ReentrantLock();
        reservedCondition = lock.newCondition();
    }

    public void reserveTable(int pairNum){
        lock.lock();
        try{
            this.waitFor2nd.putIfAbsent(pairNum, lock.newCondition());
            both.putIfAbsent(pairNum, true);

            final var waitFor2nd = this.waitFor2nd.get(pairNum);

            // both = !both
            // second = both
            final boolean second = both.compute(pairNum, (k, v) -> !v);

            // pierwszy czeka dopóki drugi nie da mu znać
            while (!both.get(pairNum)) {
                waitFor2nd.await();
            }

            // drugi doszedł do pierwszego
            // próbuje rezerwować
            if(second){
                // while(reserved)
                while(this.pairNum != -1){
                    reservedCondition.await();
                }
                this.pairNum = pairNum;
                // zarezerwowane, dajemy znać pierwszemu
                waitFor2nd.signal();
            }
        } catch (InterruptedException ignored) {}
        finally {
            lock.unlock();
        }
    }

    public void reuseTable(){
        lock.lock();
        try{
            if(both.compute(pairNum, (k, v) -> !v)){
                pairNum = -1;
                reservedCondition.signal();
            }
        }
        finally {
            lock.unlock();
        }
    }
}
