package teamproject.pocoapoco.enums;

import lombok.Getter;

@Getter
public enum StrictEnum {
    SEOUL("서울"),
    KYEONGGI("경기"),
    KANGWON("강원"),
    CHUNGBUK("충북"),
    CHUNGNAM("충남"),

    JEJU("제주")
    ;

    private String value;


    StrictEnum(String value) {
        this.value = value;
    }
}
