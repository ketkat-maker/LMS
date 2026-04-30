package Ebrahem.Group.LMS.Model.Entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @Column(length = 12, columnDefinition = "CHAR(12)", updatable = false, nullable = false)
    private String courseId;

    @Column(nullable = false)
    private String courseTitle;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime courseDuration;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;

    public Course(String courseTitle, LocalDateTime courseDuration, User instructor) {
        this.courseTitle = courseTitle.toLowerCase();
        this.courseDuration = courseDuration;
        this.instructor = instructor;
    }

    @PrePersist
    void createdAt() {
        if (this.courseId == null) {
            char[] alphabet = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
            this.courseId = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet, 12);
        }
        this.createdAt = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);

    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
    }
}
