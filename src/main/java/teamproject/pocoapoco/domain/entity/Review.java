package teamproject.pocoapoco.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "Reviews")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fromUser_id")
    private User fromUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toUser_id")
    private User toUser;

//    private String reviews;
    private Double reviewScore;
    private String reviewContext;

    public void of(Crew crew, User fromUser, User toUser, /*String reviews,*/ Double reviewScore, String review) {
        this.crew = crew;
        this.fromUser = fromUser;
        this.toUser = toUser;
//        this.reviews = reviews;
        this.reviewScore= reviewScore;
        this.reviewContext = review;
    }
}
