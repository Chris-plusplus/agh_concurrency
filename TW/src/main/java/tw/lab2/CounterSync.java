package tw.lab2;

public class CounterSync {
    private long value;
    BinarySemaphore semaphore = new BinarySemaphore();

    public void increment(){
        semaphore.opusc();
        ++value;
        semaphore.podnies();
    }
    public void decrement(){
        semaphore.opusc();
        --value;
        semaphore.podnies();
    }
    public long getValue(){
        return value;
    }
}
