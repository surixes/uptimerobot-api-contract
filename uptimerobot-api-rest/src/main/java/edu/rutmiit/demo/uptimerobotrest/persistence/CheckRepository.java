package edu.rutmiit.demo.uptimerobotrest.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckRepository extends JpaRepository<CheckEntity, Long> {

    Optional<CheckEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<CheckEntity> findAllByEnabledTrueOrderByIdAsc();
}
