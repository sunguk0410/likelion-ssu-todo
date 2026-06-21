package likelion.todo.domain.todo.controller;

import likelion.todo.domain.todo.dto.TodoEmojiRequestDTO;
import likelion.todo.domain.todo.dto.TodoRequestDTO;
import likelion.todo.domain.todo.dto.TodoResponseDTO;
import likelion.todo.domain.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/{member_id}/todos")
    public ResponseEntity<TodoResponseDTO> createTodo(@PathVariable Long member_id,
                                                      @RequestBody TodoRequestDTO req) {
        return ResponseEntity.ok(todoService.createTodo(member_id, req));
    }

    @GetMapping("/{member_id}/todos")
    public ResponseEntity<List<TodoResponseDTO>> getAllTodos(@PathVariable Long member_id) {
        return ResponseEntity.ok(todoService.getTodos(member_id));
    }

    @GetMapping("/{member_id}/todos/daily")
    public ResponseEntity<List<TodoResponseDTO>> getDailyTodos(@PathVariable Long member_id,
                                                         @RequestParam(required = false) Integer month,
                                                         @RequestParam(required = false) Integer day) {
        return ResponseEntity.ok(todoService.getDailyTodos(member_id, month, day));
    }

    @PatchMapping("/{member_id}/todos/{todo_id}")
    public ResponseEntity<TodoResponseDTO> updateTodoPatch(@PathVariable Long member_id,
                                                           @PathVariable Long todo_id,
                                                           @RequestBody TodoRequestDTO req) {
        return ResponseEntity.ok(todoService.updateTodoPatch(member_id, todo_id, req));
    }

    @DeleteMapping("/{member_id}/todos/{todo_id}")
    public ResponseEntity<TodoResponseDTO> deleteTodo(@PathVariable Long member_id,
                                                      @PathVariable Long todo_id) {
        todoService.deleteTodo(member_id, todo_id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{member_id}/todos/{todo_id}/reviews")
    public ResponseEntity<TodoResponseDTO> updateTodoEmoji(@PathVariable Long member_id,
                                                           @PathVariable Long todo_id,
                                                           @RequestBody TodoEmojiRequestDTO req) {
        return ResponseEntity.ok(todoService.updateTodoEmoji(member_id, todo_id, req));
    }

    @PatchMapping("/{member_id}/todos/{todo_id}/check")
    public ResponseEntity<TodoResponseDTO> updateTodoToggle(@PathVariable Long member_id,
                                                            @PathVariable Long todo_id) {
        return ResponseEntity.ok(todoService.updateTodoToggle(member_id, todo_id));
    }
}
