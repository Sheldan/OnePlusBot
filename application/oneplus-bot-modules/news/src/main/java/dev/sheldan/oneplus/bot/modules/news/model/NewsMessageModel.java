package dev.sheldan.oneplus.bot.modules.news.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

@Getter
@Setter
@Builder
public class NewsMessageModel {
    private String messageText;
    private Message message;
    private Member author;
    private Role newsRole;
}
