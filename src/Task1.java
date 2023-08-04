import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Task1 {
    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(
                () -> System.out.println("Минуло 5 секунд"),
                5,
                5,
                TimeUnit.SECONDS
        );

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        while (true) {
            LocalDateTime now = LocalDateTime.now();
            System.out.println(dtf.format(now));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}