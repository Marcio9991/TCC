package br.edu.ifpr.tcc.clinica.service;

import br.edu.ifpr.tcc.clinica.api.Requests.AppointmentRequest;
import br.edu.ifpr.tcc.clinica.domain.*;
import br.edu.ifpr.tcc.clinica.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final ProfessionalRepository professionals;
    private final ServiceCatalogRepository services;

    public AppointmentService(AppointmentRepository appointments, PatientRepository patients,
                              ProfessionalRepository professionals, ServiceCatalogRepository services) {
        this.appointments = appointments; this.patients = patients; this.professionals = professionals; this.services = services;
    }

    @Transactional(readOnly = true)
    public List<Appointment> list(Long patientId, LocalDate date) {
        List<Appointment> result;
        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            result = appointments.findByScheduledAtBetweenOrderByScheduledAtAsc(start, start.plusDays(1).minusNanos(1));
        } else if (patientId != null) {
            result = appointments.findByPatient_IdOrderByScheduledAtAsc(patientId);
        } else {
            result = appointments.findAllByOrderByScheduledAtAsc();
        }
        if (date != null && patientId != null) result = result.stream().filter(a -> a.getPatient().getId().equals(patientId)).toList();
        return result;
    }

    @Transactional(readOnly = true)
    public Appointment get(Long id) { return appointments.findById(id).orElseThrow(() -> notFound()); }

    public Appointment create(AppointmentRequest request) {
        Appointment a = new Appointment(); apply(a, request); a.setStatus(AppointmentStatus.SCHEDULED);
        ensureNoConflict(a, null);
        return appointments.save(a);
    }

    public Appointment update(Long id, AppointmentRequest request) {
        Appointment a = get(id); apply(a, request);
        if (a.getStatus() == AppointmentStatus.SCHEDULED) ensureNoConflict(a, id);
        return appointments.save(a);
    }

    public Appointment updateStatus(Long id, AppointmentStatus status) {
        Appointment a = get(id);
        if (status == AppointmentStatus.SCHEDULED) ensureNoConflict(a, id);
        a.setStatus(status);
        return appointments.save(a);
    }

    public boolean isAvailable(Long professionalId, Long serviceId, LocalDateTime start, Long ignoreAppointmentId) {
        Professional professional = professionals.findById(professionalId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        ServiceCatalog service = services.findById(serviceId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
        if (!service.isActive() || professional.getServices().stream().noneMatch(s -> s.getId().equals(serviceId))) return false;
        LocalDateTime end = start.plusMinutes(service.getDurationMinutes());
        return appointments.findPotentialConflicts(professionalId, AppointmentStatus.CANCELLED, end).stream()
            .filter(a -> ignoreAppointmentId == null || !a.getId().equals(ignoreAppointmentId))
            .noneMatch(a -> a.getScheduledAt().plusMinutes(a.getService().getDurationMinutes()).isAfter(start));
    }

    private void apply(Appointment a, AppointmentRequest r) {
        Patient patient = patients.findById(r.patientId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
        Professional professional = professionals.findByIdForUpdate(r.professionalId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        ServiceCatalog service = services.findById(r.serviceId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
        if (!service.isActive()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O serviço está inativo");
        if (professional.getServices().stream().noneMatch(s -> s.getId().equals(service.getId())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profissional não está habilitado para este serviço");
        a.setPatient(patient); a.setProfessional(professional); a.setService(service); a.setScheduledAt(r.scheduledAt()); a.setNotes(r.notes());
    }

    private void ensureNoConflict(Appointment a, Long ignoreId) {
        if (a.getStatus() == AppointmentStatus.CANCELLED) return;
        if (!isAvailable(a.getProfessional().getId(), a.getService().getId(), a.getScheduledAt(), ignoreId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profissional indisponível: o horário conflita com outro agendamento ou não está habilitado para o serviço");
    }
    private ResponseStatusException notFound() { return new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"); }
}
