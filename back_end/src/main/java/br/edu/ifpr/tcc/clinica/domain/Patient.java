package br.edu.ifpr.tcc.clinica.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import br.edu.ifpr.tcc.clinica.validation.ValidCpf;

@Entity
@Table(name = "pacientes", uniqueConstraints = @UniqueConstraint(name = "uk_paciente_cpf", columnNames = "cpf"))
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;
    @NotBlank @Pattern(regexp = "[0-9+() .-]{8,20}", message = "telefone deve ter entre 8 e 20 caracteres válidos")
    @Column(nullable = false, length = 20)
    private String phone;
    @NotBlank @ValidCpf
    @Column(nullable = false, length = 11)
    private String cpf;
    @Column(nullable = false)
    private Integer age;
    @Size(max = 1000)
    @Column(length = 1000)
    private String allergies;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id")
    private Address address;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Patient() {}
    @PrePersist void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone == null ? null : phone.trim(); }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf == null ? null : cpf.replaceAll("\\D", ""); }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies == null ? null : allergies.trim(); }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
