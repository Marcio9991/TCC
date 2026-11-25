package br.edu.ifpr.tcc.clinica.web.form;

import br.edu.ifpr.tcc.clinica.validation.ValidCpf;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ClinicForms {
    private ClinicForms() {}

    public static class Patient {
        @NotBlank(message = "Informe o nome do paciente.")
        @Size(max = 120, message = "O nome deve ter até 120 caracteres.")
        private String name;

        @NotBlank(message = "Informe o telefone do paciente.")
        @Pattern(regexp = "[0-9+() .-]{8,20}", message = "Informe um telefone válido.")
        private String phone;

        @NotBlank(message = "Informe o CPF do paciente.")
        @ValidCpf
        private String cpf;

        @NotNull(message = "Informe a idade do paciente.")
        @Min(value = 0, message = "A idade não pode ser negativa.")
        @Max(value = 120, message = "Informe uma idade de até 120 anos.")
        private Integer age;

        @Size(max = 1000, message = "As observações devem ter até 1000 caracteres.")
        private String allergies;

        @Size(max = 250, message = "O endereço deve ter até 250 caracteres.")
        private String address;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getAllergies() { return allergies; }
        public void setAllergies(String allergies) { this.allergies = allergies; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public static class Professional {
        @NotBlank(message = "Informe o nome do profissional.")
        @Size(max = 120, message = "O nome deve ter até 120 caracteres.")
        private String name;

        @NotBlank(message = "Informe o telefone do profissional.")
        @Pattern(regexp = "[0-9+() .-]{8,20}", message = "Informe um telefone válido.")
        private String phone;

        @NotBlank(message = "Informe o CPF do profissional.")
        @ValidCpf
        private String cpf;

        @Size(max = 250, message = "O endereço deve ter até 250 caracteres.")
        private String address;

        private Set<@Positive(message = "Selecione um serviço válido.") Long> serviceIds = new LinkedHashSet<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public Set<Long> getServiceIds() { return serviceIds; }
        public void setServiceIds(Set<Long> serviceIds) { this.serviceIds = serviceIds == null ? new LinkedHashSet<>() : serviceIds; }
    }

    public static class Service {
        @NotBlank(message = "Informe o nome do serviço.")
        @Size(max = 100, message = "O nome do serviço deve ter até 100 caracteres.")
        private String name;

        @NotBlank(message = "Informe o tipo do serviço.")
        @Size(max = 60, message = "O tipo deve ter até 60 caracteres.")
        private String type;

        @NotNull(message = "Informe a duração do serviço.")
        @Min(value = 5, message = "A duração mínima é de 5 minutos.")
        @Max(value = 1440, message = "A duração máxima é de 1440 minutos.")
        private Integer durationMinutes;

        private boolean active = true;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    public static class Appointment {
        @NotNull(message = "Selecione o paciente.")
        @Positive(message = "Selecione um paciente válido.")
        private Long patientId;

        @NotNull(message = "Selecione o profissional.")
        @Positive(message = "Selecione um profissional válido.")
        private Long professionalId;

        @NotNull(message = "Selecione o serviço.")
        @Positive(message = "Selecione um serviço válido.")
        private Long serviceId;

        @NotNull(message = "Informe a data e o horário do agendamento.")
        private LocalDateTime scheduledAt;

        @Size(max = 1000, message = "As observações devem ter até 1000 caracteres.")
        private String notes;

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public Long getProfessionalId() { return professionalId; }
        public void setProfessionalId(Long professionalId) { this.professionalId = professionalId; }
        public Long getServiceId() { return serviceId; }
        public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
        public LocalDateTime getScheduledAt() { return scheduledAt; }
        public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
