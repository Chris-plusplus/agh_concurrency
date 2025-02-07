package tw.lab1;

public class Buffer {
    private String storage = "";
    public synchronized void put(String str){
        //System.out.println("buf.put(): " + storage.isEmpty());
        try {
            while(!storage.isEmpty()){
                //System.out.println("buf.put() waits ");
                wait();
            }
        }
        catch (InterruptedException exception){}
        storage = storage + str;
        notifyAll();
    }
    public synchronized String take(){
        //System.out.println("buf.take(): " + storage.isEmpty());
        try {
            while(storage.isEmpty()){
                //System.out.println("buf.take() waits");
                wait();
            }
        }
        catch (InterruptedException exception){}
        String temp = storage;
        storage = "";
        notifyAll();
        return temp;
    }
}
