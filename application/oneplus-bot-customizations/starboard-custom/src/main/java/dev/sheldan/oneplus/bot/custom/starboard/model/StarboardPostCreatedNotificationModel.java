package dev.sheldan.oneplus.bot.custom.starboard.model;

import dev.sheldan.abstracto.core.models.ServerChannelMessage;
import dev.sheldan.abstracto.starboard.model.database.StarboardPost;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

@Getter
@Setter
@Builder
public class StarboardPostCreatedNotificationModel {
    private StarboardPost post;
    private ServerChannelMessage starboardMessageSimple;
    private Message starboardMessage;
    private ServerChannelMessage starredMessageSimple;
    private Message starredMessage;
    private Member starredMember;
    private Long starredUserId;
    private Member starringMember;
    private Long starringUserId;
}
