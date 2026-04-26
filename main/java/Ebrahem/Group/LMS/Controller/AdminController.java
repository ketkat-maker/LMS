package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(path = "/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "Get all students And  instructors role ADMIN")
    @GetMapping("/Users")
    public ResponseEntity<List<UserResponse>> getUserUsingRole(
            @RequestBody List<Role> roles
    ) {
        List<UserResponse> allUsers = adminService.getUsersByRoles(roles);
        return ResponseEntity.ok(allUsers);
    }

    @Operation(summary = "Delete  instructor or student by ID", description = "Requires ADMIN role")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteStudentOrInstructor(@PathVariable UUID id) {
        adminService.deleteStudentOrInstructor(id);
        return new ResponseEntity<>(
                "UserResponse delete successfully ", HttpStatus.NO_CONTENT
        );
    }

}
