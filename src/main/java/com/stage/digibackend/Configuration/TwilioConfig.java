package com.stage.digibackend.Configuration;

import com.twilio.Twilio;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioConfig {
    private String accountSid;
    private String authToken;
    private String trialNumber;
    @PostConstruct
    public void init() {
        Twilio.init(getAccountSid(),getAuthToken());
    }

}
