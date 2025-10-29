package Ebrahem.Group.LMS.Model.Entity;

import Ebrahem.Group.LMS.Model.Enums.Role;
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
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID userId;

    @Column(nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "instructor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Course> courses;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Enrollment> enrollments;

    public User(UUID userId, String userEmail, String userPassword, Role role) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.role = role;
    }

    public User(String userPassword, Role role, String userEmail, String userName) {
        if (userEmail != null) this.userEmail = userEmail.toLowerCase();
        if (userName != null) this.userName = userName.toLowerCase();
        this.userPassword = userPassword;
        this.role = role;
    }

    @PrePersist
    protected void onCreated() {
        this.createdAt = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
    }
}
