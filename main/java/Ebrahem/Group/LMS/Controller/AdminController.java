package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Dtos.UsersDto;
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
    public ResponseEntity<List<UserResponse>> getAllStudentAndInstructor() {
        List<UserResponse> allUsers = adminService.getAllStudentAndInstructor();
        return ResponseEntity.ok(allUsers);
    }

    @Operation(summary = "Delete  instructor or student by ID", description = "Requires ADMIN role")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteStudentOrInstructor(@PathVariable UUID id){
        adminService.deleteStudentOrInstructor(id);
        return new ResponseEntity<>(
                "UserResponse delete successfully ",HttpStatus.NO_CONTENT
        );
    }
    @Operation(summary = "get all users include (inst and stud) ", description = "Requires ADMIN role")
    @GetMapping("/allUsers")
    public ResponseEntity<List<UsersDto>>getAllUsers(){
        List<UsersDto> allByAdmin = adminService.getAllByAdmin();
        return  ResponseEntity.ok(allByAdmin);

    }
}
