package task2;

public class Task2 {
    public static void main(String[] args) throws InterruptedException {
        FizzBuzzMultithreading fbm = new FizzBuzzMultithreading(100);
        fbm.start();
    }
}