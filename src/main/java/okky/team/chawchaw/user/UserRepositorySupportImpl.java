package okky.team.chawchaw.user;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import okky.team.chawchaw.follow.QFollowEntity;
import okky.team.chawchaw.user.country.QUserCountryEntity;
import okky.team.chawchaw.user.dto.FindUserVo;
import okky.team.chawchaw.user.dto.RequestUserVo;
import okky.team.chawchaw.user.dto.UserCardDto;
import okky.team.chawchaw.user.language.QLanguageEntity;
import okky.team.chawchaw.user.language.QUserHopeLanguageEntity;
import okky.team.chawchaw.user.language.QUserLanguageEntity;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositorySupportImpl implements UserRepositorySupport{

    private final JPAQueryFactory jpaQueryFactory;

    QUserEntity user = QUserEntity.userEntity;
    QUserLanguageEntity userLanguage = QUserLanguageEntity.userLanguageEntity;
    QUserHopeLanguageEntity userHopeLanguage = QUserHopeLanguageEntity.userHopeLanguageEntity;
    QLanguageEntity language = QLanguageEntity.languageEntity;
    QFollowEntity follow = QFollowEntity.followEntity;

    @Override
    public List<UserCardDto> findAllByElement(FindUserVo findUserVo) {

        int limit = 0;
        int offset = 0;

        if (findUserVo.getPageNo() == 1) {
            limit = 6;
        } else {
            limit = 3;
            offset = 6 + 3 * (findUserVo.getPageNo() - 2);
        }

        return jpaQueryFactory
                .select(Projections.constructor(
                        UserCardDto.class,
                        user.id,
                        user.imageUrl,
                        user.content,
                        user.repCountry,
                        user.repLanguage,
                        user.repHopeLanguage,
                        user.regDate,
                        user.views,
                        user.count()
                ))
                .from(user)
                .leftJoin(user.followTo, follow)
                .where(
                        /* 구사할 수 있는 언어 */
                        StringUtils.hasText(findUserVo.getLanguage()) ?
                                user.in(
                                        JPAExpressions
                                                .select(userLanguage.user)
                                                .from(userLanguage)
                                                .join(userLanguage.user, user)
                                                .join(userLanguage.language, language)
                                                .where(userLanguage.language.abbr.eq(findUserVo.getLanguage()))
                                ) : null,
                        /* 배우길 희망하는 언어 */
                        StringUtils.hasText(findUserVo.getHopeLanguage()) ?
                                user.in(
                                        JPAExpressions
                                                .select(userHopeLanguage.user)
                                                .from(userHopeLanguage)
                                                .join(userHopeLanguage.user, user)
                                                .join(userHopeLanguage.hopeLanguage, language)
                                                .where(userHopeLanguage.hopeLanguage.abbr.eq(findUserVo.getHopeLanguage()))
                                ) : null,
                        /* 이름 */
                        StringUtils.hasText(findUserVo.getName()) ? user.name.eq(findUserVo.getName()) : null,
                        /* 제외 목록 */
                        findUserVo.getExclude() != null && !findUserVo.getExclude().isEmpty() ?
                                user.id.notIn(findUserVo.getExclude().toArray(Integer[]::new)) : null
                )
                .groupBy(user)
                .orderBy(getSortedColumn(findUserVo.getOrder()))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    private OrderSpecifier<?> getSortedColumn(String order) {
        if (StringUtils.hasText(order)) {
            if (order.equals("like")) {
                return user.count().desc();
            }
            else if (order.equals("view")) {
                return user.views.desc();
            }
        }
        return NumberExpression.random().asc();
    }

}