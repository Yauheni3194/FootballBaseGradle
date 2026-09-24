package by.kovalevskiy.FootballBaseGradle.repositories;

import by.kovalevskiy.FootballBaseGradle.model.Player;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player,Integer> {
    @EntityGraph(attributePaths = {"games"})
    Optional<Player> findByNameAndSurname(String name, String surname);
    boolean existsByNameAndSurname(String name, String surname);
    List<Player> findAllByOrderBySurnameAscNameAsc();
}
