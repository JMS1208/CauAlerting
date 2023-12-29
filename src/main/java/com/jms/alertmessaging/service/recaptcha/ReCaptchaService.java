package com.jms.alertmessaging.service.recaptcha;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@Service
public class ReCaptchaService {

    @Value("${recaptcha.key.secret}")
    private String recaptchaSecretKey;
    public boolean verifyRecaptcha(String recaptchaToken) {
        Map<String, String> body = new HashMap<>();
        body.put("secret", recaptchaSecretKey);
        body.put("response", recaptchaToken);

        RestTemplate restTemplate = new RestTemplate();

        URI verifyUri = URI.create(String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s",
                recaptchaSecretKey, recaptchaToken));

        GoogleResponse googleResponse = restTemplate.postForObject(verifyUri, body, GoogleResponse.class);

        assert googleResponse != null;
        return googleResponse.isSuccess();
    }

    @Data
    public static class GoogleResponse {
        private boolean success;
        private String challengeTs;
        private String hostname;
        private String[] errorCodes;

        // Getters and setters
    }
}
