package io.github.robertomike.inject_model.repositories;

import io.github.robertomike.inject_model.models.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
}
