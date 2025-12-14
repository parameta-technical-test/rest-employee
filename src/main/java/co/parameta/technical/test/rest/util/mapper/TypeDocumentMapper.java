package co.parameta.technical.test.rest.util.mapper;

import co.parameta.technical.test.commons.dto.TypeDocumentDTO;
import co.parameta.technical.test.commons.entity.TypeDocumentEntity;
import co.parameta.technical.test.commons.util.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TypeDocumentMapper extends BaseMapper<TypeDocumentEntity, TypeDocumentDTO> {
}
