package com.hedgerock.manager.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name="t_authority", schema = "user_management")
public class Authority {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name="c_authority")
    private String authority;

}
