package com.serpentia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private Integer gamesPlayed = 0;
    private Integer gamesWon = 0;
    private Integer totalPoints = 0;
    private Integer bigPoints = 0;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}


