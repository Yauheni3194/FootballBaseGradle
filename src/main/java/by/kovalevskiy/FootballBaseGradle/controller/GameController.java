package by.kovalevskiy.FootballBaseGradle.controller;

import by.kovalevskiy.FootballBaseGradle.config.MyUserDetails;
import by.kovalevskiy.FootballBaseGradle.exception.CommentAccessException;
import by.kovalevskiy.FootballBaseGradle.exception.GameAlreadyExistsException;
import by.kovalevskiy.FootballBaseGradle.model.Game;
import by.kovalevskiy.FootballBaseGradle.model.Player;
import by.kovalevskiy.FootballBaseGradle.services.CommentService;
import by.kovalevskiy.FootballBaseGradle.services.GameService;
import by.kovalevskiy.FootballBaseGradle.services.PlaceService;
import by.kovalevskiy.FootballBaseGradle.services.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("games")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final CommentService commentService;
    private final PlaceService placeService;

    public GameController(GameService gameService, PlayerService playerService, CommentService commentService, PlaceService placeService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.commentService = commentService;
        this.placeService = placeService;
    }

    @GetMapping()
    public String showAllGames(Model model) {

        model.addAttribute("games", gameService.showAllGames());
        return "game/allGames";
    }

    @GetMapping("/{id}")
    public String showGame(@PathVariable("id") int id, Model model, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        Game game;
        try {game = gameService.showGame(id);}
        catch (EntityNotFoundException e){
            return "game/allGames";
        }
        model.addAttribute("game", game);
        List<Player> players = gameService.GamePlayers(id);
        model.addAttribute("players", players);
        List<Player> allFreePlayers = playerService.showAllPlayers();
        allFreePlayers.removeAll(players);
        model.addAttribute("allFreePlayers", allFreePlayers);
        model.addAttribute("comments", commentService.showAllGameComment(game));
        model.addAttribute("place", game.getPlace());
        if (myUserDetails != null) {
            model.addAttribute("currentUser", myUserDetails.getPlayer());
        }
        return "game/game";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newGame(Model model) {
        model.addAttribute("game", new Game());
        model.addAttribute("places", placeService.showAllPlaces());
        return "game/newGame";
    }
    @PostMapping()
    public String createGame (@ModelAttribute("game") @Valid Game game, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("places", placeService.showAllPlaces());
            return "game/newGame";
        }
        game.statusUpdate();
        try {gameService.saveGame(game);}
        catch (GameAlreadyExistsException e){
            bindingResult.reject("GameAlreadyExistsException",
                    "Такая игра уже существует");
            return "game/newGame";
        }

        return "redirect:/games";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editGame(Model model, @PathVariable("id") int id) {
        model.addAttribute("game", gameService.showGame(id));
        model.addAttribute("places", placeService.showAllPlaces());
        return "game/editGame";
    }

    @PostMapping("/{id}/edit")
    public String updateGame(@ModelAttribute("game") @Valid Game game, BindingResult bindingResult,RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("places", placeService.showAllPlaces());
            return "game/editGame";
        }
        game.statusUpdate();
        try {gameService.updateGame(game);}
        catch (GameAlreadyExistsException e){
            bindingResult.reject("GameAlreadyExistsException",
                    "Такая игра уже существует");
            model.addAttribute("places", placeService.showAllPlaces());
            return "game/editGame";
        }
        redirectAttributes.addFlashAttribute("updateGame", "Вы обновили игру!");
        return "redirect:/games/{id}";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteGame(@PathVariable("id") int id) {
        gameService.deleteGame(id);
        return "redirect:/games";
    }

    @PostMapping("/{id}/addPlayer")
    public String addPlayer(@PathVariable("id") int id, @AuthenticationPrincipal MyUserDetails myUserDetails, RedirectAttributes redirectAttributes) {

        try {
            gameService.addPlayer(myUserDetails.getPlayer().getId(), id);
            redirectAttributes.addFlashAttribute("success", "Вы успешно вошли в игру!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Вы уже являетесь участником этой игры!");
        }
        return "redirect:/games/{id}";
    }

    @PostMapping("/{id}/deletePlayer")
    public String deletePlayerFromGame(@PathVariable("id") int id, @AuthenticationPrincipal MyUserDetails myUserDetails, RedirectAttributes redirectAttributes) {
        gameService.deletePlayerFromGame(myUserDetails.getPlayer().getId(), id);
        redirectAttributes.addFlashAttribute("success", "Вы покинули игру!");
        return "redirect:/games/{id}";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/addPlayerAsAdmin")
    public String addPlayerAsAdmin(@PathVariable("id") int id, @RequestParam int playerId) {
        gameService.addPlayerAsAdmin(playerId, id);
        return "redirect:/games/{id}/changeGameAsAdmin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/deletePlayerAsAdmin")
    public String deletePlayerAsAdmin(@PathVariable("id") int id, @RequestParam int playerId) {
        gameService.deletePlayerFromGame(playerId, id);
        return "redirect:/games/{id}/changeGameAsAdmin";
    }

    @PostMapping("/{id}/addComment")
    public String addComment(@PathVariable("id") int id, @RequestParam("text") String text, @AuthenticationPrincipal MyUserDetails myUserDetails, RedirectAttributes redirectAttributes) {

        if (text == null || text.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("comment_error", "Комментарий не может быть пустым!");
            return "redirect:/games/{id}";
        }

        if (text.length() < 2 || text.length() > 500) {
            redirectAttributes.addFlashAttribute("comment_error", "Длина комментария должна быть от 2 до 500 символов!");
            return "redirect:/games/{id}";
        }

        commentService.addComment(id, myUserDetails.getPlayer().getId(), text);
        return "redirect:/games/{id}";
    }

    @PostMapping("/{id}/deleteComment/{commentId}")
    public String deleteComment(@PathVariable("id") int id, @PathVariable("commentId") int commentId, @AuthenticationPrincipal MyUserDetails myUserDetails, RedirectAttributes redirectAttributes) {
        int currentPlayerId = myUserDetails.getPlayer().getId();
        boolean isAdmin = myUserDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        try {
            commentService.deleteComment(commentId, currentPlayerId, isAdmin);
            redirectAttributes.addFlashAttribute("deleteCommentSuccess", "Комментарий успешно удален.");
        } catch (CommentAccessException e) {
            redirectAttributes.addFlashAttribute("deleteCommentError", "У вас нет прав на удаление этого комментария!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("deleteCommentError", "Произошла системная ошибка.");
        }
        return "redirect:/games/{id}";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/changeGameAsAdmin")
    public String changeGameAsAdmin(@PathVariable("id") int id, Model model) {
        model.addAttribute("game", gameService.showGame(id));
        List<Player> players = gameService.GamePlayers(id);
        model.addAttribute("players", players);
        List<Player> allFreePlayers = playerService.showAllPlayers();
        allFreePlayers.removeAll(players);
        model.addAttribute("allFreePlayers", allFreePlayers);
        return "game/changeGameAsAdmin";
    }
}
