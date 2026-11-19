package br.edu.ifpr.tcc.clinica.service;

import br.edu.ifpr.tcc.clinica.api.Requests.ServiceRequest;
import br.edu.ifpr.tcc.clinica.domain.ServiceCatalog;
import br.edu.ifpr.tcc.clinica.repository.ServiceCatalogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class ServiceCatalogService {
    private final ServiceCatalogRepository services;
    public ServiceCatalogService(ServiceCatalogRepository services) { this.services = services; }
    @Transactional(readOnly = true) public List<ServiceCatalog> list(boolean activeOnly) { return services.findAllByOrderByNameAsc().stream().filter(s -> !activeOnly || s.isActive()).toList(); }
    @Transactional(readOnly = true) public ServiceCatalog get(Long id) { return services.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado")); }
    public ServiceCatalog create(ServiceRequest r) { ServiceCatalog s = new ServiceCatalog(); apply(s, r); return services.save(s); }
    public ServiceCatalog update(Long id, ServiceRequest r) { ServiceCatalog s = get(id); apply(s, r); return services.save(s); }
    private void apply(ServiceCatalog s, ServiceRequest r) { s.setName(r.name()); s.setType(r.type()); s.setDurationMinutes(r.durationMinutes()); if (r.active() != null) s.setActive(r.active()); }
}
