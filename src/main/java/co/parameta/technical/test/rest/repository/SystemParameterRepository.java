package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.SystemParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("rest-system")
public interface SystemParameterRepository extends JpaRepository<SystemParameterEntity, Integer> {

    @Query(value = """
            SELECT sp FROM SystemParameterEntity sp where sp.name in :listNames
            """)
    List<SystemParameterEntity> searchAllParameters(@Param("listNames") List<String> names);

}
