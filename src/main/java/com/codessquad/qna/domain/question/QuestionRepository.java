package com.codessquad.qna.domain.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndHasDeleted(Long id, boolean hasDeleted);

    int countByHasDeletedFalse();

    Page<Question> findAllByHasDeletedFalse(Pageable pageable);

}
