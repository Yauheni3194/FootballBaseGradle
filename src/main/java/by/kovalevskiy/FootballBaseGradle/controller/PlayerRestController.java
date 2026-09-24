package by.kovalevskiy.FootballBaseGradle.controller;

import by.kovalevskiy.FootballBaseGradle.DTO.GameDTO;
import by.kovalevskiy.FootballBaseGradle.DTO.PlayerDTO;
import by.kovalevskiy.FootballBaseGradle.exception.PlayerNotFoundException;
import by.kovalevskiy.FootballBaseGradle.model.Game;
import by.kovalevskiy.FootballBaseGradle.model.Player;
import by.kovalevskiy.FootballBaseGradle.repositories.GameRepository;
import by.kovalevskiy.FootballBaseGradle.repositories.PlayerRepository;
import by.kovalevskiy.FootballBaseGradle.services.GameService;
import by.kovalevskiy.FootballBaseGradle.services.PlayerService;
import by.kovalevskiy.FootballBaseGradle.util.PlayerErrorResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class PlayerRestController {
    private final PlayerService playerService;
    private final GameService gameService;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;

    public PlayerRestController(PlayerService playerService, GameService gameService, PlayerRepository playerRepository, GameRepository gameRepository, ModelMapper modelMapper) {
        this.playerService = playerService;
        this.gameService = gameService;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PlayerDTO> allPlayers(){
        return playerService.showAllPlayers().stream().map(this::convertToPlayerDTO).collect(Collectors.toList());
    }

    @GetMapping("/player/{id}")
    public PlayerDTO showPlayer(@PathVariable("id") int id){
        return convertToPlayerDTO(playerService.showPlayer(id));
    }

    private PlayerDTO convertToPlayerDTO(Player player){
        return modelMapper.map(player,PlayerDTO.class);
    }

    @PostMapping("/player/amountOfGames")
    public ResponseEntity<String> amountOfGames(@RequestBody @Valid PlayerDTO playerDTO, BindingResult bindingResult){
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .filter(error -> "name".equals(error.getField()) || "surname".equals(error.getField()))
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        if (!errors.isEmpty()) {
            return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
        }
        Player player = playerRepository.findByNameAndSurname(playerDTO.getName(),playerDTO.getSurname()).orElseThrow(PlayerNotFoundException::new);
        int id = player.getId();
        String COUNT = String.valueOf(gameRepository.countGamesByPlayerId((long) id));
        return ResponseEntity.ok("Игрок сыграл "+COUNT+" игр(ы)");
    }
    @ExceptionHandler
    private ResponseEntity<PlayerErrorResponse> handleException (PlayerNotFoundException exception){
        PlayerErrorResponse playerErrorResponse = new PlayerErrorResponse(
                "Игрока с таким именем и фамилией нет",System.currentTimeMillis());
        return new ResponseEntity(playerErrorResponse, HttpStatus.NOT_FOUND);
    }

    private GameDTO convertToGameDTO(Game game){
        return modelMapper.map(game,GameDTO.class);
    }

    @PostMapping("/player/playerGames")
    public ResponseEntity<String> playerGames(@RequestBody @Valid PlayerDTO playerDTO, BindingResult bindingResult){
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .filter(error -> "name".equals(error.getField()) || "surname".equals(error.getField()))
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        if (!errors.isEmpty()) {
            return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
        }
        Player player = playerRepository.findByNameAndSurname(playerDTO.getName(),playerDTO.getSurname()).orElseThrow(PlayerNotFoundException::new);
        List<GameDTO> gameDTOS = player.getGames().stream().map(this::convertToGameDTO).collect(Collectors.toList());
        return new ResponseEntity(gameDTOS, HttpStatus.OK);
    }
}


