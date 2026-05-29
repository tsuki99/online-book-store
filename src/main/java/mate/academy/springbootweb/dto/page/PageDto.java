package mate.academy.springbootweb.dto.page;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
