package mate.academy.onlinebookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCategoryRequestDto {
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
    @Size(max = 255)
    private String description;
}
