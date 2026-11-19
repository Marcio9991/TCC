package br.edu.ifpr.tcc.clinica.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "servicos")
public class ServiceCatalog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;
    @NotBlank @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String type;
    @Min(5) @Column(nullable = false)
    private Integer durationMinutes;
    @Column(nullable = false)
    private boolean active = true;

    public ServiceCatalog() {}
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }
    public String getType() { return type; }
    public void setType(String type) { this.type = type == null ? null : type.trim(); }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
