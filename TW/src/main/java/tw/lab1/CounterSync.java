package tw.lab1;

public class CounterSync {
    private long value;

    public synchronized void increment(){
        ++value;
    }
    public synchronized void decrement(){
        --value;
    }
    public synchronized long getValue(){
        return value;
    }
}
