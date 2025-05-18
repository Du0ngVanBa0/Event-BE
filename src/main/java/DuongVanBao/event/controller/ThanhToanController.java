package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.CreatePaymentRequest;
import DuongVanBao.event.dto.response.CreatePaymentResponse;
import DuongVanBao.event.dto.response.PaymentResultResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.service.impl.ThanhToanService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class ThanhToanController {
    private final ThanhToanService thanhToanService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest request, HttpServletRequest servletRequest) throws UnsupportedEncodingException {
        CreatePaymentResponse response = thanhToanService.createPaymentUrl(request, servletRequest);
        return ResponseEntity.ok(SuccessResponse.withData(response));
    }

    @GetMapping("/process-return")
    public ResponseEntity<?> processPaymentReturn(HttpServletRequest request) {
        Map<String, String> requestParams = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            requestParams.put(paramName, paramValue);
        }

        PaymentResultResponse response = thanhToanService.processPaymentReturn(requestParams);
        return ResponseEntity.ok(SuccessResponse.withData(response));
    }
}
