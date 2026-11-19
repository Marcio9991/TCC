package br.edu.ifpr.tcc.clinica.api;

import br.edu.ifpr.tcc.clinica.domain.ServiceCatalog;
import br.edu.ifpr.tcc.clinica.service.ServiceCatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceCatalogController {
    private final ServiceCatalogService service;

    public ServiceCatalogController(ServiceCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceCatalog> list(@RequestParam(defaultValue = "false") boolean activeOnly) {
        return service.list(activeOnly);
    }

    @GetMapping("/{id}")
    public ServiceCatalog get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceCatalog create(@Valid @RequestBody Requests.ServiceRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ServiceCatalog update(@PathVariable Long id, @Valid @RequestBody Requests.ServiceRequest request) {
        return service.update(id, request);
    }
}
