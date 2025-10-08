package Ebrahem.Group.LMS.Controller;


import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Service.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/instructor")
public class InstructorController {
    private final InstructorService instructorService;
    @Operation(summary = "Create new course by Instructor")
    @PostMapping(path = "/createCourse")
    public ResponseEntity<CourseResponse>createCourse(@Valid @RequestBody CourseRequest course){
        CourseResponse createdCourse = instructorService.CreateCourseResponse(course);
        return new ResponseEntity<>(
               createdCourse,
                CREATED
        );
    }
    @Operation(summary = "Delete course by Instructor")
    @DeleteMapping("/deleteCourse/{id}")
    public ResponseEntity<CourseResponse>deleteCourse(@PathVariable UUID id){
        CourseResponse response = instructorService.deleteCourse(id);
        return new ResponseEntity<>(
                response,
                ACCEPTED
        );
    }
    @Operation(summary = "Update course by instructor")
    @PutMapping(path = "/updateCourse/{id}")
    public ResponseEntity<CourseResponse>updatedCourse(@PathVariable UUID id,
                                                       @RequestBody CourseRequest request ){
        CourseResponse response = instructorService.updateCourse(id, request);
return new ResponseEntity<>(
        response,
        OK
);
    }
}
