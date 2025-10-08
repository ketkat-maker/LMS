package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.Student;
import Ebrahem.Group.LMS.Model.Dtos.UsersDto;
import Ebrahem.Group.LMS.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class UserController {
     private final UserService userService;
     @Operation(summary = "Get all students And  instructors role ADMIN")
    @GetMapping("/Users")
    public ResponseEntity<List<Student>> getAllStudentAndInstructor() {
        List<Student> allUsers = userService.getAllStudentAndInstructor();
        return ResponseEntity.ok(allUsers);
    }

    @Operation(summary = "Delete a student by ID", description = "Requires ADMIN role")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteStudentOrInstructor(@PathVariable UUID id){
        userService.deleteStudentOrInstructor(id);
        return new ResponseEntity<>(
                "Student delete successfully ",HttpStatus.NO_CONTENT
        );
    }
    @GetMapping("/allUsers")
    public ResponseEntity<List<UsersDto>>getAllUsers(){
        List<UsersDto> allByAdmin = userService.getAllByAdmin();
        return  ResponseEntity.ok(allByAdmin);

    }
}
