package mate.academy.springbootweb.mapper.page;

import mate.academy.springbootweb.dto.page.PageDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageMapperImpl<T> implements PageMapper<T> {
    @Override
    public PageDto<T> toDto(Page<T> page) {
        return new PageDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
