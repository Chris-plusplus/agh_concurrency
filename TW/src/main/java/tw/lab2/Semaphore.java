package tw.lab2;

public class Semaphore {
    //private final int max;
    private int value;

    public Semaphore(int value){
        //this.max = value;
        this.value = value;
    }

    public synchronized void opusc(){
        while (value == 0){
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }
        value -= 1;
        //notifyAll();
    }
    public synchronized void podnies(){
//        while (value == max){
//            try {
//                wait();
//            } catch (InterruptedException ignored) {}
//        }
        value += 1;
        notify();
    }
}
