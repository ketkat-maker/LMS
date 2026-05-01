package Ebrahem.Group.LMS.Model.Entity;

import Ebrahem.Group.LMS.Model.Enums.Status;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @Column(length = 12, columnDefinition = "CHAR(12)", updatable = false, nullable = false)
    private String enrollmentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime enrollAt;

    @Column(nullable = false)
    @Builder.Default
    private float progress = 0;

    public Enrollment(User user, Course course, Status status, float progress, LocalDateTime enrollAt) {
        this.user = user;
        this.course = course;
        this.status = status;
        this.progress = progress;
        this.enrollAt = enrollAt;
    }

    @PrePersist
    private void onEnroll() {
        if (this.enrollmentId == null) {
            char[] alphabet = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
            this.enrollmentId = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet, 12);
            this.enrollAt = LocalDateTime.now();
        }
    }
}
