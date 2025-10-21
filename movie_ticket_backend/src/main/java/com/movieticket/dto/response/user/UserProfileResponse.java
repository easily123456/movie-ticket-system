package com.movieticket.dto.response.user;

import com.movieticket.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private User.Role role;
    private Boolean status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.avatar = user.getAvatar();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.lastLoginTime = user.getLastLoginTime();
        this.createTime = user.getCreateTime();
    }
}