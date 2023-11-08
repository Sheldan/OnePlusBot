package dev.sheldan.oneplus.bot.modules.news.model.forum;

import dev.sheldan.oneplus.bot.modules.news.model.database.NewsSource;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ForumPost {
    private Long id;
    private String subject;
    private String content;
    private NewsSource source;

    public static ForumPost fromRow(NewsForumPostDataRow dataRow, NewsSource newsSource) {
        return ForumPost
                .builder()
                .subject(dataRow.getSubject())
                .id(dataRow.getId())
                .content(dataRow.getContent())
                .source(newsSource)
                .build();
    }
}
