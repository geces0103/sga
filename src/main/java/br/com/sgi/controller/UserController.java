package br.com.sgi.controller;


import br.com.sgi.controller.dto.UserRequestDTO;
import br.com.sgi.service.UserService;
import br.com.sgi.service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "v1/users", produces = APPLICATION_JSON_VALUE)
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

    @GetMapping("/username")
    public ResponseEntity<List<UserDTO>> getProductByUsername(@RequestParam(value="username") String name) {
        return ResponseEntity.ok(this.userService.findByName(name));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable(required = true) Long id) {
        this.userService.deleteById(id);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(this.userService.findAll());
    }

    @GetMapping("/ordered")
    public ResponseEntity<Set<UserDTO>> getAllOrdered() {
        return ResponseEntity.ok(this.userService.findAllOrdered());
    }
    public URI getLocation(String path, Long id) {
        return URI.create(String.format("v1".concat(path).concat("/%s"), id));
    }

}
