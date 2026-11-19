package br.edu.ifpr.tcc.clinica.repository;

import br.edu.ifpr.tcc.clinica.domain.Appointment;
import br.edu.ifpr.tcc.clinica.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByScheduledAtAsc();

    List<Appointment> findByPatient_IdOrderByScheduledAtAsc(Long patientId);

    List<Appointment> findByScheduledAtBetweenOrderByScheduledAtAsc(LocalDateTime start, LocalDateTime end);

    @Query("select a from Appointment a where a.professional.id = :professionalId and a.status <> :excludedStatus and a.scheduledAt < :newEnd")
    List<Appointment> findPotentialConflicts(@Param("professionalId") Long professionalId,
            @Param("excludedStatus") AppointmentStatus excludedStatus, @Param("newEnd") LocalDateTime newEnd);
}
