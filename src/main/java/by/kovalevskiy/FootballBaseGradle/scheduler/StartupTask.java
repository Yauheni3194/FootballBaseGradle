package by.kovalevskiy.FootballBaseGradle.scheduler;

import by.kovalevskiy.FootballBaseGradle.model.Game;
import by.kovalevskiy.FootballBaseGradle.model.Status;
import by.kovalevskiy.FootballBaseGradle.repositories.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class StartupTask {
    private final GameRepository gameRepository;

    public StartupTask(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        log.info("Проверяем статусы игр...");
        List<Game> games = gameRepository.findAllByStatus(Status.Expected);

        for (Game game : games) {
            try {
                game.statusUpdate();
            } catch (Exception e) {
                log.error("Ошибка при обновлении игры с id: {}", game.getId(), e);
                game.setStatus(Status.FailedToUpdate);
            } finally {
                gameRepository.save(game);
            }
        }
        log.info("Статусы игр обновлены...");
    }
}
