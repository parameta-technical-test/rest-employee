package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, String> {

    @Query(value = """
            SELECT p.description FROM PositionEntity p where p.code = :position or p.description = :position
            """)
    String positionDescription(@Param("position") String position);

    @Query(value = """
            SELECT p FROM PositionEntity p where p.code = :position or p.description = :position
            """)
    PositionEntity postionInformation(@Param("position") String position);

}
