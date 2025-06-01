package com.hedgerock.manager.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@Entity
//@Table(name="t_user", schema = "user_management")
public class ApplicationUser {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name="c_username")
    private String username;

//    @Column(name="c_password")
    private String password;

//    @ManyToMany
//    @JoinTable(
//            schema = "user_management",
//            name = "t_user_authority",
//            joinColumns = @JoinColumn(name="id_user"),
//            inverseJoinColumns = @JoinColumn(name="id_authority")
//    )
    private List<Authority> authorities;
}
