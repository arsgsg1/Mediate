package com.ko.mediate.HC.community.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.ko.mediate.HC.community.domain.Article;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class GetArticleDto {
  private long articleId;
  private String title;
  private String content;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss",
      timezone = "Asia/Seoul")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime createAt;

  private long like;
  private long view;

  public GetArticleDto(Article article) {
    this.articleId = article.getId();
    this.title = article.getTitle();
    this.content = article.getContent();
    this.createAt = article.getCreateAt();
    this.like = article.getLike();
    this.view = article.getView();
  }

  public GetArticleDto(long articleId, String title, String content, LocalDateTime createAt,
      long like, long view) {
    this.articleId = articleId;
    this.title = title;
    this.content = content;
    this.createAt = createAt;
    this.like = like;
    this.view = view;
  }
}
