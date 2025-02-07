package tw.lab3;

public class Printer {
    private final int num;

    public Printer(int num){
        this.num = num;
    }

    public void print(String message){
        System.out.println("Drukarka %d: '%s'".formatted(num, message));
    }
}
