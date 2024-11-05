package com.woo.codeapiserver.repository;

import com.woo.codeapiserver.dto.enums.Language;
import com.woo.codeapiserver.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long> {

    Optional<Code> findCodeByUserIdAndProblemIdAndLanguage(String userId, Long problemId, Language language);

}
