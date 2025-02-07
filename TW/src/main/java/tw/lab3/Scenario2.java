package tw.lab3;

import java.util.ArrayList;

public class Scenario2 {
    public static final int N = 10;

    public static void main(String[] args) {
        var table = new Table();
        var waiter = new Waiter();

        var people = new ArrayList<Thread>();
        for(int i = 0; i != 2*N; ++i){
            final int j = i / 2;
            people.add(new Thread(() -> {
                for(;;){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                    waiter.reserveTable(j);
                    table.eat(j);
                    waiter.reuseTable();
                }
            }));
        }

        for(var person : people){
            person.start();
        }
        for(var person : people){
            try {
                person.join();
            } catch (InterruptedException ignored){}
        }
    }
}
