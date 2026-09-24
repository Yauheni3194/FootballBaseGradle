package by.kovalevskiy.FootballBaseGradle.config;

import by.kovalevskiy.FootballBaseGradle.model.Player;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {

    private Player player;
    public MyUserDetails(Player player){
        this.player = player;
    }
    public Player getPlayer() {
        return player;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(player.getRole().split(", "))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                .collect(Collectors.toList());
    }
    @Override
    public String getPassword() { return player.getPassword(); }

    @Override
    public String getUsername() { return player.getName(); }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() {return true; }
    @Override
    public boolean isEnabled() { return player.getEnabled(); }
}
