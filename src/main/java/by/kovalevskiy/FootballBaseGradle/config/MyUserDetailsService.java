package by.kovalevskiy.FootballBaseGradle.config;

import by.kovalevskiy.FootballBaseGradle.model.Player;
import by.kovalevskiy.FootballBaseGradle.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class MyUserDetailsService implements UserDetailsService {


    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        username = username.trim();
        String[] parts = username.split("\\s+");
        if (parts.length < 2) {
            throw new UsernameNotFoundException("Введите имя и фамилию через пробел");
        }
        String name = parts[0];
        String surname = parts[1];
        Player user = playerRepository.findByNameAndSurname(name,surname)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с именем " + name + " " + surname + " не найден"
                ));
        return new MyUserDetails(user);
    }
}
