package com._blog.backend.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// @Entity
// @Table(name = "users")
public class User {

    // @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    private String username;
    
    private String password;
    
    private String email;

}
