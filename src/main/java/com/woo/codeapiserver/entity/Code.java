package com.woo.codeapiserver.entity;

import com.woo.codeapiserver.dto.enums.Language;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String code;
    private Long problemId;
    @Enumerated(EnumType.STRING)
    private Language language;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Code(String userId, String code, Long problemId, Language language) {
        this.userId = userId;
        this.code = code;
        this.problemId = problemId;
        this.language = language;
    }

    public void updateCode(String code) {
        this.code = code;
    }
}
