package teamproject.pocoapoco.domain.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.domain.entity.check.ChatConfigEntity;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.enums.UserRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String nickName;
    private String password;
    private String address;
    private Integer manner;
    private String email;
    private String imagePath;
    private UserRole role = UserRole.ROLE_USER;
    private LocalDateTime deletedAt;

    @Builder.Default
    private Double mannerScore = 36.5;

    @OneToOne(cascade = CascadeType.ALL)
    private Sport sport;
    private String provider;
    private String providerId;

    @OneToMany(mappedBy = "user")
    private List<Crew> crews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ChatConfigEntity> chatConfigEntities = new ArrayList<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<GrantedAuthority>(List.of(new SimpleGrantedAuthority(role.name())));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public static User toEntity(String userName, String nickName, String address, String password, SportEnum sport1, SportEnum sport2, SportEnum sport3, String email){
        return User.builder()
                .userName(userName)
                .nickName(nickName)
                .address(address)
                .role(UserRole.ROLE_USER)
                .sport(Sport.setSport(sport1, sport2, sport3))
                .password(password)
                .email(email)
                .build();
    }

    public static User toRevisedEntity(Long id,  String userName, String revisedNickName, String revisedAddress, String encodedPassword, SportEnum sport1, SportEnum sport2, SportEnum sport3, String email) {
        return User.builder()
                .id(id)
                .userName(userName)
                .nickName(revisedNickName)
                .address(revisedAddress)
                .role(UserRole.ROLE_USER)
                .sport(Sport.setSport(sport1, sport2, sport3))
                .email(email)
                .password(encodedPassword)
                .build();
    }

    public static User toEntityWithImage(Long id, String userName, String revisedNickName,  String revisedAddress, String encodedPassword, SportEnum sport1, SportEnum sport2, SportEnum sport3, String imagePath, String email) {
        return User.builder()
                .id(id)
                .userName(userName)
                .nickName(revisedNickName)
                .address(revisedAddress)
                .role(UserRole.ROLE_USER)
                .imagePath(imagePath)
                .sport(Sport.setSport(sport1, sport2, sport3))
                .email(email)
                .password(encodedPassword)
                .build();
    }
}
