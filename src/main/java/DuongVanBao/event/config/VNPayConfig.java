package DuongVanBao.event.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig {
    private String version;
    private String tmnCode;
    private String hashSecret;
    private String returnUrl;
    private String paymentUrl;
    private String apiUrl;
    private String command;
    private String currCode;
    private String locale;
}
