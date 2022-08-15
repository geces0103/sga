package br.com.sgi.service;

import br.com.sgi.controller.dto.UserRequestDTO;
import br.com.sgi.entity.User;
import br.com.sgi.entity.transformer.UserTransformer;
import br.com.sgi.repository.UserRepository;
import br.com.sgi.service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTransformer userTransformer;

    public UserDTO create (UserRequestDTO dto) {
        try {
            return userTransformer.toDTO(
                    userRepository.save(User.builder()
                            .id(dto.getId())
                            .username(dto.getUsername())
                            .description(dto.getDescription())
                            .email(dto.getEmail())
                            .active(Boolean.TRUE)
                            .hashPassword(dto.getHashPassword())
                            .creation(LocalDateTime.now())
                            .updated(LocalDateTime.now()).build()));

        } catch (DataIntegrityViolationException ex) {
            if (ex.getRootCause() instanceof SQLException) {
                final SQLException rootCause = (SQLException) ex.getRootCause();
                if (rootCause != null && rootCause.getSQLState().equals("23505")) {
                    throw new EntityExistsException("User already exists: " + dto.getUsername());
                } else {
                    throw ex;
                }
            } else {
                throw ex;
            }
        }
    }

    public UserDTO update (UserRequestDTO dto) {
        return userTransformer.toDTO(update(userRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException("Could not find user: " + dto.getId())), dto));
    }

    private User update(User user, UserRequestDTO dto) {
        user.setCreation(dto.getCreation());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setUpdated(dto.getUpdated());
        user.setCreation(dto.getCreation());
        return userRepository.save(user);
    }

    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(userTransformer::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user: " + id));
    }

    public List<UserDTO> findAll() {
        return  userRepository.findAll().stream().map(userTransformer::toDTO).collect(Collectors.toList());
    }

    public Set<UserDTO> findAllOrdered() {
        return  userRepository.findAll().stream().sorted().map(userTransformer::toDTO).collect(Collectors.toSet());
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserDTO> findByName(String name) {

        var list = userRepository.findByUsername(name).stream()
                .map(userTransformer::toDTO).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(list)) {
            return list;
        }
        throw new EntityNotFoundException("Could not find user: " + name);
    }


}
