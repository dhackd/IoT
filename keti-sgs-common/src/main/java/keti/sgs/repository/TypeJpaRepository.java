package keti.sgs.repository;

import keti.sgs.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeJpaRepository extends JpaRepository<Type, String> {
}
