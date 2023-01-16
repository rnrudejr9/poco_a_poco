package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.UsesSunHttpServer;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.UserRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
    @Builder.Default
    private List<Crew> crews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Like> likes = new ArrayList<>();
}
