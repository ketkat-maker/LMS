package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private JwtProviderServiceImpl jwtProviderService;
    @Mock
    private UserRepository repository;
    @InjectMocks
    private AdminServiceImpl adminService;
    private List<Role> roles;
    private List<User> testUser;
    private User user1;
    private User user2;
    private List<UserResponse> userResponse;

//    @BeforeEach
//    void setUp() {

    /// /        this.repository
//        this.user2 = User.builder().
//                userName("Ebrahem Hany").
//                userPassword("Ebrahem-hany")
//                .userEmail("ebrahemhany@test.com")
//                .role(Role.STUDENT).
//                userId(UUID.fromString("4a9771d1-b3cd-470c-a0e6-e5fc929d2944"))
//                .enrollments(new ArrayList<>())
//                .createdAt(LocalDateTime.now())
//                .enrollments(new ArrayList<>())
//                .build();
//        this.user1 = User.builder().
//                userName("Ebrahem Hany1").
//                userPassword("Ebrahem-hany")
//                .userEmail("ebrahemhany@test.com")
//                .role(Role.INSTRUCTOR).
//                userId(UUID.fromString("07faf878-8b39-43c9-8789-fc99b4a877cd"))
//                .enrollments(new ArrayList<>())
//                .createdAt(LocalDateTime.now())
//                .enrollments(new ArrayList<>())
//                .build();
//        this.roles = Arrays.asList(Role.STUDENT, Role.INSTRUCTOR);
//        this.testUser = Arrays.asList(user2, user1);
//        this.userResponse = testUser.stream()
//                .map(user -> new UserResponse(
//                        user.getUserId(),
//                        user.getUserName(),
//                        user.getUserEmail(),
//                        user.getCreatedAt(),
//                        user.getRole()
//                )).toList();
//    }

//    @Test
//    void getAllStudentAndInstructor() {
//        //given
//        when(repository.findByRoleIn(anyList()))
//                .thenReturn(testUser);
//        //when
//        List<UserResponse> result = AdminServiceImplTest.this.adminService.getAllStudentAndInstructor();
//        //then
//        assertNotNull(result);
//        assertEquals(2, result.size());
//    }
//
//    @Test
//    void deleteStudentOrInstructor() {
//        //given
//        UUID userId = UUID.randomUUID();
//        //then
//        when(repository.existsById(userId)).thenReturn(false);
//
//        assertThatExceptionOfType(IllegalArgumentException.class)
//                .isThrownBy(() -> adminService.deleteStudentOrInstructor(userId))
//                .withMessage("user not found by this id: " + userId);
//    }
    @Test
    void getAllByAdmin() {
    }
}