package br.com.sgi.service;


import br.com.sgi.controller.dto.UserRequestDTO;
import br.com.sgi.entity.User;
import br.com.sgi.entity.transformer.UserTransformer;
import br.com.sgi.repository.UserRepository;
import br.com.sgi.service.dto.UserDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends AbstractTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private static UserTransformer userTransformer;

    @BeforeAll
    static void setUp() {
        userTransformer = mock(UserTransformer.class);
        ModelMapper modelMapper = new ModelMapper();
        ReflectionTestUtils.setField(userTransformer, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(userTransformer, "dtoClass", UserDTO.class);
    }

    @BeforeEach
    void beforeEach() {
        when(userTransformer.toDTO(any(User.class))).thenCallRealMethod();
    }

    @Test
    void updateWhithInvalidUserIdShouldThrowException() {
        UserRequestDTO request = createUserRequestDTO();

        doReturn(Optional.empty()).when(userRepository).findById(request.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.update(request));
        assertThat(ex.getMessage()).isEqualTo("Could not find user: " + request.getId());


        verify(userRepository).findById(eq(request.getId()));
    }

    @Test
    void updateWhithValidUserIdShouldUpdate() {
        UserRequestDTO request = createUserRequestDTO();
        User user = createUser();

        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());

        userService.update(request);
        assertThat(user.getUsername()).isEqualTo(request.getUsername());

        verify(userRepository).findById(anyLong());
    }

    @Test
    void createWhithUserExistsShouldThrowException() {
        DataIntegrityViolationException mockEx = mock(DataIntegrityViolationException.class);
        SQLException mockSqlEx = mock(SQLException.class);
        UserRequestDTO dto = createUserRequestDTO();

        when(mockEx.getRootCause()).thenReturn(mockSqlEx);
        when(((SQLException) mockEx.getRootCause()).getSQLState()).thenReturn("23505");
        when(userRepository.save(any(User.class))).thenThrow(mockEx);

        EntityExistsException ex = assertThrows(EntityExistsException.class, () -> userService.create(dto));

        verify(userRepository).save(any(User.class));
        assertThat(ex.getMessage()).isEqualTo("User already exists: " + dto.getUsername());
    }

    @Test
    void createWithSQLExceptionShouldThrowException() {
        DataIntegrityViolationException mockEx = mock(DataIntegrityViolationException.class);
        SQLException mockSqlEx = mock(SQLException.class);
        UserRequestDTO dto = createUserRequestDTO();

        when(mockEx.getRootCause()).thenReturn(mockSqlEx);
        when(((SQLException) mockEx.getRootCause()).getSQLState()).thenReturn("anyState");
        when(userRepository.save(any(User.class))).thenThrow(mockEx);

        verify(mockEx).getRootCause();
        assertThrows(DataIntegrityViolationException.class, () -> userService.create(dto));
    }

    @Test
    void createWhithDataIntegrityViolationShouldThrowException() {
        DataIntegrityViolationException mockEx = mock(DataIntegrityViolationException.class);
        UserRequestDTO dto = createUserRequestDTO();

        when(userRepository.save(any(User.class))).thenThrow(mockEx);

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(dto));
    }

    @Test
    void findByIdWithValidIdShouldReturnDTO() {
        User user = createUser();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDTO dto = userService.findById(1L);
        assertThat(dto.getUsername()).isEqualTo(user.getUsername());

        verify(userRepository).findById(anyLong());
    }

    @Test
    void findByIdWithInvalidIdShouldThrowException() {
        doReturn(Optional.empty()).when(userRepository).findById(anyLong());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
        assertThat(ex.getMessage()).isEqualTo("Could not find user: 1");

        verify(userRepository).findById(anyLong());
    }


}