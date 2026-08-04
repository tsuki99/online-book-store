package mate.academy.springbootweb.service;

import java.util.List;
import java.util.Optional;
import mate.academy.springbootweb.dto.category.CategoryDto;
import mate.academy.springbootweb.dto.category.CreateCategoryRequestDto;
import mate.academy.springbootweb.dto.page.PageDto;
import mate.academy.springbootweb.exception.EntityNotFoundException;
import mate.academy.springbootweb.mapper.CategoryMapper;
import mate.academy.springbootweb.mapper.page.PageMapper;
import mate.academy.springbootweb.model.Category;
import mate.academy.springbootweb.repository.category.CategoryRepository;
import mate.academy.springbootweb.service.category.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private static final PageRequest DEFAULT_PAGEABLE = PageRequest.of(0, 6);
    private static final Long ID_EXAMPLE = 1L;
    private static final Long NON_EXISTING_ID_EXAMPLE = 999L;
    private static final String EXPECTED_EXCEPTION_MESSAGE = "Can't find category by id: ";
    private static final String CATEGORY_NAME_FIRST_EXAMPLE = "Fantasy";
    private static final String CATEGORY_NAME_SECOND_EXAMPLE = "Romance";
    private static final String CATEGORY_DESC_FIRST_EXAMPLE = "Fantasy worlds, magic and adventures";
    private static final String CATEGORY_DESC_SECOND_EXAMPLE = "Romantic stories and love novels";

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private PageMapper<CategoryDto> pageMapper;
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(
                categoryRepository,
                categoryMapper,
                pageMapper
        );
    }

    @Test
    @DisplayName("""
            Find all categories returns page DTO
            """)
    void findAll_WithValidPageable_ReturnsPageDto() {
        Category category = createCategory();
        CategoryDto categoryDto = createCategoryDto(category);
        Page<Category> categoryPage = new PageImpl<>(List.of(category));
        Page<CategoryDto> categoryDtoPage = new PageImpl<>(List.of(categoryDto));
        PageDto<CategoryDto> expectedPageDto = createPageDto(categoryDtoPage);

        when(categoryRepository.findAll(DEFAULT_PAGEABLE)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        when(pageMapper.toDto(categoryDtoPage)).thenReturn(expectedPageDto);

        PageDto<CategoryDto> actualPageDto = categoryService.findAll(DEFAULT_PAGEABLE);

        assertEquals(expectedPageDto, actualPageDto);

        verify(categoryRepository).findAll(DEFAULT_PAGEABLE);
        verify(categoryMapper).toDto(category);
        verify(pageMapper).toDto(categoryDtoPage);

        verifyNoMoreInteractions(
                categoryRepository,
                categoryMapper,
                pageMapper
        );
    }

    @Test
    @DisplayName("""
            Find category by existing id returns category DTO
            """)
    void findById_WithExistingId_ReturnsCategoryDto() {
        Category category = createCategory();
        CategoryDto expectedCategoryDto = createCategoryDto(category);

        when(categoryRepository.findById(ID_EXAMPLE)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        CategoryDto actualCategoryDto = categoryService.findById(ID_EXAMPLE);

        assertEquals(expectedCategoryDto, actualCategoryDto);

        verify(categoryRepository).findById(ID_EXAMPLE);
        verify(categoryMapper).toDto(category);

        verifyNoMoreInteractions(
                categoryRepository,
                categoryMapper,
                pageMapper
        );
    }

    @Test
    @DisplayName("""
            Find category by non-existing id throws exception
            """)
    void findById_WithNonExistingId_ThrowsException() {
        when(categoryRepository.findById(NON_EXISTING_ID_EXAMPLE)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.findById(NON_EXISTING_ID_EXAMPLE)
        );

        assertEquals(
                EXPECTED_EXCEPTION_MESSAGE + NON_EXISTING_ID_EXAMPLE,
                exception.getMessage()
        );

        verify(categoryRepository).findById(NON_EXISTING_ID_EXAMPLE);
        verify(categoryMapper, never()).toDto(any(Category.class));

        verifyNoMoreInteractions(
                categoryRepository,
                categoryMapper,
                pageMapper
        );
    }

    @Test
    @DisplayName("""
            Save valid category request returns category DTO
            """)
    void save_WithValidCategoryRequestDto_ReturnsCategoryDto() {
        Category category = createCategory();
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto(category);
        CategoryDto expectedCategoryDto = createCategoryDto(category);

        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        CategoryDto actualCategoryDto = categoryService.save(categoryRequestDto);

        assertEquals(expectedCategoryDto, actualCategoryDto);

        verify(categoryMapper).toModel(categoryRequestDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);

        verifyNoMoreInteractions(
                categoryRepository,
                categoryMapper,
                pageMapper
        );
    }

    @Test
    @DisplayName("""
            Update category by existing id returns category DTO
            """)
    void update_WithExistingId_ReturnsCategoryDto() {
        Category category = createCategory();
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        CategoryDto expectedCategoryDto = createCategoryDto(category);

        when(categoryRepository.findById(ID_EXAMPLE)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        CategoryDto actualCategoryDto = categoryService.update(ID_EXAMPLE, categoryRequestDto);

        assertEquals(expectedCategoryDto, actualCategoryDto);

        verify(categoryRepository).findById(ID_EXAMPLE);
        verify(categoryMapper).updateCategoryFromDto(category, categoryRequestDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);

        verifyNoMoreInteractions(
                categoryRepository,
                categoryMapper,
                pageMapper
        );
    }

    @Test
    @DisplayName("""
            Update category by non-existing id throws exception
            """)
    void update_WithNonExistingId_ThrowsException() {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();

        when(categoryRepository.findById(NON_EXISTING_ID_EXAMPLE)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(NON_EXISTING_ID_EXAMPLE, categoryRequestDto)
        );

        assertEquals(EXPECTED_EXCEPTION_MESSAGE + NON_EXISTING_ID_EXAMPLE,
                exception.getMessage()
        );

        verify(categoryRepository).findById(NON_EXISTING_ID_EXAMPLE);
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryMapper, never()).updateCategoryFromDto(
                any(Category.class),
                any(CreateCategoryRequestDto.class)
        );
        verify(categoryMapper, never()).toDto(any(Category.class));

        verifyNoMoreInteractions(
                categoryRepository,
                categoryMapper,
                pageMapper
        );
    }

    @Test
    @DisplayName("""
            Delete category by existing id calls repository
            """)
    void deleteById_WithExistingId_CallsRepository() {
        categoryService.deleteById(ID_EXAMPLE);

        verify(categoryRepository).deleteById(ID_EXAMPLE);

        verifyNoMoreInteractions(
                categoryRepository,
                categoryMapper,
                pageMapper
        );
    }

    private CategoryDto createCategoryDto(Category category) {
        return new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());
    }

    private CreateCategoryRequestDto createCategoryRequestDto(Category category) {
        return new CreateCategoryRequestDto()
                .setName(category.getName())
                .setDescription(category.getDescription());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setId(ID_EXAMPLE);
        category.setName(CATEGORY_NAME_FIRST_EXAMPLE);
        category.setDescription(CATEGORY_DESC_FIRST_EXAMPLE);

        return category;
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName(CATEGORY_NAME_SECOND_EXAMPLE)
                .setDescription(CATEGORY_DESC_SECOND_EXAMPLE);
    }

    private PageDto<CategoryDto> createPageDto(
            Page<CategoryDto> categoryDtoPage
    ) {
        return new PageDto<>(
                categoryDtoPage.getContent(),
                categoryDtoPage.getNumber(),
                categoryDtoPage.getSize(),
                categoryDtoPage.getTotalElements(),
                categoryDtoPage.getTotalPages()
        );
    }
}
