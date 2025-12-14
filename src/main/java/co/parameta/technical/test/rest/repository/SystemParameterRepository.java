package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.SystemParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link SystemParameterEntity} persistence.
 * <p>
 * Provides access to system configuration parameters stored in the database,
 * which are commonly used to control application behavior at runtime
 * (e.g. email configuration, feature toggles, PDF generation flags).
 * </p>
 */
@Repository("rest-system")
public interface SystemParameterRepository extends JpaRepository<SystemParameterEntity, Integer> {

    /**
     * Retrieves all system parameters whose names match the given list.
     *
     * @param names list of parameter names to search for
     * @return a list of {@link SystemParameterEntity} matching the provided names;
     *         an empty list if no parameters are found
     */
    @Query("""
            SELECT sp
            FROM SystemParameterEntity sp
            WHERE sp.name IN :listNames
            """)
    List<SystemParameterEntity> searchAllParameters(@Param("listNames") List<String> names);

    /**
     * Retrieves a system parameter by its unique name.
     *
     * @param name the unique name of the system parameter
     * @return the {@link SystemParameterEntity} associated with the given name,
     *         or {@code null} if no parameter is found
     */
    SystemParameterEntity findByName(String name);

}
