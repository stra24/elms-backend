package com.everrefine.elms.config;

import com.everrefine.elms.domain.model.Order;
import com.everrefine.elms.domain.model.Url;
import com.everrefine.elms.domain.model.course.Description;
import com.everrefine.elms.domain.model.news.Content;
import com.everrefine.elms.domain.model.user.EmailAddress;
import com.everrefine.elms.domain.model.user.Password;
import com.everrefine.elms.domain.model.user.RealName;
import com.everrefine.elms.domain.model.user.UserName;
import com.everrefine.elms.domain.model.user.UserRole;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

/** JDBC設定に関するクラス。 */
@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

  @Bean
  @Override
  public JdbcCustomConversions jdbcCustomConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();

    converters.add(new EmailAddressToStringConverter());
    converters.add(new StringToEmailAddressConverter());

    converters.add(new PasswordToStringConverter());
    converters.add(new StringToPasswordConverter());

    converters.add(new RealNameToStringConverter());
    converters.add(new StringToRealNameConverter());

    converters.add(new UserNameToStringConverter());
    converters.add(new StringToUserNameConverter());

    converters.add(new UrlToStringConverter());
    converters.add(new StringToUrlConverter());

    converters.add(new UserRoleToStringConverter());
    converters.add(new StringToUserRoleConverter());

    converters.add(new CourseTitleToStringConverter());
    converters.add(new StringToCourseTitleConverter());

    converters.add(new CourseDescriptionToStringConverter());
    converters.add(new StringToCourseDescriptionConverter());

    converters.add(new NewsTitleToStringConverter());
    converters.add(new StringToNewsTitleConverter());

    converters.add(new NewsContentToStringConverter());
    converters.add(new StringToNewsContentConverter());

    converters.add(new OrderToBigDecimalConverter());
    converters.add(new BigDecimalToOrderConverter());

    converters.add(new LessonTitleToStringConverter());
    converters.add(new StringToLessonTitleConverter());

    converters.add(new LessonContentToStringConverter());
    converters.add(new StringToLessonContentConverter());

    return new JdbcCustomConversions(converters);
  }

  /** EmailAddressをStringに変換するコンバーター。 */
  @WritingConverter
  static class EmailAddressToStringConverter implements Converter<EmailAddress, String> {
    @Override
    public String convert(EmailAddress source) {
      return source.getValue();
    }
  }

  /** StringをEmailAddressに変換するコンバーター。 */
  @ReadingConverter
  static class StringToEmailAddressConverter implements Converter<String, EmailAddress> {
    @Override
    public EmailAddress convert(String source) {
      return new EmailAddress(source);
    }
  }

  /** PasswordをStringに変換するコンバーター。 */
  @WritingConverter
  static class PasswordToStringConverter implements Converter<Password, String> {
    @Override
    public String convert(Password source) {
      return source.getValue();
    }
  }

  /** StringをPasswordに変換するコンバーター。 */
  @ReadingConverter
  static class StringToPasswordConverter implements Converter<String, Password> {
    @Override
    public Password convert(String source) {
      return new Password(source);
    }
  }

  /** RealNameをStringに変換するコンバーター。 */
  @WritingConverter
  static class RealNameToStringConverter implements Converter<RealName, String> {
    @Override
    public String convert(RealName source) {
      return source.getValue();
    }
  }

  /** StringをRealNameに変換するコンバーター。 */
  @ReadingConverter
  static class StringToRealNameConverter implements Converter<String, RealName> {
    @Override
    public RealName convert(String source) {
      return new RealName(source);
    }
  }

  /** UserNameをStringに変換するコンバーター。 */
  @WritingConverter
  static class UserNameToStringConverter implements Converter<UserName, String> {
    @Override
    public String convert(UserName source) {
      return source.getValue();
    }
  }

  /** StringをUserNameに変換するコンバーター。 */
  @ReadingConverter
  static class StringToUserNameConverter implements Converter<String, UserName> {
    @Override
    public UserName convert(String source) {
      return new UserName(source);
    }
  }

  /** UrlをStringに変換するコンバーター。 */
  @WritingConverter
  static class UrlToStringConverter implements Converter<Url, String> {
    @Override
    public String convert(Url source) {
      return source != null ? source.getValue() : null;
    }
  }

  /** StringをUrlに変換するコンバーター。 */
  @ReadingConverter
  static class StringToUrlConverter implements Converter<String, Url> {
    @Override
    public Url convert(String source) {
      return source != null ? new Url(source) : null;
    }
  }

  /** UserRoleをStringに変換するコンバーター。 */
  @WritingConverter
  static class UserRoleToStringConverter implements Converter<UserRole, String> {
    @Override
    public String convert(UserRole source) {
      return source.name();
    }
  }

  /** StringをUserRoleに変換するコンバーター。 */
  @ReadingConverter
  static class StringToUserRoleConverter implements Converter<String, UserRole> {
    @Override
    public UserRole convert(String source) {
      return UserRole.valueOf(source);
    }
  }

  /** コースタイトルをStringに変換するコンバーター。 */
  @WritingConverter
  static class CourseTitleToStringConverter
      implements Converter<com.everrefine.elms.domain.model.course.Title, String> {
    @Override
    public String convert(com.everrefine.elms.domain.model.course.Title source) {
      return source.getValue();
    }
  }

  /** StringをコースタイトルTitle（Course）に変換するコンバーター。 */
  @ReadingConverter
  static class StringToCourseTitleConverter
      implements Converter<String, com.everrefine.elms.domain.model.course.Title> {
    @Override
    public com.everrefine.elms.domain.model.course.Title convert(String source) {
      return new com.everrefine.elms.domain.model.course.Title(source);
    }
  }

  /** DescriptionをStringに変換するコンバーター。 */
  @WritingConverter
  static class CourseDescriptionToStringConverter implements Converter<Description, String> {
    @Override
    public String convert(Description source) {
      return source != null ? source.getValue() : null;
    }
  }

  /** StringをDescriptionに変換するコンバーター。 */
  @ReadingConverter
  static class StringToCourseDescriptionConverter implements Converter<String, Description> {
    @Override
    public Description convert(String source) {
      return source != null ? new Description(source) : null;
    }
  }

  /** お知らせタイトルをStringに変換するコンバーター。 */
  @WritingConverter
  static class NewsTitleToStringConverter
      implements Converter<com.everrefine.elms.domain.model.news.Title, String> {
    @Override
    public String convert(com.everrefine.elms.domain.model.news.Title source) {
      return source.getValue();
    }
  }

  /** StringをTitle（News）に変換するコンバーター。 */
  @ReadingConverter
  static class StringToNewsTitleConverter
      implements Converter<String, com.everrefine.elms.domain.model.news.Title> {
    @Override
    public com.everrefine.elms.domain.model.news.Title convert(String source) {
      return new com.everrefine.elms.domain.model.news.Title(source);
    }
  }

  /** ContentをStringに変換するコンバーター。 */
  @WritingConverter
  static class NewsContentToStringConverter implements Converter<Content, String> {
    @Override
    public String convert(Content source) {
      return source.getValue();
    }
  }

  /** StringをContentに変換するコンバーター。 */
  @ReadingConverter
  static class StringToNewsContentConverter implements Converter<String, Content> {
    @Override
    public Content convert(String source) {
      return new Content(source);
    }
  }

  /** OrderをBigDecimalに変換するコンバーター。 */
  @WritingConverter
  static class OrderToBigDecimalConverter implements Converter<Order, BigDecimal> {
    @Override
    public BigDecimal convert(Order source) {
      return source.getValue();
    }
  }

  /** BigDecimalをOrderに変換するコンバーター。 */
  @ReadingConverter
  static class BigDecimalToOrderConverter implements Converter<BigDecimal, Order> {
    @Override
    public Order convert(BigDecimal source) {
      return new Order(source);
    }
  }

  /** レッスンタイトルをStringに変換するコンバーター。 */
  @WritingConverter
  static class LessonTitleToStringConverter
      implements Converter<com.everrefine.elms.domain.model.lesson.Title, String> {
    @Override
    public String convert(com.everrefine.elms.domain.model.lesson.Title source) {
      return source.getValue();
    }
  }

  /** StringをTitle（Lesson）に変換するコンバーター。 */
  @ReadingConverter
  static class StringToLessonTitleConverter
      implements Converter<String, com.everrefine.elms.domain.model.lesson.Title> {
    @Override
    public com.everrefine.elms.domain.model.lesson.Title convert(String source) {
      return new com.everrefine.elms.domain.model.lesson.Title(source);
    }
  }

  /** レッスンコンテンツをStringに変換するコンバーター。 */
  @WritingConverter
  static class LessonContentToStringConverter
      implements Converter<com.everrefine.elms.domain.model.lesson.Content, String> {
    @Override
    public String convert(com.everrefine.elms.domain.model.lesson.Content source) {
      return source != null ? source.getValue() : null;
    }
  }

  /** StringをContent（Lesson）に変換するコンバーター。 */
  @ReadingConverter
  static class StringToLessonContentConverter
      implements Converter<String, com.everrefine.elms.domain.model.lesson.Content> {
    @Override
    public com.everrefine.elms.domain.model.lesson.Content convert(String source) {
      return source != null ? new com.everrefine.elms.domain.model.lesson.Content(source) : null;
    }
  }
}
