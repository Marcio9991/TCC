package br.edu.ifpr.tcc.clinica.api;

import br.edu.ifpr.tcc.clinica.domain.Appointment;
import br.edu.ifpr.tcc.clinica.service.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@Validated
public class AppointmentController {
    private final AppointmentService service;
    public AppointmentController(AppointmentService service) { this.service = service; }
    @GetMapping public List<Appointment> list(@RequestParam(required = false) @Positive Long patientId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) { return service.list(patientId, date); }
    @GetMapping("/{id}") public Appointment get(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Appointment create(@Valid @RequestBody Requests.AppointmentRequest request) { return service.create(request); }
    @PutMapping("/{id}") public Appointment update(@PathVariable Long id, @Valid @RequestBody Requests.AppointmentRequest request) { return service.update(id, request); }
    @PatchMapping("/{id}/status") public Appointment status(@PathVariable Long id, @Valid @RequestBody Requests.StatusRequest request) { return service.updateStatus(id, request.status()); }

    @GetMapping("/availability")
    public Map<String, Boolean> availability(@RequestParam @Positive Long professionalId,
        @RequestParam @Positive Long serviceId,
        @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startsAt) {
        return Map.of("available", service.isAvailable(professionalId, serviceId, startsAt, null));
    }
}
