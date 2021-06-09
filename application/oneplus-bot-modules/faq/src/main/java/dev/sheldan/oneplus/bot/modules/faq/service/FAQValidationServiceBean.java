package dev.sheldan.oneplus.bot.modules.faq.service;

import dev.sheldan.abstracto.core.models.JSONValidationResult;
import dev.sheldan.abstracto.core.service.JSONValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class FAQValidationServiceBean {

    @Autowired
    private JSONValidationService jsonValidationService;

    @Value("classpath:validation/createScheme.json")
    private Resource creationValidationResource;

    private String creationValidationSchema;

    public JSONValidationResult validateJSONForCreation(String json) {
        log.info("Validating FAQ creation schema.");
        return jsonValidationService.validateJSONSchema(creationValidationSchema, json);
    }

    @PostConstruct
    public void postConstruct() {
        try {
            this.creationValidationSchema = IOUtils.toString(creationValidationResource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load validation schemes.", e);
        }
    }
}
