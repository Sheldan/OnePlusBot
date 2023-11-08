package dev.sheldan.oneplus.bot.modules.news.service;

import com.google.gson.Gson;
import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.oneplus.bot.modules.news.model.database.NewsSource;
import dev.sheldan.oneplus.bot.modules.news.model.forum.ForumPost;
import dev.sheldan.oneplus.bot.modules.news.model.forum.NewsForumPostResponse;
import dev.sheldan.oneplus.bot.modules.news.model.forum.NewsSourceResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ForumApiClient {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private Gson gson;

    @Value("${abstracto.feature.news.userURL}")
    private String userRequestURL;

    @Value("${abstracto.feature.news.threadURL}")
    private String threadInfoUrl;

    public Long getCurrentThreadCount(NewsSource newsSource) {
        Request request = new Request.Builder()
                .url(String.format(userRequestURL, newsSource.getUserId()))
                .get()
                .build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new AbstractoRunTimeException(String.format("Failed to load user info for id %s", newsSource.getUserId()));
            }
            NewsSourceResponse newsSourceResponse = gson.fromJson(response.body().string(), NewsSourceResponse.class);
            if (newsSourceResponse.getUser() != null
                    && newsSourceResponse.getUser().getUserStats() != null
                    && newsSourceResponse.getUser().getUserStats().getThreadCount() != null) {
                return newsSourceResponse.getUser().getUserStats().getThreadCount();
            }
        } catch (IOException e) {
            throw new AbstractoRunTimeException(e);
        }
        return 0L;
    }

    public List<ForumPost> getPostsOfSource(NewsSource source) {
        Request request = new Request.Builder()
                .url(String.format(threadInfoUrl, source.getUserId()))
                .get()
                .build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new AbstractoRunTimeException(String.format("Failed to load thread info for id %s", source.getUserId()));
            }
            NewsForumPostResponse newsSourceResponse = gson.fromJson(response.body().string(), NewsForumPostResponse.class);
            if (newsSourceResponse.getNewsForumPostData() != null
                    && newsSourceResponse.getNewsForumPostData().getRows() != null) {
                return newsSourceResponse
                        .getNewsForumPostData()
                        .getRows()
                        .stream()
                        .map(dataRow -> ForumPost.fromRow(dataRow, source))
                        .toList();
            }
        } catch (IOException e) {
            throw new AbstractoRunTimeException(e);
        }
        return new ArrayList<>();
    }

}
