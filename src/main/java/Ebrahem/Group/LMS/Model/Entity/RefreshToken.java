package Ebrahem.Group.LMS.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class RefreshToken {

    @Id
    private String id;

    @Column(unique = true, nullable = false, length = 300)
    private String token;

    @Builder.Default
    private boolean revoked = false;

    @Builder.Default
    private boolean expired = false;
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
