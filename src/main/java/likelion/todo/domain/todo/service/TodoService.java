package likelion.todo.domain.todo.service;

import likelion.todo.domain.member.entity.Member;
import likelion.todo.domain.member.repository.MemberRepository;
import likelion.todo.domain.todo.dto.TodoEmojiRequestDTO;
import likelion.todo.domain.todo.dto.TodoRequestDTO;
import likelion.todo.domain.todo.dto.TodoResponseDTO;
import likelion.todo.domain.todo.entity.Todo;
import likelion.todo.domain.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TodoResponseDTO createTodo(Long member_id, TodoRequestDTO req) {
        Member member = findMemberById(member_id);

        Todo todo = Todo.builder()
                .member(member)
                .date(req.date())
                .content(req.content())
                .isChecked(false)
                .emoji("")
                .build();

        todoRepository.save(todo);

        return TodoResponseDTO.from(todo);
    }

    public List<TodoResponseDTO> getTodos(Long member_id) {
        findMemberById(member_id);

        return todoRepository.findByMemberId(member_id).stream()
                .map(TodoResponseDTO::from)
                .toList();
    }

    public List<TodoResponseDTO> getDailyTodos(Long member_id, Integer month, Integer day) {
        findMemberById(member_id);

        if ((month == null && day != null)
                || (month != null && day == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "요청 형식이 올바르지 않습니다.");
        }

        LocalDate today = LocalDate.now();

        int targetMonth = (month == null) ? today.getMonthValue() : month;
        int targetDay = (day == null) ? today.getDayOfMonth() : day;

        return todoRepository.findByMemberId(member_id).stream()
                .filter(todo ->
                        todo.getDate().getMonthValue() == targetMonth
                        && todo.getDate().getDayOfMonth() == targetDay)
                .map(TodoResponseDTO::from)
                .toList();
    }

    @Transactional
    public TodoResponseDTO updateTodoPatch(Long member_id, Long todo_id,
                                          TodoRequestDTO req) {
        findMemberById(member_id);
        Todo todo = findTodoById(todo_id);
        validateTodoOwner(todo, member_id);

        todo.updateTodoPatch(req.date(), req.content());

        return TodoResponseDTO.from(todo);
    }

    @Transactional
    public void deleteTodo(Long member_id, Long todo_id) {
        findMemberById(member_id);
        Todo todo = findTodoById(todo_id);
        validateTodoOwner(todo, member_id);

        todoRepository.delete(todo);
    }

    @Transactional
    public TodoResponseDTO updateTodoEmoji(Long member_id, Long todo_id,
                                           TodoEmojiRequestDTO req) {
        findMemberById(member_id);
        Todo todo = findTodoById(todo_id);
        validateTodoOwner(todo, member_id);

        todo.updateEmoji(req.emoji());

        return TodoResponseDTO.from(todo);
    }

    @Transactional
    public TodoResponseDTO updateTodoToggle(Long member_id, Long todo_id) {
        findMemberById(member_id);
        Todo todo = findTodoById(todo_id);
        validateTodoOwner(todo, member_id);

        todo.toggleChecked();

        return TodoResponseDTO.from(todo);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "멤버를 찾을 수 없습니다."
                ));
    }

    private Todo findTodoById(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "투두를 찾을 수 없습니다."
                ));
    }

    private void validateTodoOwner(Todo todo, Long memberId) {
        if (!todo.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "해당 멤버의 투두가 아닙니다."
            );
        }
    }
}
