package sme.tech.innovators.sme.unit;

import org.junit.jupiter.api.Test;
import sme.tech.innovators.sme.dto.response.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void successSetsSuccessTrueAndErrorNull() {
        ApiResponse<String> response = ApiResponse.success("data");
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertEquals("data", response.getData());
    }

    @Test
    void errorSetsSuccessFalseAndDataNull() {
        ApiResponse<Void> response = ApiResponse.error("CODE", "message");
        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("CODE", response.getError().getCode());
        assertEquals("message", response.getError().getMessage());
    }

    @Test
    void timestampIsIso8601Format() {
        ApiResponse<String> response = ApiResponse.success("test");
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().endsWith("Z"),
                "Timestamp should be ISO-8601 UTC: " + response.getTimestamp());
    }
}
