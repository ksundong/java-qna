package com.codessquad.qna.domain.question;

import com.codessquad.qna.common.constants.FormatConstants;
import com.codessquad.qna.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime updatedDateTime;

    @Column(nullable = false)
    private boolean hasDeleted;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    public Answer() {}

    public Answer(User writer, Question question, String comment) {
        this.writer = writer;
        this.question = question;
        this.comment = comment;
        this.createdDateTime = LocalDateTime.now();
        this.updatedDateTime = LocalDateTime.now();
        this.hasDeleted = false;
    }

    public Long getId() {
        return id;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getFormattedCreatedDateTime() {
        return createdDateTime.format(FormatConstants.POST_DATA_DATE_TIME_FORMATTER);
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public String getFormattedUpdatedDateTime() {
        return updatedDateTime.format(FormatConstants.POST_DATA_DATE_TIME_FORMATTER);
    }

    public boolean hasDeleted() {
        return hasDeleted;
    }

    public Answer delete() {
        this.hasDeleted = true;
        return this;
    }
}
