package teamproject.pocoapoco.domain.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.UserRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Table(name = "Users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String userName;
    private String password;
    private String address;
    private Integer manner;
    private String email;
    private UserRole role = UserRole.ROLE_USER;

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

    public static User toEntity(String userId, String userName, String address, String password, Boolean likeSoccer, Boolean likeJogging, Boolean likeTennis){
        return User.builder()
                .userId(userId)
                .userName(userName)
                .address(address)
                .role(UserRole.ROLE_USER)
                .sport(Sport.setSport(likeSoccer, likeJogging, likeTennis))
                .password(password)
                .build();
    }

    public static User toRevisedEntity(Long id, String userId, String revisedUserName, String revisedAddress, String encodedPassword, Boolean revisedLikeSoccer, Boolean revisedLikeJogging, Boolean revisedLikeTennis) {
        return User.builder()
                .id(id)
                .userId(userId)
                .userName(revisedUserName)
                .address(revisedAddress)
                .role(UserRole.ROLE_USER)
                .sport(Sport.setSport(revisedLikeSoccer, revisedLikeJogging, revisedLikeTennis))
                .password(encodedPassword)
                .build();
    }

}
