package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.TypeDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeDocumentRepository extends JpaRepository<TypeDocumentEntity, String> {

    @Query(value = """
            SELECT td.description FROM TypeDocumentEntity td where td.code = :typeDocument or td.description = :typeDocument
            """)
    String documentDescription(@Param("typeDocument") String typeDocument);

    @Query(value = """
            SELECT td FROM TypeDocumentEntity td where td.code = :typeDocument or td.description = :typeDocument
            """)
    TypeDocumentEntity documentInformation(@Param("typeDocument") String typeDocument);

}
