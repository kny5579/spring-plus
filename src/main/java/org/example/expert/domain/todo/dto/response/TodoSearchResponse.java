package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
public class TodoSearchResponse {

    private final Long id;
    private final String title;
    private final int managerCount;
    private final int commentCount;

    @QueryProjection
    public TodoSearchResponse(Long id, String title, int managerCount, int commentCount) {
        this.id = id;
        this.title = title;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }
}
