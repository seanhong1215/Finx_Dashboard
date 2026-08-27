package com.finx.controller;

import com.finx.dto.request.CreateUserRequest;
import com.finx.dto.request.RoleUpdateRequest;
import com.finx.dto.request.StatusUpdateRequest;
import com.finx.dto.response.AdminSummaryResponse;
import com.finx.dto.response.ApiResponse;
import com.finx.dto.response.UserResponse;
import com.finx.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AdminSummaryResponse>> summary() {
        return ResponseEntity.ok(ApiResponse.success(adminService.summary()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> users() {
        return ResponseEntity.ok(ApiResponse.success(adminService.findUsers()));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(adminService.createUser(request)));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(@PathVariable Long id,
                                                                @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updateRole(id, request.getRole())));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(@PathVariable Long id,
                                                                  @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updateStatus(id, request.getActive())));
    }
}
