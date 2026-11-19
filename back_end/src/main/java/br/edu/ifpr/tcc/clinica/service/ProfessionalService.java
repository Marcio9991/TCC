package br.edu.ifpr.tcc.clinica.service;

import br.edu.ifpr.tcc.clinica.api.Requests.ProfessionalRequest;
import br.edu.ifpr.tcc.clinica.domain.Professional;
import br.edu.ifpr.tcc.clinica.domain.ServiceCatalog;
import br.edu.ifpr.tcc.clinica.repository.ProfessionalRepository;
import br.edu.ifpr.tcc.clinica.repository.ServiceCatalogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@Transactional
public class ProfessionalService {
    private final ProfessionalRepository professionals;
    private final ServiceCatalogRepository services;

    public ProfessionalService(ProfessionalRepository professionals, ServiceCatalogRepository services) {
        this.professionals = professionals;
        this.services = services;
    }

    @Transactional(readOnly = true)
    public List<Professional> list() {
        return professionals.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Professional get(Long id) {
        return professionals.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
    }

    public Professional create(ProfessionalRequest request) {
        String cpf = digits(request.cpf());
        if (professionals.existsByCpf(cpf))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe profissional cadastrado com este CPF");
        Professional p = new Professional();
        apply(p, request);
        return professionals.save(p);
    }

    public Professional update(Long id, ProfessionalRequest request) {
        Professional p = get(id);
        String cpf = digits(request.cpf());
        if (professionals.existsByCpfAndIdNot(cpf, id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe profissional cadastrado com este CPF");
        apply(p, request);
        return professionals.save(p);
    }

    private void apply(Professional p, ProfessionalRequest r) {
        p.setName(r.name());
        p.setPhone(r.phone());
        p.setCpf(r.cpf());
        p.setAddress(r.address() == null || r.address().isBlank() ? null
                : new br.edu.ifpr.tcc.clinica.domain.Address(r.address()));
        var ids = r.serviceIds() == null ? java.util.Set.<Long>of() : r.serviceIds();
        var found = services.findAllById(ids);
        if (found.size() != ids.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais serviços informados não existem");
        if (found.stream().anyMatch(s -> !s.isActive()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível habilitar um profissional em serviço inativo");
        p.setServices(new LinkedHashSet<>(found));
    }

    private String digits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }
}
