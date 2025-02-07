package tw.lab1;

public class Counter {
    private long value;

    public void increment(){
        ++value;
    }
    public void decrement(){
        --value;
    }
    public long getValue(){
        return value;
    }
}
