package teamproject.pocoapoco.domain.entity;

import lombok.Getter;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.UserRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    private String address;
    private InterestSport sport;
    private Integer manner;
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private List<Crew> crews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Like> likes = new ArrayList<>();
}
