package co.parameta.tecnical.test.rest.repository;

import co.parameta.tecnical.test.commons.entity.ScriptValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("rest-script")
public interface ScriptValidationRepository extends JpaRepository<ScriptValidationEntity, String> {

    @Query("""
           SELECT sv FROM ScriptValidationEntity sv WHERE sv.state = 1
           """)
    List<ScriptValidationEntity> searchActiveValidationsGroovie();



}
