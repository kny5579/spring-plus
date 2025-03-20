package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;

@Repository
@AllArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(todo)
                .where(todo.id.eq(todoId))
                .leftJoin(todo.user).fetchJoin()
                .fetchOne());
    }

    @Override
    public Page<TodoSearchResponse> searchByTitleAndNicknameAndCreatedAtBetween(Pageable pageable,
                                                                                String title,
                                                                                String nickname,
                                                                                LocalDateTime startDate,
                                                                                LocalDateTime endDate) {

        List<TodoSearchResponse> searchList = jpaQueryFactory
                .select(
                        new QTodoSearchResponse(
                                todo.id,
                                todo.title,
                                todo.managers.size(),
                                todo.comments.size())
                )
                .from(todo)
                .leftJoin(todo.managers)
                .leftJoin(todo.comments)
                .where(eqTitle(title), eqNickname(nickname), betweenCreatedAt(startDate, endDate))
                .orderBy(todo.createdAt.desc())
                .fetch();

        long total = Optional.ofNullable(
                jpaQueryFactory
                        .select(todo.count())
                        .from(todo)
                        .where(
                                eqTitle(title),
                                eqNickname(nickname),
                                betweenCreatedAt(startDate, endDate)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(searchList, pageable, total);
    }

    private BooleanExpression eqTitle(String title) {
        if (title == null || title.isEmpty()) {
            return null;
        }
        return todo.title.containsIgnoreCase(title);
    }

    private BooleanExpression eqNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return null;
        }
        return todo.user.nickname.containsIgnoreCase(nickname);
    }

    private BooleanExpression betweenCreatedAt(LocalDateTime startDate, LocalDateTime endDate) {
        return todo.createdAt.between(startDate, endDate);
    }
}
