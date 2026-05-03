package sme.tech.innovators.sme.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import sme.tech.innovators.sme.config.SecurityConfig;
import sme.tech.innovators.sme.controller.AccountController;
import sme.tech.innovators.sme.dto.request.DeleteAccountRequest;
import sme.tech.innovators.sme.dto.request.UpdateBusinessRequest;
import sme.tech.innovators.sme.dto.request.UpdatePasswordRequest;
import sme.tech.innovators.sme.dto.request.UpdateProfileRequest;
import sme.tech.innovators.sme.dto.response.AccountProfileDto;
import sme.tech.innovators.sme.entity.AccountStatus;
import sme.tech.innovators.sme.entity.User;
import sme.tech.innovators.sme.entity.UserRole;
import sme.tech.innovators.sme.exception.BusinessAccessDeniedException;
import sme.tech.innovators.sme.exception.GlobalExceptionHandler;
import sme.tech.innovators.sme.repository.UserRepository;
import sme.tech.innovators.sme.security.CustomUserDetailsService;
import sme.tech.innovators.sme.security.JwtAuthenticationFilter;
import sme.tech.innovators.sme.service.AccountService;
import sme.tech.innovators.sme.service.JwtService;
import sme.tech.innovators.sme.service.RateLimitService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    // AccountController injects UserRepository directly to resolve userId from email
    @MockBean
    private UserRepository userRepository;

    // Required by SecurityConfig
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    // Required by WebMvcConfig → RateLimitInterceptor
    @MockBean
    private RateLimitService rateLimitService;

    private UUID userId;
    private User user;
    private AccountProfileDto profileDto;

    @BeforeEach
    void setUp() throws Exception {
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("owner@example.com")
                .password("$2a$12$hashed")
                .fullName("Jane Doe")
                .accountStatus(AccountStatus.VERIFIED)
                .role(UserRole.OWNER)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        profileDto = AccountProfileDto.builder()
                .userId(userId)
                .email("owner@example.com")
                .fullName("Jane Doe")
                .accountStatus(AccountStatus.VERIFIED)
                .role(UserRole.OWNER)
                .userCreatedAt(LocalDateTime.now().minusDays(1))
                .userUpdatedAt(LocalDateTime.now().minusDays(1))
                .businessId(UUID.randomUUID())
                .businessName("Acme Coffee")
                .slug("acme-coffee")
                .publicLink("https://example.com/store/acme-coffee")
                .businessDescription("Best coffee in town")
                .businessUpdatedAt(LocalDateTime.now().minusDays(1))
                .build();

        // Configure the JWT filter mock to pass through (not intercept requests)
        doAnswer(invocation -> {
            HttpServletRequest req = invocation.getArgument(0);
            HttpServletResponse res = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(req, res);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());

        // Default stub: resolve email → user for all authenticated requests
        when(userRepository.findByEmailAndIsDeletedFalse("owner@example.com"))
                .thenReturn(Optional.of(user));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/account/me
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void getProfile_returns200WithApiResponseEnvelope() throws Exception {
        when(accountService.getProfile(userId)).thenReturn(profileDto);

        mockMvc.perform(get("/api/v1/account/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                .andExpect(jsonPath("$.data.email").value("owner@example.com"))
                .andExpect(jsonPath("$.data.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.data.slug").value("acme-coffee"))
                .andExpect(jsonPath("$.data.publicLink").value("https://example.com/store/acme-coffee"));
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void getProfile_doesNotExposePasswordField() throws Exception {
        when(accountService.getProfile(userId)).thenReturn(profileDto);

        mockMvc.perform(get("/api/v1/account/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void getProfile_returns401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/account/me"))
                .andExpect(status().is4xxClientError());
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/account/profile
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void updateProfile_returns200WithUpdatedDto() throws Exception {
        AccountProfileDto updated = AccountProfileDto.builder()
                .userId(userId)
                .email("owner@example.com")
                .fullName("New Name")
                .accountStatus(AccountStatus.VERIFIED)
                .role(UserRole.OWNER)
                .businessId(profileDto.getBusinessId())
                .businessName("Acme Coffee")
                .slug("acme-coffee")
                .publicLink("https://example.com/store/acme-coffee")
                .build();

        when(accountService.updateProfile(eq(userId), any(UpdateProfileRequest.class))).thenReturn(updated);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("New Name");

        mockMvc.perform(put("/api/v1/account/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("New Name"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void updateProfile_returns400_whenFullNameIsBlank() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("   "); // blank

        mockMvc.perform(put("/api/v1/account/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void updateProfile_returns400_whenFullNameIsEmpty() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("");

        mockMvc.perform(put("/api/v1/account/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateProfile_returns401_whenNotAuthenticated() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("New Name");

        mockMvc.perform(put("/api/v1/account/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/account/password
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void updatePassword_returns200OnSuccess() throws Exception {
        doNothing().when(accountService).updatePassword(eq(userId), any(UpdatePasswordRequest.class));

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("OldPass1!");
        request.setNewPassword("NewPass1!");

        mockMvc.perform(put("/api/v1/account/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Password updated successfully"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void updatePassword_returns401_whenCurrentPasswordWrong() throws Exception {
        doThrow(new BadCredentialsException("Current password is incorrect"))
                .when(accountService).updatePassword(eq(userId), any(UpdatePasswordRequest.class));

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("NewPass1!");

        mockMvc.perform(put("/api/v1/account/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void updatePassword_returns400_whenCurrentPasswordIsBlank() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("");
        request.setNewPassword("NewPass1!");

        mockMvc.perform(put("/api/v1/account/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updatePassword_returns401_whenNotAuthenticated() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("OldPass1!");
        request.setNewPassword("NewPass1!");

        mockMvc.perform(put("/api/v1/account/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/account/business
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void updateBusiness_returns200WithUpdatedDto() throws Exception {
        AccountProfileDto updated = AccountProfileDto.builder()
                .userId(userId)
                .email("owner@example.com")
                .fullName("Jane Doe")
                .accountStatus(AccountStatus.VERIFIED)
                .role(UserRole.OWNER)
                .businessId(profileDto.getBusinessId())
                .businessName("New Business Name")
                .slug("acme-coffee") // slug unchanged
                .publicLink("https://example.com/store/acme-coffee")
                .build();

        when(accountService.updateBusiness(eq(userId), eq("OWNER"), any(UpdateBusinessRequest.class)))
                .thenReturn(updated);

        UpdateBusinessRequest request = new UpdateBusinessRequest();
        request.setName("New Business Name");

        mockMvc.perform(put("/api/v1/account/business")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.businessName").value("New Business Name"))
                .andExpect(jsonPath("$.data.slug").value("acme-coffee"));
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "EMPLOYEE")
    void updateBusiness_returns403_forEmployeeRole() throws Exception {
        doThrow(new BusinessAccessDeniedException("Employees are not permitted to update business details"))
                .when(accountService).updateBusiness(eq(userId), eq("EMPLOYEE"), any(UpdateBusinessRequest.class));

        UpdateBusinessRequest request = new UpdateBusinessRequest();
        request.setName("New Name");

        mockMvc.perform(put("/api/v1/account/business")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"));
    }

    @Test
    void updateBusiness_returns401_whenNotAuthenticated() throws Exception {
        UpdateBusinessRequest request = new UpdateBusinessRequest();
        request.setName("New Name");

        mockMvc.perform(put("/api/v1/account/business")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/account
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void deleteAccount_returns200OnSuccess() throws Exception {
        doNothing().when(accountService).deleteAccount(eq(userId), any(DeleteAccountRequest.class));

        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setPassword("correctPassword");

        mockMvc.perform(delete("/api/v1/account")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Account deleted successfully"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void deleteAccount_returns400_whenPasswordIsBlank() throws Exception {
        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setPassword(""); // blank

        mockMvc.perform(delete("/api/v1/account")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void deleteAccount_returns401_whenPasswordIsWrong() throws Exception {
        doThrow(new BadCredentialsException("Password is incorrect"))
                .when(accountService).deleteAccount(eq(userId), any(DeleteAccountRequest.class));

        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setPassword("wrongPassword");

        mockMvc.perform(delete("/api/v1/account")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void deleteAccount_returns4xx_whenNotAuthenticated() throws Exception {
        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setPassword("somePassword");

        mockMvc.perform(delete("/api/v1/account")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    // -------------------------------------------------------------------------
    // Response envelope structure
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void allSuccessResponses_containRequiredEnvelopeFields() throws Exception {
        when(accountService.getProfile(userId)).thenReturn(profileDto);

        mockMvc.perform(get("/api/v1/account/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "OWNER")
    void errorResponses_containRequiredEnvelopeFields() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName(""); // triggers 400

        mockMvc.perform(put("/api/v1/account/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").isNotEmpty())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
