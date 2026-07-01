package mate.academy.springbootweb.service.category;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootweb.dto.category.CategoryDto;
import mate.academy.springbootweb.dto.category.CreateCategoryRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.mapper.CategoryMapper;
import mate.academy.springbootweb.mapper.page.PageMapper;
import mate.academy.springbootweb.model.Category;
import mate.academy.springbootweb.repository.category.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final String ENTITY_NOT_FOUND_MESSAGE = "Can't find category by id: ";
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PageMapper<CategoryDto> pageMapper;

    @Override
    public PageDto<CategoryDto> findAll(Pageable pageable) {
        Page<CategoryDto> page = categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);

        return pageMapper.toDto(page);
    }

    @Override
    public CategoryDto findById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + id)));
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto requestDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toModel(requestDto)));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + id));

        categoryMapper.updateCategoryFromDto(category, requestDto);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
