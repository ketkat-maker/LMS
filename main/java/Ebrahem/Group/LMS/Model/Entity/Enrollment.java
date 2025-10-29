package Ebrahem.Group.LMS.Model.Entity;

import Ebrahem.Group.LMS.Model.Enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID enrollmentId;

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
        if (this.enrollAt == null) {
            this.enrollAt = LocalDateTime.now();
        }
    }
}
