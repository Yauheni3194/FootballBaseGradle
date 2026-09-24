package by.kovalevskiy.FootballBaseGradle.services;

import by.kovalevskiy.FootballBaseGradle.exception.UserAlreadyExistsException;
import by.kovalevskiy.FootballBaseGradle.model.Game;
import by.kovalevskiy.FootballBaseGradle.model.Player;
import by.kovalevskiy.FootballBaseGradle.model.Status;
import by.kovalevskiy.FootballBaseGradle.repositories.PlayerRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final JdbcTemplate jdbcTemplate;

    public PlayerService(PlayerRepository playerRepository, JdbcTemplate jdbcTemplate) {
        this.playerRepository = playerRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Player> showAllPlayers() {
        return playerRepository.findAllByOrderBySurnameAscNameAsc();
    }

    public Player showPlayer(int id) {
        Optional<Player> player = playerRepository.findById(id);
        return player.orElse(null);
    }

    @Transactional
    public void createPlayer(String name, String surname, int age, String city) {
        if (!playerRepository.existsByNameAndSurname(name,surname)){
            jdbcTemplate.update("insert into player (name, surname, age, city, created, updated,role,password,enabled) VALUES (?,?,?,?,?,?,?,?,?)", name,surname,age,city, LocalDate.now(),LocalDate.now(),"USER","$2a$12$ZthRbx/wfp2CxZWlxS0aZ.Alm5QUBBs77/ElBmLGXLIY8N23v2qEC",true);
        }else {throw new UserAlreadyExistsException ("Пользователь с таким именем и фамилией уже существует");
        }
    }

    @Transactional
    public void updatePlayer(Player player) {
        //TODO ПОМЕНЯТЬ ЛОГИКУ СРАВНЕНИЯ. ЧЕРЕЗ "AndIdNot"
        player.setUpdated(LocalDate.now());
        if (!playerRepository.existsByNameAndSurname(player.getName(), player.getSurname())){
            jdbcTemplate.update("update player set name=?, surname=?, age=?, city=?, updated=? where id=?",
                    player.getName(), player.getSurname(), player.getAge(), player.getCity(), player.getUpdated(), player.getId());
        }else {throw new UserAlreadyExistsException ("Пользователь с таким именем и фамилией уже существует");
        }
    }

    @Transactional
    public void deletePlayer(int id) {
        playerRepository.deleteById(id);
    }

    public List<Game> showPlayerGames(int id) {
        Optional<Player> player = playerRepository.findById(id);
        return new ArrayList<>(player.get().getGames()
                .stream().filter(game -> game.getStatus()==Status.Passed).sorted(Comparator.comparing(Game::getDate)).toList());
    }
}
