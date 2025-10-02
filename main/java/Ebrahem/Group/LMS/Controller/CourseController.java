package Ebrahem.Group.LMS.Controller;


import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/course")
public class CourseController {
    private final CourseService courseService;
    @Operation(summary = "Create new course by Instructor")
    @PostMapping
    public ResponseEntity<CourseResponse>createCourse(@Valid @RequestBody CourseRequest course){
        CourseResponse createdCourse = courseService.CreateCourseResponse(course);
        return new ResponseEntity<>(
               createdCourse,
                CREATED
        );
    }
}
