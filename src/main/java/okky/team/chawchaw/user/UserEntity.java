package okky.team.chawchaw.user;

import lombok.*;
import okky.team.chawchaw.utils.RoleAttributeConverter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String web_email;
    @Column(nullable = false)
    private String school;
    @Column(length = 1000)
    private String imageUrl;
    @Column(length = 2000)
    private String content;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String language;
    @Column(nullable = false)
    private String hopeLanguage;
    private String socialUrl;
    @Column(nullable = false, insertable = false, updatable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private LocalDateTime regDate;
    @Column(nullable = false)
    private Long views = 0L;
    @Column(nullable = false)
    @Convert(converter = RoleAttributeConverter.class)
    private Role role;

    @Builder
    public UserEntity(String email, String password, String name, String web_email, String school, String imageUrl, String content, String country, String language, String hopeLanguage, String socialUrl, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.web_email = web_email;
        this.school = school;
        this.imageUrl = imageUrl;
        this.content = content;
        this.country = country;
        this.language = language;
        this.hopeLanguage = hopeLanguage;
        this.socialUrl = socialUrl;
        this.role = role == null ? Role.USER : role;
    }
}
