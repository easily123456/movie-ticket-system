package com.movieticket.dto.response.auth;

import com.movieticket.entity.User;
import lombok.Data;

@Data
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private User.Role role;
    private String token;
    private String tokenType= "Bearer";

    public AuthResponse(User user, String token) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.avatar = user.getAvatar();
        this.role = user.getRole();
        this.token = token;
    }
}