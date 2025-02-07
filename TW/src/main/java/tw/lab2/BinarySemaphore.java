package tw.lab2;

public class BinarySemaphore {
    private boolean value;

    public BinarySemaphore(){
        this.value = true;
    }

    public synchronized void opusc(){
        while (!value){
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }
        value = false;
    }
    public synchronized void podnies(){
        value = true;
        notify();
    }
}
