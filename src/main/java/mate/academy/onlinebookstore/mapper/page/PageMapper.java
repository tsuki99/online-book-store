package mate.academy.onlinebookstore.mapper.page;

import mate.academy.onlinebookstore.dto.page.PageDto;
import org.springframework.data.domain.Page;

public interface PageMapper<T> {
    PageDto<T> toDto(Page<T> page);
}
