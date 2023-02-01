package teamproject.pocoapoco.domain.dto.crew;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class FormDto {
    private String name;
    private boolean tnf;
    private List<String> hobbies;
}