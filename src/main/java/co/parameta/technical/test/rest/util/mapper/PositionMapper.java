package co.parameta.technical.test.rest.util.mapper;

import co.parameta.technical.test.commons.dto.PositionDTO;
import co.parameta.technical.test.commons.entity.PositionEntity;
import co.parameta.technical.test.commons.util.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PositionMapper extends BaseMapper<PositionEntity, PositionDTO> {
}
