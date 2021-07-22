package okky.team.chawchaw.user.language;

import okky.team.chawchaw.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserHopeLanguageRepository extends JpaRepository<UserHopeLanguageEntity, Long> {

    @EntityGraph(attributePaths = {"hopeLanguage"}, type = EntityGraph.EntityGraphType.LOAD)
    List<UserHopeLanguageEntity> findByUser(UserEntity userEntity);
    void deleteByUser(UserEntity userEntity);
    void deleteByUserAndHopeLanguage(UserEntity userEntity, LanguageEntity languageEntity);

}
