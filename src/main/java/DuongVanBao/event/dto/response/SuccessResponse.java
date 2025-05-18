package DuongVanBao.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse<T> {
    @Builder.Default
    private boolean success = true;
    private String message;
    private T data;

    public static <T> SuccessResponse<T> withData (T data) {
        return SuccessResponse.<T>builder()
                .data(data)
                .build();
    }

    public static <T> SuccessResponse<T> withMessage (T data, String message) {
        return SuccessResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }
}