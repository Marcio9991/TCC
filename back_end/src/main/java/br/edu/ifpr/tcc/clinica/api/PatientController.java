package br.edu.ifpr.tcc.clinica.api;

import br.edu.ifpr.tcc.clinica.domain.Patient;
import br.edu.ifpr.tcc.clinica.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService service;
    public PatientController(PatientService service) { this.service = service; }
    @GetMapping public List<Patient> list(@RequestParam(required = false) String q) { return service.list(q); }
    @GetMapping("/{id}") public Patient get(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Patient create(@Valid @RequestBody Requests.PatientRequest request) { return service.create(request); }
    @PutMapping("/{id}") public Patient update(@PathVariable Long id, @Valid @RequestBody Requests.PatientRequest request) { return service.update(id, request); }
}
