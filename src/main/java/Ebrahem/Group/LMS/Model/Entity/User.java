package Ebrahem.Group.LMS.Model.Entity;

import Ebrahem.Group.LMS.Model.Enums.OtpStatus;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Utilitys.PublicUtility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "users")
public class User {

    @Id
    @Column(length = 12, columnDefinition = "CHAR(12)", updatable = false, nullable = false)
    private String userId;


    @Column(nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpStatus otpStatus;

    @Column(nullable = false)
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "instructor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Course> courses;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Enrollment> enrollments;

    public User(String userName, String userEmail, String userPassword, Role role, OtpStatus otpStatus, LocalDateTime verifiedAt) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.role = role;
        this.otpStatus = otpStatus;
        this.verifiedAt = verifiedAt;
    }

    @PrePersist
    protected void onCreated() {
        this.createdAt = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
        if (this.userId == null) {
            this.userId = PublicUtility.generateId();
        }
    }
}
