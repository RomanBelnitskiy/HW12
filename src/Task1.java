import java.time.Duration;
import java.time.Instant;
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
        Instant startedAt = Instant.now();
        while (true) {
            Instant current = Instant.now();
            Duration duration = Duration.between(startedAt, current);
            printDuration(duration);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void printDuration(Duration duration) {
        System.out.printf("Програма працює %02d:%02d:%02d\n",
                duration.toHours(), duration.toMinutes() % 60, duration.toSeconds() % 60);
    }
}