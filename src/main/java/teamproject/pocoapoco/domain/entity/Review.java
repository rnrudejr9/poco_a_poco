//package teamproject.pocoapoco.domain.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import teamproject.pocoapoco.domain.entity.chat.ChatRoom;
//import teamproject.pocoapoco.enums.UserRole;
//
//import javax.persistence.*;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//@Entity
//@Getter
//@Table(name = "Reviews")
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class Review{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
////    @OneToOne
////    @JoinColumn(name = "crew_id")
////    private Crew crew;
////
////    @ManyToOne(fetch = FetchType.EAGER)
////    @JoinColumn(name = "fromUser_id")
////    private User fromUser;
////
////    @ManyToOne(fetch = FetchType.EAGER)
////    @JoinColumn(name = "toUser_id")
////    private User toUser;
//
//
//    private Double mannerScore;
//    private String review;
//
//
//}
