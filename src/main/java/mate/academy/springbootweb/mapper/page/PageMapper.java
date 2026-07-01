package mate.academy.springbootweb.mapper.page;

import mate.academy.springbootweb.dto.page.PageDto;
import org.springframework.data.domain.Page;

public interface PageMapper<T> {
    PageDto<T> toDto(Page<T> page);
}
