package Ebrahem.Group.LMS.Model.Dtos;


import java.util.UUID;

public record CourseResponse (
        UUID courseId,
        String courseName,
        String instructorName,
        UUID instructorId
){
}
