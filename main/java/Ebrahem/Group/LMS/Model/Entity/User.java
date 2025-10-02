package Ebrahem.Group.LMS.Model.Entity;

import Ebrahem.Group.LMS.Model.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @GeneratedValue(strategy = GenerationType.UUID )
    @Id
    private UUID userId;

    @Column(nullable = false)
    private String userName;

    @Column(unique = true,nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;

    @OneToMany(mappedBy = "user")
    private List<Enrollment> enrollments;

    public User(UUID userId,String userEmail,String userPassword,Role role) {
        this.userId=userId;
        this.userEmail=userEmail.toLowerCase();
        this.userPassword=userPassword;
        this.role=role;
    }

    public User(String userPassword, Role role, String userEmail, String userName) {
        this.userPassword = userPassword;
        this.role = role;
        this.userEmail = userEmail.toLowerCase();
        this.userName = userName.toLowerCase();
    }


    @PrePersist
    protected void onCreated(){
        this.createdAt=LocalDateTime.now();
    }
}
