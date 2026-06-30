package com.everrefine.elms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger/OpenAPI設定に関するクラス。 */
@Configuration
public class SwaggerConfig {

  /**
   * OpenAPI定義を生成する。
   *
   * @return OpenAPI定義
   */
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("e-learning-management-system API")
                .description("e-learning-management-system API")
                .version("1.0.0"))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(
            new Components()
                .addSchemas(
                    "ErrorResponse",
                    new ObjectSchema()
                        .description("エラーレスポンス")
                        .addProperty(
                            "code",
                            new StringSchema().description("エラーコード").example("RESOURCE_NOT_FOUND"))
                        .addProperty(
                            "message",
                            new StringSchema().description("エラーメッセージ").example("リソースが見つかりません")))
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  /**
   * 全エンドポイントの4xx/5xxレスポンスにErrorResponseスキーマを自動適用するカスタマイザーを生成する。
   *
   * @return OpenAPIカスタマイザー
   */
  @Bean
  public OpenApiCustomizer errorResponseCustomizer() {
    var errorContent =
        new Content()
            .addMediaType(
                "application/json",
                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")));
    return openApi ->
        openApi
            .getPaths()
            .values()
            .forEach(
                pathItem ->
                    pathItem
                        .readOperations()
                        .forEach(
                            operation ->
                                operation
                                    .getResponses()
                                    .forEach(
                                        (code, response) -> {
                                          try {
                                            if (Integer.parseInt(code) >= 400) {
                                              response.setContent(errorContent);
                                            }
                                          } catch (NumberFormatException ignored) {
                                          }
                                        })));
  }
}
