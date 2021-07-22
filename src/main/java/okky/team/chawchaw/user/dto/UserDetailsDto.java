package okky.team.chawchaw.user.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {

    Long id;
    String name;
    String imageUrl;
    String content;
    List<String> country;
    List<String> language;
    List<String> hopeLanguage;
    String facebookUrl;
    String instagramUrl;
    LocalDateTime days;
    Long views;
    Long follows;

}
