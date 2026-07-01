package mate.academy.springbootweb.mapper;

import mate.academy.springbootweb.config.MapperConfig;
import mate.academy.springbootweb.dto.category.CategoryDto;
import mate.academy.springbootweb.dto.category.CreateCategoryRequestDto;
import mate.academy.springbootweb.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CreateCategoryRequestDto requestDto);

    void updateCategoryFromDto(@MappingTarget Category category, CreateCategoryRequestDto dto);
}
