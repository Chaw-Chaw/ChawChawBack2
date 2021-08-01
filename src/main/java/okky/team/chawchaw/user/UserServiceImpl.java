package okky.team.chawchaw.user;

import lombok.RequiredArgsConstructor;
import okky.team.chawchaw.follow.FollowRepository;
import okky.team.chawchaw.user.country.CountryRepository;
import okky.team.chawchaw.user.country.UserCountryEntity;
import okky.team.chawchaw.user.country.UserCountryRepository;
import okky.team.chawchaw.user.dto.*;
import okky.team.chawchaw.user.language.*;
import okky.team.chawchaw.user.view.ViewEntity;
import okky.team.chawchaw.user.view.ViewRepository;
import okky.team.chawchaw.utils.DtoToEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final CountryRepository countryRepository;
    private final UserCountryRepository userCountryRepository;
    private final LanguageRepository languageRepository;
    private final UserLanguageRepository userLanguageRepository;
    private final UserHopeLanguageRepository userHopeLanguageRepository;
    private final ViewRepository viewRepository;
    private final Environment env;
    @Value("${user.profile.image.path}")
    private String uploadPath;
    @Value("${user.profile.image.default}")
    private String defaultImage;

    @Override
    @Transactional(readOnly = false)
    public Boolean createUser(RequestUserVo requestUserVo) {
        requestUserVo.setPassword(passwordEncoder.encode(requestUserVo.getPassword()));
        UserEntity user = DtoToEntity.CreateUserVoToEntity(requestUserVo);
        if (!StringUtils.hasText(user.getImageUrl())) {
                user.changeImageUrl(defaultImage);
        }
        userRepository.save(user);

        if (requestUserVo.getCountry() != null && requestUserVo.getLanguage() != null && requestUserVo.getHopeLanguage() != null) {
            for (String country : requestUserVo.getCountry()) {
                UserCountryEntity userCountryEntity = userCountryRepository.save(
                        new UserCountryEntity(
                                user, countryRepository.findByName(country)
                        )
                );
                userCountryEntity.changeUser(user);
            }
            for (String language : requestUserVo.getLanguage()) {
                UserLanguageEntity userLanguageEntity = userLanguageRepository.save(
                        new UserLanguageEntity(
                                user, languageRepository.findByAbbr(language)
                        )
                );
                userLanguageEntity.changeUser(user);
            }
            for (String language : requestUserVo.getHopeLanguage()) {
                UserHopeLanguageEntity userHopeLanguageEntity = userHopeLanguageRepository.save(
                        new UserHopeLanguageEntity(
                                user, languageRepository.findByAbbr(language)
                        )
                );
                userHopeLanguageEntity.changeUser(user);
            }
            return true;
        }
        return false;
    }

    @Override
    public Boolean duplicateEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteUser(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow();
        followRepository.deleteByUserFromOrUserTo(user, user);
        if (user != null)
            userRepository.delete(user);

    }

    @Override
    public List<UserCardDto> findUserCards(FindUserVo findUserVo) {
        return userRepository.findAllByElement(findUserVo);
    }

    @Override
    @Transactional(readOnly = false)
    public UserDetailsDto findUserDetails(Long userId, Long userId2) {

        UserEntity user = userRepository.findById(userId).orElseThrow();
        UserEntity user2 = userRepository.findById(userId2).orElseThrow();

        ViewEntity view = viewRepository.findByUserFromIdAndUserToId(userId2, userId);

        if (view == null && !userId.equals(userId2)) {
            user.plusViews();
            viewRepository.save(new ViewEntity(user2, user));
        }

        List<UserCountryEntity> countrys = userCountryRepository.findByUser(user);
        List<UserLanguageEntity> languages = userLanguageRepository.findByUser(user);
        List<UserHopeLanguageEntity> hopeLanguages = userHopeLanguageRepository.findByUser(user);
        Long follows = followRepository.countByUserToId(user.getId());

        UserDetailsDto result = UserDetailsDto.builder()
                .id(user.getId())
                .name(user.getName())
                .imageUrl(user.getImageUrl())
                .content(user.getContent())
                .facebookUrl(user.getFacebookUrl())
                .instagramUrl(user.getInstagramUrl())
                .days(user.getRegDate())
                .views(user.getViews())
                .follows(follows)
                .country(countrys.stream().map(x -> x.getCountry().getName()).collect(Collectors.toList()))
                .language(languages.stream().map(x -> x.getLanguage().getAbbr()).collect(Collectors.toList()))
                .hopeLanguage(hopeLanguages.stream().map(x -> x.getHopeLanguage().getAbbr()).collect(Collectors.toList()))
                .build();

        return result;
    }

    @Override
    public UserProfileDto findUserProfile(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow();

        List<UserCountryEntity> countrys = userCountryRepository.findByUser(user);
        List<UserLanguageEntity> languages = userLanguageRepository.findByUser(user);
        List<UserHopeLanguageEntity> hopeLanguages = userHopeLanguageRepository.findByUser(user);

        UserProfileDto result = UserProfileDto.builder()
                .imageUrl(user.getImageUrl())
                .content(user.getContent())
                .facebookUrl(user.getFacebookUrl())
                .instagramUrl(user.getInstagramUrl())
                .country(countrys.stream().map(x -> x.getCountry().getName()).collect(Collectors.toList()))
                .language(languages.stream().map(x -> x.getLanguage().getAbbr()).collect(Collectors.toList()))
                .hopeLanguage(hopeLanguages.stream().map(x -> x.getHopeLanguage().getAbbr()).collect(Collectors.toList()))
                .build();

        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Boolean updateProfile(RequestUserVo requestUserVo) {

        UserEntity user = userRepository.findById(requestUserVo.getId()).orElseThrow();

        user.changeContent(requestUserVo.getContent());
        user.changeFacebookUrl(requestUserVo.getFacebookUrl());
        user.changeInstagramUrl(requestUserVo.getInstagramUrl());
        user.changeImageUrl(requestUserVo.getImageUrl());
        user.changeRepCountry(requestUserVo.getRepCountry());
        user.changeRepLanguage(requestUserVo.getRepLanguage());
        user.changeRepHopeLanguage(requestUserVo.getRepHopeLanguage());

        List<UserCountryEntity> userCountrys = userCountryRepository.findByUser(user);
        List<UserLanguageEntity> userLanguages = userLanguageRepository.findByUser(user);
        List<UserHopeLanguageEntity> userHopeLanguages = userHopeLanguageRepository.findByUser(user);

        Set<String> addCountrys = requestUserVo.getCountry();
        Set<String> removeCountrys = userCountrys.stream().map(x -> x.getCountry().getName()).collect(Collectors.toSet());

        Set<String> addLanguages = requestUserVo.getLanguage();
        Set<String> removeLanguages = userLanguages.stream().map(x -> x.getLanguage().getAbbr()).collect(Collectors.toSet());

        Set<String> addHopeLanguages = requestUserVo.getHopeLanguage();
        Set<String> removeHopeLanguages = userHopeLanguages.stream().map(x -> x.getHopeLanguage().getAbbr()).collect(Collectors.toSet());

        removeCountrys.removeAll(addCountrys);
        addCountrys.removeAll(userCountrys.stream().map(x -> x.getCountry().getName()).collect(Collectors.toSet()));

        removeLanguages.removeAll(addLanguages);
        addLanguages.removeAll(userLanguages.stream().map(x -> x.getLanguage().getAbbr()).collect(Collectors.toSet()));

        removeHopeLanguages.removeAll(addHopeLanguages);
        addHopeLanguages.removeAll(userHopeLanguages.stream().map(x -> x.getHopeLanguage().getAbbr()).collect(Collectors.toSet()));

        for (String removeCountry : removeCountrys) {
            userCountryRepository.deleteByUserAndCountry(user, countryRepository.findByName(removeCountry));
        }
        for (String addCountry : addCountrys) {
            userCountryRepository.save(new UserCountryEntity(user, countryRepository.findByName(addCountry)));
        }

        for (String removeLanguage : removeLanguages) {
            userLanguageRepository.deleteByUserAndLanguage(user, languageRepository.findByAbbr(removeLanguage));
        }
        for (String addLanguage : addLanguages) {
            userLanguageRepository.save(new UserLanguageEntity(user, languageRepository.findByAbbr(addLanguage)));
        }

        for (String removeHopeLanguage : removeHopeLanguages) {
            userHopeLanguageRepository.deleteByUserAndHopeLanguage(user, languageRepository.findByAbbr(removeHopeLanguage));
        }
        for (String addHopeLanguage : addHopeLanguages) {
            userHopeLanguageRepository.save(new UserHopeLanguageEntity(user, languageRepository.findByAbbr(addHopeLanguage)));
        }

        return true;
    }

    @Override
    @Transactional(readOnly = false)
    public String uploadImage(MultipartFile file, Long userId) {
        try {
            UserEntity user = userRepository.findById(userId).orElseThrow();
            String uuid = UUID.randomUUID().toString();
            String fileName = file.getOriginalFilename();

            /* 폴더 생성 */
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String folderPath = date.replace("//", File.separator);
            File uploadPathFolder = new File(uploadPath, folderPath);
            if (!uploadPathFolder.exists()) {
                uploadPathFolder.mkdirs();
            }

            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;
            Path savePath = Paths.get(saveName);
            file.transferTo(savePath);
            String encodeUrl = URLEncoder.encode(folderPath + File.separator + uuid + "_" + fileName, "UTF-8");
            if (!URLDecoder.decode(user.getImageUrl(), "UTF-8").equals(defaultImage)) {
                new File(uploadPath + URLDecoder.decode(user.getImageUrl(), "UTF-8")).delete();
            }
            user.changeImageUrl(encodeUrl);
            return encodeUrl;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    @Transactional(readOnly = false)
    public String deleteImage(String imageUrl, Long userId) {
        try {
            UserEntity user = userRepository.findById(userId).orElseThrow();
            if (!imageUrl.equals(defaultImage)) {
                user.changeImageUrl(defaultImage);
                new File(uploadPath + URLDecoder.decode(imageUrl, "UTF-8")).delete();
                return defaultImage;
            } else{
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Boolean isUser(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
