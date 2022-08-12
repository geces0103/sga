package br.com.sgi.controller;


import br.com.sgi.controller.dto.UserRequestDTO;
import br.com.sgi.entity.User;
import br.com.sgi.repository.UserRepository;
import br.com.sgi.service.AbstractTest;
import br.com.sgi.service.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIT extends AbstractTest {

    public static final String ANY_NAME = "anyName";
    private static final String ENCODING = "utf-8";
    public static final String ANY_DESCRIPTION = "anyDescription";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    private static final String BASE_URI = "/users";


    @Test
    void createUserWithValidParametersShouldCreateUser() throws Exception {
        UserRequestDTO userRequestDTO = createUserRequestDTO();

        MockHttpServletResponse response = mvc.perform(post(versionApi + BASE_URI)
                        .characterEncoding(ENCODING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse();

        UserDTO dto = mapper.readValue(response.getContentAsString(), UserDTO.class);

        assertThat(dto.getDescription()).isEqualTo(ANY_DESCRIPTION);
        assertThat(dto.getUsername()).isEqualTo(ANY_DESCRIPTION);
    }

    @Test
    void updateUserWithValidParameterShouldUpdateUser() throws Exception {
        User user = createUserToTest();
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .id(user.getId())
                .username(ANY_DESCRIPTION)
                .creation(LocalDateTime.now())
                .updated(LocalDateTime.now().plusDays(15))
                .username(ANY_NAME)
                .description(ANY_DESCRIPTION)
                .build();

        MockHttpServletResponse response = mvc.perform(put(versionApi + BASE_URI)
                        .characterEncoding(ENCODING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        UserDTO dto = mapper.readValue(response.getContentAsString(), UserDTO.class);

        assertThat(dto.getUsername()).isEqualTo(ANY_NAME);
    }

    @Test
    void getUserByIdWithValidIdShouldReturnDTO() throws Exception {
        User user = createUser();

        MockHttpServletResponse response = mvc.perform(get(versionApi + BASE_URI + "/" + user.getId())
                        .characterEncoding(ENCODING)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse();

        UserDTO dto = mapper.readValue(response.getContentAsString(), UserDTO.class);

        assertThat(dto.getId()).isEqualTo(user.getId());
    }

    private User createUserToTest() {
        User user = createUser();
        user.setId(null);
        user.setDescription(ANY_DESCRIPTION);
        user.setUsername(ANY_NAME);
        user.setCreation(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now().plusDays(15));

        return userRepository.save(user);
    }

}