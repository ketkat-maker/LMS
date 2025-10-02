package Ebrahem.Group.LMS.Model.Entity;

import Ebrahem.Group.LMS.Model.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "courses")
public class Course {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID courseId;

    @Column(nullable = false)
    private String courseTitle;

    @Column(name = "duration")
    private int courseDuration;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;


    public Course(String courseTitle, int courseDuration, User instructor) {
        this.courseTitle = courseTitle.toLowerCase();
        this.courseDuration = courseDuration;
        this.instructor=instructor;
    }
}

