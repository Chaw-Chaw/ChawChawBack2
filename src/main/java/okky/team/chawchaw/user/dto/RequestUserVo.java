package okky.team.chawchaw.user.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserVo {

    Long id;
    String email;
    String password;
    String name;
    String country;
    String language;
    String hopeLanguage;
    String content;
    String school;
    String web_email;
    String order;
    String facebookUrl;
    String instagramUrl;
    String imageUrl;

}
