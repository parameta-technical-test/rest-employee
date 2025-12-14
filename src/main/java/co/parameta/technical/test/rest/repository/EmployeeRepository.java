package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("rest-employee")
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    EmployeeEntity findByTypeDocument_codeAndDocumentNumber(String codeDocument, String numberDocument);

    @Query(value = """
            UPDATE technical_test.employee SET storage_location_report = :report where id = :idEmployee
            """, nativeQuery = true)
    @Modifying
    void saveReportEmployee(@Param("report") String report, @Param("idEmployee") Integer idEmployee);

    @Query(value = """
            SELECT e.id FROM EmployeeEntity e where e.documentNumber = :documentNumber and e.typeDocument.code = :typeDocument
            """)
    Integer searchIdEmployee(@Param("documentNumber") String documentNumber, @Param("typeDocument") String typeDocument);

}
