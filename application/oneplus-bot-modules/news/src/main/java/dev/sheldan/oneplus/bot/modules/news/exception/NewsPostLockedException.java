package dev.sheldan.oneplus.bot.modules.news.exception;

import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.core.templating.Templatable;

public class NewsPostLockedException  extends AbstractoRunTimeException implements Templatable {
    @Override
    public String getTemplateName() {
        return "news_post_locked_exception";
    }

    @Override
    public Object getTemplateModel() {
        return new Object();
    }
}
