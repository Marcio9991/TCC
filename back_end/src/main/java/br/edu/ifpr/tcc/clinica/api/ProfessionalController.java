package br.edu.ifpr.tcc.clinica.api;

import br.edu.ifpr.tcc.clinica.domain.Professional;
import br.edu.ifpr.tcc.clinica.service.ProfessionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {
    private final ProfessionalService service;
    public ProfessionalController(ProfessionalService service) { this.service = service; }
    @GetMapping public List<Professional> list() { return service.list(); }
    @GetMapping("/{id}") public Professional get(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Professional create(@Valid @RequestBody Requests.ProfessionalRequest request) { return service.create(request); }
    @PutMapping("/{id}") public Professional update(@PathVariable Long id, @Valid @RequestBody Requests.ProfessionalRequest request) { return service.update(id, request); }
}
