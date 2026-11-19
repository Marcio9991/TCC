package br.edu.ifpr.tcc.clinica.service;

import br.edu.ifpr.tcc.clinica.api.Requests.PatientRequest;
import br.edu.ifpr.tcc.clinica.domain.Patient;
import br.edu.ifpr.tcc.clinica.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patients;
    public PatientService(PatientRepository patients) { this.patients = patients; }

    @Transactional(readOnly = true)
    public List<Patient> list(String q) { return q == null || q.isBlank() ? patients.findAllByOrderByNameAsc() : patients.search(q.trim(), digits(q)); }

    @Transactional(readOnly = true)
    public Patient get(Long id) { return patients.findById(id).orElseThrow(() -> notFound("Paciente", id)); }

    public Patient create(PatientRequest request) {
        String cpf = digits(request.cpf());
        if (patients.existsByCpf(cpf)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe paciente cadastrado com este CPF");
        Patient patient = new Patient();
        apply(patient, request);
        return patients.save(patient);
    }

    public Patient update(Long id, PatientRequest request) {
        Patient patient = get(id);
        String cpf = digits(request.cpf());
        if (patients.existsByCpfAndIdNot(cpf, id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe paciente cadastrado com este CPF");
        apply(patient, request);
        return patients.save(patient);
    }

    private void apply(Patient p, PatientRequest r) {
        p.setName(r.name()); p.setPhone(r.phone()); p.setCpf(r.cpf()); p.setAge(r.age()); p.setAllergies(r.allergies());
        p.setAddress(r.address() == null || r.address().isBlank() ? null : new br.edu.ifpr.tcc.clinica.domain.Address(r.address()));
    }
    private String digits(String value) { return value == null ? "" : value.replaceAll("\\D", ""); }
    private ResponseStatusException notFound(String type, Long id) { return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " " + id + " não encontrado"); }
}
