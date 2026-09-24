package by.kovalevskiy.FootballBaseGradle.controller;

import by.kovalevskiy.FootballBaseGradle.exception.UserAlreadyExistsException;
import by.kovalevskiy.FootballBaseGradle.model.Player;
import by.kovalevskiy.FootballBaseGradle.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping()
    public String showAllPlayers (Model model){
        model.addAttribute("players", playerService.showAllPlayers());
        return "player/allPlayers";
    }

    @GetMapping ("/{id}")
    public String showPlayer (@PathVariable("id") int id, Model model) {
        model.addAttribute("player", playerService.showPlayer(id));
        return "player/player";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping ("/new")
    public String newPlayer (Model model){
        model.addAttribute("player", new Player());
        return "player/newPlayer";
    }

    @PostMapping()
    public String createPlayer (@ModelAttribute("player") @Valid Player player, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "player/newPlayer";
        }
        try {
            playerService.createPlayer(player.getName(),player.getSurname(),player.getAge(),player.getCity());
        }catch (UserAlreadyExistsException e){
            bindingResult.reject("UserAlreadyExistsException",
                    "Пользователь с таким именем и фамилией уже существует");
            return "player/newPlayer";
        }

        return "redirect:/players";
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.player.id")
    @PostMapping ("/{id}/delete")
    public String deletePlayer (@PathVariable("id") int id){
        playerService.deletePlayer(id);
        return "redirect:/players";
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.player.id")
    @GetMapping ("/{id}/edit")
    public String editPlayer (Model model, @PathVariable("id") int id){
        model.addAttribute("player", playerService.showPlayer(id));
        return "player/editPlayer";
    }

    @PostMapping ("/{id}/edit")
    public String updatePlayer (@ModelAttribute("player") @Valid Player player, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "player/editPlayer";
        }
        try {playerService.updatePlayer(player);}
        catch (UserAlreadyExistsException e) {
            bindingResult.reject("UserAlreadyExistsException",
                    "Пользователь с таким именем и фамилией уже существует");
            return "player/editPlayer";
        }

        return "redirect:/players";
    }

    @GetMapping ("/games/{id}")
    public String PlayerGames (Model model, @PathVariable("id") int id){
        model.addAttribute("games", playerService.showPlayerGames(id));
        return "player/playerGames";
    }
}
