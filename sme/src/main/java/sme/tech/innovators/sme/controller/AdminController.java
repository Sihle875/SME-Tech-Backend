package sme.tech.innovators.sme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sme.tech.innovators.sme.dto.response.ApiResponse;
import sme.tech.innovators.sme.dto.response.BusinessDto;
import sme.tech.innovators.sme.dto.response.UserDto;
import sme.tech.innovators.sme.dto.response.UserWithBusinessDto;
import sme.tech.innovators.sme.service.AdminQueryService;

import java.util.List;

@Tag(name = "Admin", description = "Admin endpoints for querying users and businesses — requires JWT authentication")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final AdminQueryService adminQueryService;

    @Operation(summary = "Get all users",
               description = "Returns all non-deleted users. Excludes passwords and sensitive auth fields.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of users returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminQueryService.getAllUsers()));
    }

    @Operation(summary = "Get all businesses",
               description = "Returns all non-deleted businesses.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of businesses returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/businesses")
    public ResponseEntity<ApiResponse<List<BusinessDto>>> getAllBusinesses() {
        return ResponseEntity.ok(ApiResponse.success(adminQueryService.getAllBusinesses()));
    }

    @Operation(summary = "Get all users with their mapped businesses",
               description = "Returns all non-deleted users with their associated business. Business is null if the user has no business registered.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of users with businesses returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/users-with-businesses")
    public ResponseEntity<ApiResponse<List<UserWithBusinessDto>>> getAllUsersWithBusinesses() {
        return ResponseEntity.ok(ApiResponse.success(adminQueryService.getAllUsersWithBusinesses()));
    }
}
