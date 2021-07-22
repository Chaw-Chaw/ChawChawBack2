package okky.team.chawchaw.user;

import okky.team.chawchaw.follow.FollowService;
import okky.team.chawchaw.user.country.UserCountryRepository;
import okky.team.chawchaw.user.dto.RequestUserVo;
import okky.team.chawchaw.user.language.UserHopeLanguageRepository;
import okky.team.chawchaw.user.language.UserLanguageRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowService followService;
    @Autowired
    private UserCountryRepository userCountryRepository;
    @Autowired
    private UserLanguageRepository userLanguageRepository;
    @Autowired
    private UserHopeLanguageRepository userHopeLanguageRepository;

    @Test
    public void 회원가입() throws Exception {
        //given
        RequestUserVo requestUserVo = RequestUserVo.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .content("내용")
                .facebookUrl("페이스북주소")
                .instagramUrl("인스타그램주소")
                .imageUrl("이미지 주소")
                .language(Sets.newHashSet(Arrays.asList(
                        "ko",
                        "kv",
                        "kg"
                )))
                .hopeLanguage(Sets.newHashSet(Arrays.asList(
                        "en",
                        "ee"
                )))
                .country(Sets.newHashSet(Arrays.asList(
                        "South Korea",
                        "United States"
                )))
                .build();
        //when
        userService.createUser(requestUserVo);
        List<UserEntity> users = userRepository.findAll();
        List<String> countrys = userCountryRepository.findAll().stream().map(x -> x.getCountry().getName()).collect(Collectors.toList());
        List<String> languages = userLanguageRepository.findAll().stream().map(x -> x.getLanguage().getAbbr()).collect(Collectors.toList());
        List<String> hopeLanguages = userHopeLanguageRepository.findAll().stream().map(x -> x.getHopeLanguage().getAbbr()).collect(Collectors.toList());
        //then
        // 유저
        Assertions.assertThat(users.size()).isEqualTo(1);
        Assertions.assertThat(users.get(0).getEmail())
                .isEqualTo("mangchhe@naver.com");
        // 나라
        Assertions.assertThat(countrys.size()).isEqualTo(2);
        Assertions.assertThat(countrys)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("South Korea", "United States"));
        // 언어
        Assertions.assertThat(languages.size()).isEqualTo(3);
        Assertions.assertThat(languages)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("ko", "kv", "kg"));
        // 희망 언어
        Assertions.assertThat(hopeLanguages.size()).isEqualTo(2);
        Assertions.assertThat(hopeLanguages)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("en", "ee"));
    }

    @Test
    public void 회원중복() throws Exception {
        //given
        RequestUserVo requestUserVo = RequestUserVo.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .content("내용")
                .facebookUrl("페이스북주소")
                .instagramUrl("인스타그램주소")
                .imageUrl("이미지 주소")
                .language(Sets.newHashSet(Arrays.asList(
                        "ko",
                        "kv",
                        "kg"
                )))
                .hopeLanguage(Sets.newHashSet(Arrays.asList(
                        "en",
                        "ee"
                )))
                .country(Sets.newHashSet(Arrays.asList(
                        "South Korea",
                        "United States"
                )))
                .build();
        userService.createUser(requestUserVo);
        //when
        Boolean result = userService.duplicateEmail("mangchhe@naver.com");
        Boolean result2 = userService.duplicateEmail("mangchhe2@naver.com");
        //then
        Assertions.assertThat(result).isTrue();
        Assertions.assertThat(result2).isFalse();
    }

    @Test
    public void 회원삭제() throws Exception {
        //given
        RequestUserVo requestUserVo = RequestUserVo.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .content("내용")
                .facebookUrl("페이스북주소")
                .instagramUrl("인스타그램주소")
                .imageUrl("이미지 주소")
                .language(Sets.newHashSet(Arrays.asList(
                        "ko",
                        "kv",
                        "kg"
                )))
                .hopeLanguage(Sets.newHashSet(Arrays.asList(
                        "en",
                        "ee"
                )))
                .country(Sets.newHashSet(Arrays.asList(
                        "South Korea",
                        "United States"
                )))
                .build();

        userService.createUser(requestUserVo);
        //when
        userService.deleteUser(requestUserVo.getEmail());
        List<UserEntity> users = userRepository.findAll();
        //then
        Assertions.assertThat(users.size()).isEqualTo(0);
    }

    @Test
    public void 프로필_수정() throws Exception {
        //given

        RequestUserVo createVo = RequestUserVo.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .content("내용")
                .facebookUrl("페이스북주소")
                .instagramUrl("인스타그램주소")
                .imageUrl("이미지주소")
                .language(Sets.newHashSet(Arrays.asList(
                        "fy",
                        "xh",
                        "yi",
                        "yo"
                )))
                .hopeLanguage(Sets.newHashSet(Arrays.asList(
                        "ab",
                        "aa",
                        "af",
                        "ak"
                )))
                .country(Sets.newHashSet(Arrays.asList(
                        "United States",
                        "South Korea",
                        "Zambia",
                        "Zimbabwe"
                )))
                .build();
        userService.createUser(createVo);
        UserEntity user = userRepository.findAll().get(0);
        RequestUserVo requestUserVo = RequestUserVo.builder()
                .id(user.getId())
                .content("내용2")
                .imageUrl("이미지주소2")
                .facebookUrl("페이스북주소2")
                .instagramUrl("인스타그램주소2")
                .country(Sets.newHashSet(Arrays.asList(
                        "United States",
                        "South Korea",
                        "Samoa",
                        "Kosovo"
                )))
                .language(Sets.newHashSet(Arrays.asList(
                        "fy",
                        "xh",
                        "wo",
                        "cy"
                )))
                .hopeLanguage(Sets.newHashSet(Arrays.asList(
                        "ab",
                        "aa",
                        "sq",
                        "am"
                )))
                .build();

        //when
        userService.updateProfile(requestUserVo);

        //then
        List<String> countrys = userCountryRepository.findAll().stream().map(x -> x.getCountry().getName()).collect(Collectors.toList());
        List<String> languages = userLanguageRepository.findAll().stream().map(x -> x.getLanguage().getAbbr()).collect(Collectors.toList());
        List<String> hopeLanguages = userHopeLanguageRepository.findAll().stream().map(x -> x.getHopeLanguage().getAbbr()).collect(Collectors.toList());

        Assertions.assertThat(countrys)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("South Korea", "United States", "Samoa", "Kosovo"));
        Assertions.assertThat(languages)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("fy", "xh", "wo", "cy"));
        Assertions.assertThat(hopeLanguages)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("ab", "aa", "sq", "am"));
        Assertions.assertThat(user.getFacebookUrl()).isEqualTo("페이스북주소2");
        Assertions.assertThat(user.getInstagramUrl()).isEqualTo("인스타그램주소2");
        Assertions.assertThat(user.getImageUrl()).isEqualTo("이미지주소2");
        Assertions.assertThat(user.getContent()).isEqualTo("내용2");
    }


