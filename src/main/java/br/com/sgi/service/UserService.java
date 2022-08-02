package br.com.sgi.service;

import br.com.sgi.entity.transformer.UserTransformer;
import br.com.sgi.repository.UserRepository;
import br.com.sgi.controller.dto.UserRequestDTO;
import br.com.sgi.entity.User;
import br.com.sgi.service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTransformer userTransformer;

    public UserDTO create (UserRequestDTO dto) {
        try {
            return userTransformer.toDTO(
                    userRepository.save(User.builder()
                            .identifier(dto.getIdentifier())
                            .name(dto.getName())
                            .document(dto.getDocument())
                            .creation(LocalDateTime.now())
                            .updated(dto.getUpdated()).build()));

        } catch (DataIntegrityViolationException ex) {
            if (ex.getRootCause() instanceof SQLException) {
                final SQLException rootCause = (SQLException) ex.getRootCause();
                if (rootCause != null && rootCause.getSQLState().equals("23505")) {
                    throw new EntityExistsException("User already exists: " + dto.getName());
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
        user.setDocument(dto.getDocument());
        user.setIdentifier(dto.getIdentifier());
        user.setUpdated(dto.getUpdated());
        user.setCreation(dto.getCreation());
        return userRepository.save(user);
    }

    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(userTransformer::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user: " + id));
    }


}
