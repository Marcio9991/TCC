package br.edu.ifpr.tcc.clinica.api;

import br.edu.ifpr.tcc.clinica.domain.AppointmentStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;
import br.edu.ifpr.tcc.clinica.validation.ValidCpf;

public final class Requests {
    private Requests() {}

    public record PatientRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Pattern(regexp = "[0-9+() .-]{8,20}", message = "telefone inválido") String phone,
        @NotBlank @ValidCpf String cpf,
        @NotNull @Min(0) @Max(120) Integer age,
        @Size(max = 1000) String allergies,
        @Size(max = 250) String address
    ) {}

    public record ProfessionalRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Pattern(regexp = "[0-9+() .-]{8,20}", message = "telefone inválido") String phone,
        @NotBlank @ValidCpf String cpf,
        @Size(max = 250) String address,
        Set<@Positive Long> serviceIds
    ) {}

    public record ServiceRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 60) String type,
        @NotNull @Min(5) @Max(1440) Integer durationMinutes,
        Boolean active
    ) {}

    public record AppointmentRequest(
        @NotNull @Positive Long patientId,
        @NotNull @Positive Long professionalId,
        @NotNull @Positive Long serviceId,
        @NotNull LocalDateTime scheduledAt,
        @Size(max = 1000) String notes
    ) {}

    public record StatusRequest(@NotNull AppointmentStatus status) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
}
