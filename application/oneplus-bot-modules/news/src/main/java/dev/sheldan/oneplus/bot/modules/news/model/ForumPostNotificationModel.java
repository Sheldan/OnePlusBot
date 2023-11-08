package dev.sheldan.oneplus.bot.modules.news.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ForumPostNotificationModel {
    private List<ForumPostNotificationEntry> entries;
}
