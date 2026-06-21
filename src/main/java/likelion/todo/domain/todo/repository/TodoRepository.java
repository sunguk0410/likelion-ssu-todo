package likelion.todo.domain.todo.repository;

import likelion.todo.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByMemberId(Long member_id);
}
