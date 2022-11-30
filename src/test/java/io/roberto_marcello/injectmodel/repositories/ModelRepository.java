package io.roberto_marcello.injectmodel.repositories;

import io.roberto_marcello.injectmodel.models.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
}
