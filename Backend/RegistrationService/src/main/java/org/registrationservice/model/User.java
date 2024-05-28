package org.registrationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id",  referencedColumnName = "id")
    private Account account;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}
