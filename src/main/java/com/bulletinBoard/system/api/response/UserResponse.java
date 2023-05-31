package com.bulletinBoard.system.api.response;

import com.bulletinBoard.system.bl.dto.UserDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserResponse {

    /**
     * <h2>id</h2>
     * <p>
     * User's ID
     * </p>
     */
    private int id;

    /**
     * <h2>name</h2>
     * <p>
     * User's Name
     * </p>
     */
    private String name;

    /**
     * <h2>email</h2>
     * <p>
     * User's Email
     * </p>
     */
    private String email;

    /**
     * <h2>address</h2>
     * <p>
     * User's Address
     * </p>
     */
    private String address;

    /**
     * <h2>password</h2>
     * <p>
     * User's Password
     * </p>
     */
    @JsonIgnore
    private String password;

    /**
     * <h2>role</h2>
     * <p>
     * User's Role
     * </p>
     */
    private int role;

    /**
     * <h2>profilePhoto</h2>
     * <p>
     * profilePhoto
     * </p>
     */
    private String profilePhoto;

    /**
     * <h2>Constructor for UserResponse</h2>
     * <p>
     * Constructor for UserResponse
     * </p>
     * 
     * @param user UserDTO
     */
    public UserResponse(UserDTO user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.password = user.getPassword();
        this.profilePhoto = user.getProfilePhotoName();
        this.role = user.getAuthority().getId();
    }
}