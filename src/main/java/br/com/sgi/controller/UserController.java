package br.com.sgi.controller;


import br.com.sgi.service.UserService;
import br.com.sgi.controller.dto.UserRequestDTO;
import br.com.sgi.service.UserService;
import br.com.sgi.service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "${api.version}/users", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserDTO response = this.userService.create(dto);
        return ResponseEntity.created(getLocation("/users", response.getId())).body(response);
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser (@Valid @RequestBody UserRequestDTO dto){
        return ResponseEntity.ok(this.userService.update(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(this.userService.findById(id));
    }

    public URI getLocation(String path, Long id) {
        return URI.create(String.format("v1".concat(path).concat("/%s"), id));
    }

}
