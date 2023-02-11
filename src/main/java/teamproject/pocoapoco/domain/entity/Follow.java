package teamproject.pocoapoco.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean status;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "followingUserId")
    private User followingUser;  //팔로잉 한 유저(로그인 한 유저)

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "followedUserId")
    private User followedUser; //팔로잉 당한 유저

    public Follow(User followingUser, User followedUser) {
        this.followingUser =followingUser;
        this.followedUser=followedUser;
    }
}

