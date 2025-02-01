package me.d4y2k.persistance.account;

import jakarta.persistence.*;
import me.d4y2k.persistance.authority.AuthorityEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "account", schema = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;

    @Column(name = "is_account_non_expired", nullable = false)
    private boolean isAccountNonLocked;

    @JoinTable(
            name = "account_authority",
            schema = "account",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    @OneToMany(fetch = FetchType.EAGER)
    private Set<AuthorityEntity> authorities;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

}
