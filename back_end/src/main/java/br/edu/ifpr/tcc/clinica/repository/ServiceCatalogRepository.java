package br.edu.ifpr.tcc.clinica.repository;

import br.edu.ifpr.tcc.clinica.domain.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {
    List<ServiceCatalog> findAllByOrderByNameAsc();
}
