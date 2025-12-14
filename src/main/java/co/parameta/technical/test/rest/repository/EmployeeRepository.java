package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link EmployeeEntity} persistence.
 * <p>
 * Provides CRUD operations and custom queries to retrieve employee information
 * by different search criteria, as well as to update report storage information.
 * </p>
 */
@Repository("rest-employee")
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    /**
     * Retrieves full employee information using flexible search criteria.
     * <p>
     * The search can be performed using:
     * <ul>
     *     <li>{@code idEmployee}</li>
     *     <li>{@code documentNumber} and {@code typeDocument}</li>
     * </ul>
     * If all parameters are {@code null}, the query returns {@code null}.
     * </p>
     *
     * @param idEmployee     the employee identifier (optional)
     * @param numberDocument the employee document number (optional)
     * @param typeDocument   the document type code or description (optional)
     * @return the matching {@link EmployeeEntity}, or {@code null} if no match is found
     */
    @Query("""
        SELECT e
        FROM EmployeeEntity e
        WHERE
          (:idEmployee IS NULL OR e.id = :idEmployee)
        AND
          (
            (:numberDocument IS NULL AND :typeDocument IS NULL)
            OR
            (
              :numberDocument IS NOT NULL
              AND :typeDocument IS NOT NULL
              AND e.documentNumber = :numberDocument
              AND (
                e.typeDocument.code = :typeDocument
                OR e.typeDocument.description = :typeDocument
              )
            )
          )
        """)
    EmployeeEntity searchAllInformationEmployee(
            @Param("idEmployee") Integer idEmployee,
            @Param("numberDocument") String numberDocument,
            @Param("typeDocument") String typeDocument
    );

    /**
     * Updates the storage location of the employee PDF report.
     * <p>
     * This method stores the S3 path or file reference where the employee
     * report PDF is located.
     * </p>
     *
     * @param report     the storage location (e.g. S3 key or path)
     * @param idEmployee the employee identifier
     */
    @Modifying
    @Query(value = """
            UPDATE technical_test.employee
            SET storage_location_report = :report
            WHERE id = :idEmployee
            """, nativeQuery = true)
    void saveReportEmployee(
            @Param("report") String report,
            @Param("idEmployee") Integer idEmployee
    );

    /**
     * Retrieves the employee identifier using document number and document type.
     *
     * @param documentNumber the employee document number
     * @param typeDocument   the document type code
     * @return the employee identifier, or {@code null} if no match is found
     */
    @Query("""
            SELECT e.id
            FROM EmployeeEntity e
            WHERE e.documentNumber = :documentNumber
              AND e.typeDocument.code = :typeDocument
            """)
    Integer searchIdEmployee(
            @Param("documentNumber") String documentNumber,
            @Param("typeDocument") String typeDocument
    );

}
