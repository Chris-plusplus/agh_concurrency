package tw.lab1;

public class Sync {
    public static void main(String[] args){
        var counter = new CounterSync();
        var incrThread = new Thread(() -> {
            for(long i = 0; i != 100000000 ; ++i){
                counter.increment();
            }
            System.out.println("incrThread ended");
        });
        var decrThread = new Thread(() -> {
            for(long i = 0; i != 100000000 ; ++i){
                counter.decrement();
            }
            System.out.println("decrThread ended");
        });

        incrThread.start();
        decrThread.start();

        try{
            incrThread.join();
        }
        catch (Exception ignored){}
        try{
            decrThread.join();
        }
        catch (Exception ignored){}

        // zawsze wynik == 0
        System.out.println(counter.getValue());
    }
}
