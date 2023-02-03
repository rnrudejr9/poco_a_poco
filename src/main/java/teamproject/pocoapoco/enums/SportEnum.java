package teamproject.pocoapoco.enums;

// enum 사용 X

import lombok.Getter;

@Getter
public enum SportEnum {

    SOCCER("축구"),
    FOOTVOLLEYBALL("족구"),
    BASKETBALL("농구"),
    BASEBALL("야구"),
    PINGPONG("탁구"),
    BOWLING("볼링"),
    BADMINTON("배드민턴"),
    TENNIS("테니스"),
    GOLF("골프"),
    HEALTH("헬스"),
    JOGGING("조깅"),
    WALK("산책"),
    HIKING("등산"),
    BICYCLE("자전거"),
    SWIMMING("수영"),
    SNORKELING("스노클링"),
    SCUBADIVING("스쿠버다이빙"),
    WATERSKIING("수상스키"),
    SKIING("스키"),
    SNOWBOARD("스노우보드"),
    SKATEBOARD("스케이트보드"),
    YOGA("요가"),
    PILATES("필라테스"),
    PARAGLIDING("패러글라이딩"),
    GO("바둑"),
    JANGGI("장기"),
    CHESS("체스"),
    ;

    private String value;

    SportEnum(String value){
        this.value = value;
    }
}
