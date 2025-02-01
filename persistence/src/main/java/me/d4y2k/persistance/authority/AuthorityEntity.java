package me.d4y2k.persistance.authority;

import jakarta.persistence.*;

@Entity
@Table(name = "authority", schema = "account")
public class AuthorityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "authority", nullable = false, unique = true)
    private String authority;

}
