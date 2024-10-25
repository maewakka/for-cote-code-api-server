package com.woo.codeapiserver.repository;

import com.woo.codeapiserver.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRepository extends JpaRepository<Code, Long> {
}
