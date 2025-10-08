package Ebrahem.Group.LMS.Model.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID courseId;

    @Column(nullable = false)
    private String courseTitle;

    @Convert(converter = DurationConverter.class)
    private Duration courseDuration;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;

    public Course(String courseTitle, Duration courseDuration, User instructor) {
        this.courseTitle = courseTitle.toLowerCase();
        this.courseDuration = courseDuration;
        this.instructor = instructor;
    }

    @PrePersist
    void createdAt() {
        this.createdAt = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
    }
}
