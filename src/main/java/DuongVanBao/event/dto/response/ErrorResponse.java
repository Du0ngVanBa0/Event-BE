package DuongVanBao.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @Builder.Default
    private boolean success = false;
    private String message;
    private Map<String, String> errors;

    public static ErrorResponse withMessage(String message) {
        return ErrorResponse.builder()
                .message(message)
                .build();
    }

    public static ErrorResponse withErrors(Map<String, String> errors) {
        return ErrorResponse.builder()
                .errors(errors)
                .build();
    }

    public static ErrorResponse withErrors(String message, Map<String, String> errors) {
        return ErrorResponse.builder()
                .message(message)
                .errors(errors)
                .build();
    }
}
