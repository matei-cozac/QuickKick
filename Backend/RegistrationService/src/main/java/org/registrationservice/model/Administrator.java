package org.registrationservice.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "administrators")
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id",  referencedColumnName = "id")
    private Account account;

    @Column(unique = true)
    private String iban;

    @Column(unique = true)
    private String businessName;

    @Column(unique = true)
    private String cui;

    @Column(unique = true)
    private String address;
}
