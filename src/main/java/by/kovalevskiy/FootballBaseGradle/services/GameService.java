package by.kovalevskiy.FootballBaseGradle.services;

import by.kovalevskiy.FootballBaseGradle.exception.GameAlreadyExistsException;
import by.kovalevskiy.FootballBaseGradle.model.Game;
import by.kovalevskiy.FootballBaseGradle.model.Player;
import by.kovalevskiy.FootballBaseGradle.repositories.GameRepository;
import by.kovalevskiy.FootballBaseGradle.repositories.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final JdbcTemplate jdbcTemplate;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, JdbcTemplate jdbcTemplate) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Game> showAllGames() {
        return gameRepository.findAllByOrderByDateAsc();
    }

    @Transactional
    public Game showGame(int id) {
        return gameRepository.findById(id)
                .map(gameForShow -> {
                    gameForShow.statusUpdate();
                    return gameRepository.save(gameForShow);
                })
                .orElseThrow(() -> new EntityNotFoundException("Игра с ID " + id + " не найдена!"));
    }


    @Transactional
    public void saveGame(Game game) {
        if (!gameRepository.existsByPlaceAndDateAndTime(game.getPlace(), game.getDate(), game.getTime())) {
            gameRepository.save(game);
        } else {
            throw new GameAlreadyExistsException("На это время и место уже запланирована другая игра");
        }
    }

    @Transactional
    public void updateGame(Game game) {
        boolean alreadyExists = gameRepository.existsByPlaceAndDateAndTimeAndIdNot(
                game.getPlace(),
                game.getDate(),
                game.getTime(),
                game.getId()
        );
        if (!alreadyExists) {
            gameRepository.save(game);
        } else {
            throw new GameAlreadyExistsException("На это время и место уже запланирована другая игра");
        }
    }

    @Transactional
    public void deleteGame(int id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        for (Player player : game.getPlayers()) {
            player.getGames().remove(game);
        }
        gameRepository.deleteById(id);
    }

    public List<Player> GamePlayers(int id) {
        Optional<Game> game = gameRepository.findById(id);
        List<Player> players = new ArrayList<>(game.get().getPlayers());
        players.sort(
                Comparator.comparing(Player::getSurname)
                        .thenComparing(Player::getName)
        );
        return players;
    }

    @Transactional
    public void addPlayer(int player_id, int game_id) {

        jdbcTemplate.update("insert into player_2_game (player_id, game_id) VALUES (?,?)", player_id, game_id);
    }

    @Transactional
    public void deletePlayerFromGame(int player_id, int game_id) {
        jdbcTemplate.update("DELETE FROM player_2_game WHERE player_id = ? AND game_id = ?", player_id, game_id);
    }

    @Transactional
    public void addPlayerAsAdmin(int player_id, int game_id) {
        Game realGame = gameRepository.findById(game_id)
                .orElseThrow(() -> new EntityNotFoundException("Игра с ID " + game_id + " не найдена"));
        Player realPlayer = playerRepository.findById(player_id)
                .orElseThrow(() -> new EntityNotFoundException("Игрок с ID " + player_id + " не найден"));
        if (realGame.getPlayers().contains(realPlayer)) {
            throw new IllegalStateException("Игрок " + realPlayer.getName() + " уже участвует в этой игре!");
        }
        realPlayer.getGames().add(realGame);
        realGame.getPlayers().add(realPlayer);
        gameRepository.save(realGame);
    }
}
