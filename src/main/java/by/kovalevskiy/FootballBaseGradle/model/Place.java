package by.kovalevskiy.FootballBaseGradle.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Place {

    @OneToMany(mappedBy = "place")
    private List<Game> games;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(message = "name should not be empty")
    @Size(min = 2, max = 20, message = "allowed from 2 to 20 characters")
    @Column(name = "name")
    private String name;
    @NotEmpty(message = "address should not be empty")
    @Size(min = 2, max = 100, message = "allowed from 2 to 100 characters")
    @Column(name = "address")
    private String address;
    @Size(max = 30, message = "enter valid number")
    @Column(name = "administrator_phone_number")
    private String administrator_phone_number;
    @CreatedDate
    @Column(name = "created", updatable = false, nullable = false)
    private LocalDate created;
    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    private LocalDate updated;
}
