package ru.itgirl.libraryproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookUpdateDto {
    private Long id;
    @Size(max = 10)
    @NotBlank(message = "Необходимо указать название")
    private String name;
    @Size(min = 3, max = 20)
    @NotBlank(message = "Необходимо указать жанр")
    private String genre;
}
