package dev.sheldan.oneplus.bot.modules.news.model;

import dev.sheldan.oneplus.bot.modules.news.model.forum.ForumPost;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ForumPostNotificationEntry {
    private Long postId;
    private String subject;
    private String content;
    private Long creatorId;

    public static ForumPostNotificationEntry fromPost(ForumPost forumPost) {
        return ForumPostNotificationEntry
                .builder()
                .postId(forumPost.getId())
                .subject(forumPost.getSubject())
                .content(forumPost.getContent())
                .creatorId(forumPost.getSource().getUserId())
                .build();
    }
}
