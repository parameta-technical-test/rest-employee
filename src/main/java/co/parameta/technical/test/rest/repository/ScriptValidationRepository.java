package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.ScriptValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link ScriptValidationEntity} persistence.
 * <p>
 * Provides access to validation scripts stored in the database, including
 * retrieval of active Groovy validation rules.
 * </p>
 */
@Repository("rest-script")
public interface ScriptValidationRepository extends JpaRepository<ScriptValidationEntity, String> {

    /**
     * Retrieves all active Groovy validation scripts.
     * <p>
     * Only scripts with {@code state = 1} are considered active and will be
     * executed during the employee validation process.
     * </p>
     *
     * @return a list of active {@link ScriptValidationEntity} instances;
     *         an empty list if no active scripts are found
     */
    @Query("""
           SELECT sv
           FROM ScriptValidationEntity sv
           WHERE sv.state = 1
           """)
    List<ScriptValidationEntity> searchActiveValidationsGroovie();

}
