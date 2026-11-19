package br.edu.ifpr.tcc.clinica.repository;

import br.edu.ifpr.tcc.clinica.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    List<Patient> findAllByOrderByNameAsc();

    @Query("select p from Patient p where lower(p.name) like lower(concat('%', :q, '%')) or p.cpf like concat('%', :cpf, '%') order by p.name asc")
    List<Patient> search(@Param("q") String query, @Param("cpf") String cpfDigits);
}