//    @Test
//    public void 카드상세보기() throws Exception {
//        //given
//        UserDto user = UserDto.builder()
//                .email("mangchhe@naver.com")
//                .password("1234")
//                .name("이름")
//                .web_email("웹메일")
//                .school("학교")
//                .content("내용")
//                .country("나라")
//                .language("언어")
//                .hopeLanguage("희망언어")
//                .imageUrl("url")
//                .build();
//        UserDto user2 = UserDto.builder()
//                .email("mangchhe2@naver.com")
//                .password("1234")
//                .name("이름2")
//                .web_email("웹메일2")
//                .school("학교2")
//                .content("내용2")
//                .country("나라2")
//                .language("언어2")
//                .hopeLanguage("희망언어2")
//                .imageUrl("url2")
//                .build();
//        UserDto user3 = UserDto.builder()
//                .email("mangchhe3@naver.com")
//                .password("1234")
//                .name("이름3")
//                .web_email("웹메일3")
//                .school("학교3")
//                .content("내용3")
//                .country("나라3")
//                .language("언어3")
//                .hopeLanguage("희망언어3")
//                .imageUrl("url3")
//                .build();
//        UserDto userDto = userService.createUser(user);
//        UserEntity userFrom = userRepository.findById(userService.createUser(user2).getId()).get();
//        UserEntity userFrom2 = userRepository.findById(userService.createUser(user3).getId()).get();
//        followService.addFollow(userFrom, userDto.getId());
//        followService.addFollow(userFrom2, userDto.getId());
//        //when
//        UserDetailsDto result = userService.findUserDetails(userDto.getId());
//        //then
//        Assertions.assertThat(result.getName()).isEqualTo("이름");
//        Assertions.assertThat(result.getImageUrl()).isEqualTo("url");
//        Assertions.assertThat(result.getContent()).isEqualTo("내용");
//        Assertions.assertThat(result.getLanguage()).isEqualTo("언어");
//        Assertions.assertThat(result.getHopeLanguage()).isEqualTo("희망언어");
//        Assertions.assertThat(result.getViews()).isEqualTo(0);
//        Assertions.assertThat(result.getFollows()).isEqualTo(2);
//    }
//
//    @Test
//    public void 프로필보기() throws Exception {
//        //given
//        UserDto user = UserDto.builder()
//                .email("mangchhe@naver.com")
//                .password("1234")
//                .name("이름")
//                .web_email("웹메일")
//                .school("학교")
//                .content("내용")
//                .country("나라")
//                .language("언어")
//                .hopeLanguage("희망언어")
//                .imageUrl("url")
//                .build();
//        UserDto user2 = UserDto.builder()
//                .email("mangchhe2@naver.com")
//                .password("1234")
//                .name("이름2")
//                .web_email("웹메일2")
//                .school("학교2")
//                .content("내용2")
//                .country("나라2")
//                .language("언어2")
//                .hopeLanguage("희망언어2")
//                .imageUrl("url2")
//                .build();
//        UserDto userDto = userService.createUser(user);
//        UserDto userDto2 = userService.createUser(user2);
//        //when
//        UserDetailsDto result = userService.findUserProfile(userDto.getId());
//        UserDetailsDto result2 = userService.findUserProfile(userDto2.getId());
//        //then
//        Assertions.assertThat(result.getName()).isEqualTo("이름");
//        Assertions.assertThat(result.getImageUrl()).isEqualTo("url");
//        Assertions.assertThat(result.getContent()).isEqualTo("내용");
//        Assertions.assertThat(result.getLanguage()).isEqualTo("언어");
//        Assertions.assertThat(result.getHopeLanguage()).isEqualTo("희망언어");
//        Assertions.assertThat(result.getViews()).isEqualTo(0);
//        Assertions.assertThat(result.getFollows()).isEqualTo(null);
//
//        Assertions.assertThat(result2.getName()).isEqualTo("이름2");
//        Assertions.assertThat(result2.getImageUrl()).isEqualTo("url2");
//        Assertions.assertThat(result2.getContent()).isEqualTo("내용2");
//        Assertions.assertThat(result2.getLanguage()).isEqualTo("언어2");
//        Assertions.assertThat(result2.getHopeLanguage()).isEqualTo("희망언어2");
//        Assertions.assertThat(result2.getViews()).isEqualTo(0);
//        Assertions.assertThat(result2.getFollows()).isEqualTo(null);
//    }

}