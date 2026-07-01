package mate.academy.springbootweb.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequestDto {
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
    @Size(max = 255)
    private String description;
}
