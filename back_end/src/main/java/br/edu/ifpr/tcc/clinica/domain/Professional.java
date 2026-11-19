package br.edu.ifpr.tcc.clinica.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import br.edu.ifpr.tcc.clinica.validation.ValidCpf;

@Entity
@Table(name = "profissionais", uniqueConstraints = @UniqueConstraint(name = "uk_profissional_cpf", columnNames = "cpf"))
public class Professional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;
    @NotBlank
    @Pattern(regexp = "[0-9+() .-]{8,20}")
    @Column(nullable = false, length = 20)
    private String phone;
    @NotBlank
    @ValidCpf
    @Column(nullable = false, length = 11)
    private String cpf;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id")
    private Address address;
    @ManyToMany
    @JoinTable(name = "profissional_servicos", joinColumns = @JoinColumn(name = "profissional_id"), inverseJoinColumns = @JoinColumn(name = "servico_id"))
    private Set<ServiceCatalog> services = new LinkedHashSet<>();

    public Professional() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<ServiceCatalog> getServices() {
        return services;
    }

    public void setServices(Set<ServiceCatalog> services) {
        this.services = services == null ? new LinkedHashSet<>() : new LinkedHashSet<>(services);
    }
}
