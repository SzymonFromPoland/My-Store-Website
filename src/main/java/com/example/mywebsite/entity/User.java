package com.example.mywebsite.entity;
import com.example.mywebsite.model.HomeAddress;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String roles;

    private String email;
    private String phone;
    private String firstName;
    private String lastName;

    @Embedded
    private HomeAddress address;

    public User() {}

    public User(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "SecurityUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
}