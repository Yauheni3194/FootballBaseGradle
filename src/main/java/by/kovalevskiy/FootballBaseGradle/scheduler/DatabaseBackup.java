package by.kovalevskiy.FootballBaseGradle.scheduler;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseBackup {
    @PreDestroy
    public void onShutdownBackup() {
        System.out.println(">>> Приложение закрывается. Запуск создания SQL дампа...");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String backupFileName = "/app/backups/footballBase_" + timestamp + ".sql";

        File backupDir = new File("/app/backups");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        String[] command = {
                "sh", "-c",
                "docker exec -e PGPASSWORD=postgres postgresForFootballBase pg_dump -U postgres -d footballBase > " + backupFileName
        };

        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println(">>> SQL дамп успешно создан: " + backupFileName);
            } else {
                System.err.println(">>> Ошибка создания дампа. Код выхода: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(">>> Не удалось выполнить бэкап при закрытии: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
