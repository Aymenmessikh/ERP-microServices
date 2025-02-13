package com.example.adminservice.Controller;

import com.example.adminservice.Dto.User.UserRequest;
import com.example.adminservice.Dto.User.UserResponse;
import com.example.adminservice.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/user")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('CREATE_USER')")
    @PostMapping("{id}")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest, @PathVariable Long id){
        UserResponse userResponse = userService.createUser(userRequest, id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ_ALL_USERS')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> userResponses = userService.getAllUsers();
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        UserResponse userResponse = userService.getUserById(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping("byEmail/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        UserResponse userResponse = userService.getUserByEmail(email);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @PutMapping("{id}")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserRequest userRequest, @PathVariable Long id){
        UserResponse userResponse = userService.updateUser(userRequest, id);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('DELETE_USER')")
    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }

    @PreAuthorize("hasAuthority('MANAGE_USER_STATUS')")
    @PutMapping("enableDisable/{id}/{enable}")
    public void enableDisableUser(@PathVariable Long id, @PathVariable Boolean enable) {
        userService.enableDisabeleUser(id, enable);
    }
}
