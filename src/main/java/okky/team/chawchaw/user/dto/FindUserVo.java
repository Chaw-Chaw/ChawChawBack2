package okky.team.chawchaw.user.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindUserVo {

    String name;
    String country;
    String language;
    String hopeLanguage;
    String order;
    Integer pageNo;
    List<Integer> exclude;

}