package mate.academy.springbootweb.service.category;

import mate.academy.springbootweb.dto.category.CategoryDto;
import mate.academy.springbootweb.dto.category.CreateCategoryRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    PageDto<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto save(CreateCategoryRequestDto requestDto);

    CategoryDto update(Long id, CreateCategoryRequestDto requestDto);

    void deleteById(Long id);
}
