package sme.tech.innovators.sme.property;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import sme.tech.innovators.sme.dto.response.ApiResponse;

public class ApiResponsePropertyTest {

    @Property(tries = 50)
    void successResponseHasNullError(@ForAll String data) {
        ApiResponse<String> response = ApiResponse.success(data);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getError());
        Assertions.assertNotNull(response.getData());
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Property(tries = 50)
    void errorResponseHasNullData(@ForAll String code, @ForAll String message) {
        ApiResponse<Void> response = ApiResponse.error(code, message);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNull(response.getData());
        Assertions.assertNotNull(response.getError());
        Assertions.assertNotNull(response.getTimestamp());
        Assertions.assertEquals(code, response.getError().getCode());
    }

    @Property(tries = 50)
    void timestampIsIso8601Format(@ForAll String data) {
        ApiResponse<String> response = ApiResponse.success(data);
        // ISO-8601 instant format: ends with Z
        Assertions.assertTrue(response.getTimestamp().endsWith("Z"),
                "Timestamp not ISO-8601: " + response.getTimestamp());
    }
}
