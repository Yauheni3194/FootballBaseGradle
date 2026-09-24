package by.kovalevskiy.FootballBaseGradle.repositories;

import by.kovalevskiy.FootballBaseGradle.model.Comment;
import by.kovalevskiy.FootballBaseGradle.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findByGame(Game game);
}
