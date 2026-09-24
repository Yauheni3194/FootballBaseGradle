package by.kovalevskiy.FootballBaseGradle.services;

import by.kovalevskiy.FootballBaseGradle.exception.CommentAccessException;
import by.kovalevskiy.FootballBaseGradle.model.Comment;
import by.kovalevskiy.FootballBaseGradle.model.Game;
import by.kovalevskiy.FootballBaseGradle.model.Player;
import by.kovalevskiy.FootballBaseGradle.repositories.CommentRepository;
import by.kovalevskiy.FootballBaseGradle.repositories.GameRepository;
import by.kovalevskiy.FootballBaseGradle.repositories.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final JdbcTemplate jdbcTemplate;

    public CommentService(CommentRepository commentRepository, GameRepository gameRepository, PlayerRepository playerRepository, JdbcTemplate jdbcTemplate) {
        this.commentRepository = commentRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void addComment(int game_id, int player_id, String text) {
        Game game = gameRepository.findById(game_id)
                .orElseThrow(() -> new EntityNotFoundException("Игра с ID " + game_id + " не найдена"));
        Player player = playerRepository.findById(player_id)
                .orElseThrow(() -> new EntityNotFoundException("Игрок с ID " + player_id + " не найден"));

        Comment comment = new Comment();
        comment.setGame(game);
        comment.setPlayer(player);
        comment.setText(text);
        commentRepository.save(comment);
    }
    public List<Comment> showAllGameComment(Game game) {
        return commentRepository.findByGame(game);
    }

    @Transactional
    public void deleteComment(int commentId, int currentPlayerId, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Комментарий не найден"));
        if (comment.getPlayer().getId()==(currentPlayerId) || isAdmin) {
            commentRepository.delete(comment);
        } else {throw new CommentAccessException("У вас нет прав на удаление этого комментария!");}
    }
}
