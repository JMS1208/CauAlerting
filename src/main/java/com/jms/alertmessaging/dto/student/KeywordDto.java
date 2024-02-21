package com.jms.alertmessaging.dto.student;

import com.jms.alertmessaging.entity.keyword.Keyword;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class KeywordDto {

    @NotNull(message = "학부 아이디 입력 누락")
    private Long departmentId;

    @NotNull(message = "키워드 입력 누락")
    @Size(max = 10, message = "키워드 최대 10개 까지 입력 가능")
    private List<@Size(max = 10, message = "키워드는 최대 10자") String> keywords;

    public List<String> trimKeywords() {
        this.keywords = this.keywords.stream().map(String::strip).toList();
        return this.keywords;
    }
}
