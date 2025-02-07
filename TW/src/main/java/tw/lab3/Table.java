package tw.lab3;

public class Table {
    public Table(){}

    public void eat(int pairNum){
        System.out.println("Je para %d".formatted(pairNum));
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {}
    }
}
