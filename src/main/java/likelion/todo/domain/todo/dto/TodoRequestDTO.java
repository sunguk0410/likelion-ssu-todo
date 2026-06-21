package likelion.todo.domain.todo.dto;

import java.time.LocalDateTime;

public record TodoRequestDTO(
        LocalDateTime date,
        String content
) {
}
