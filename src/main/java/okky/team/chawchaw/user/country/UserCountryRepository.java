package okky.team.chawchaw.user.country;

import okky.team.chawchaw.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserCountryRepository extends JpaRepository<UserCountryEntity, Long> {

    List<UserCountryEntity> findByUser(UserEntity userEntity);
    void deleteByUser(UserEntity userEntity);
    void deleteByUserAndCountry(UserEntity userEntity, CountryEntity countryEntity);
}
