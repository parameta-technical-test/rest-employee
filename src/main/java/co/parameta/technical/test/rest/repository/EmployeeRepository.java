package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    EmployeeEntity findByTypeDocument_codeAndDocumentNumber(String codeDocument, String numberDocument);

}
