package br.edu.ifpr.tcc.clinica.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "enderecos")
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 250)
    @Column(name = "localizacao", length = 250)
    private String location;

    protected Address() {}
    public Address(String location) { this.location = location == null ? null : location.trim(); }
    public Long getId() { return id; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location == null ? null : location.trim(); }
}
