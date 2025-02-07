package tw.lab1;

import java.util.ArrayList;
import java.util.List;

public class ProducersConsumers {
    public static void main(String[] args){
        var buf = new Buffer();

        var threads = new ArrayList<Thread>();

        for(int i = 0; i != 5; ++i) {
            threads.add(new Thread(new Producer(buf)));
            threads.add(new Thread(new Consumer(buf)));
        }

        for(var thread : threads){
            thread.start();
        }

        for(var thread : threads){
            try {
                thread.join();
            }
            catch (Exception ignored){}
        }
    }
}
