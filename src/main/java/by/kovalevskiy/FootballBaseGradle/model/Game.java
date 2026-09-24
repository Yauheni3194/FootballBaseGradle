package by.kovalevskiy.FootballBaseGradle.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "game")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Game {
    @ManyToMany(mappedBy = "games")
    private Set<Player> players = new HashSet<Player>();

    @OneToMany(mappedBy = "game")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "place_id", referencedColumnName = "id")
    @NotNull(message = "choose place")
    private Place place;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "date")
    @NotNull(message = "choose date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @Column(name = "time")
    @NotNull(message = "choose time")
    private LocalTime time;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @CreatedDate
    @Column(name = "created", updatable = false, nullable = false)
    private LocalDate created;
    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    private LocalDate updated;

    public void statusUpdate() {
        if (this.getDate().compareTo(LocalDate.now()) > 0) {
            this.setStatus(Status.Expected);
        } else if (this.getDate().compareTo(LocalDate.now()) < 0) {
            this.setStatus(Status.Passed);
        } else {
            if (this.getTime().compareTo(LocalTime.now()) == 1) {
                this.setStatus(Status.Expected);
            } else {
                this.setStatus(Status.Passed);
            }
        }
    }
}
