package dev.sheldan.oneplus.bot.modules.faq.models.command.faq;

import dev.sheldan.oneplus.bot.modules.faq.models.database.FAQCommandResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

@Getter
@Setter
@Builder
public class FAQResponseMessageModel {
    private User author;
    private String additionalMessage;
    private String description;
    private String imageURL;
    private Integer red;
    private Integer green;
    private Integer blue;

    public static FAQResponseMessageModel fromFAQCommandResponse(FAQCommandResponse response, User user) {
        return FAQResponseMessageModel
                .builder()
                .author(user)
                .additionalMessage(response.getAdditionalMessage())
                .description(response.getDescription())
                .blue(response.getBlue())
                .green(response.getGreen())
                .red(response.getRed())
                .imageURL(response.getImageURL())
                .build();
    }
}
