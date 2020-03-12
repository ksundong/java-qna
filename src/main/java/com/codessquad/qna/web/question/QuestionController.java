package com.codessquad.qna.web.question;

import com.codessquad.qna.common.error.exception.LoginRequiredException;
import com.codessquad.qna.common.error.exception.QuestionNotFoundException;
import com.codessquad.qna.common.utils.HttpSessionUtils;
import com.codessquad.qna.domain.question.Answer;
import com.codessquad.qna.domain.question.AnswerRepository;
import com.codessquad.qna.domain.question.Question;
import com.codessquad.qna.domain.question.QuestionRepository;
import com.codessquad.qna.domain.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionController(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @GetMapping("")
    public String goQuestionPage(Model model, @RequestParam(defaultValue = "1", required = false) int page) {
        final int totalCount = questionRepository.countByHasDeletedFalse();
        final int size = 15;
        final int maxPage = totalCount / size + (totalCount % size == 0 ? 0 : 1);

        if (page > maxPage || page < 1) {
            return "redirect:/questions?page=" + maxPage;
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("createdDateTime").descending());
        model.addAttribute("questions", questionRepository.findAllByHasDeletedFalse(pageRequest).getContent());
        model.addAttribute("maxPage", maxPage);
        return "main";
    }

    @GetMapping("/form")
    public String goQuestionForm() {
        return "questions/form";
    }

    @PostMapping("")
    public String createQuestion(HttpSession session, @RequestParam String title, @RequestParam String contents) {
        User loginUser = HttpSessionUtils.getUserFromSession(session).orElseThrow(LoginRequiredException::new);
        Question question = new Question(loginUser, title, contents);
        questionRepository.save(question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String showQuestion(@PathVariable Long id, Model model, HttpSession session) {
        User loginUser = HttpSessionUtils.getUserFromSession(session).orElse(null);
        Question question = getQuestionIfExist(id);
        List<Answer> answers = answerRepository.findByQuestionIdAndHasDeletedFalse(id);

        model.addAttribute("question", question);
        model.addAttribute("isLoginUserEqualsWriter", question.isWrittenBy(loginUser));
        model.addAttribute("answers", answers);

        return "questions/show";
    }

    @GetMapping("/{id}/form")
    public String goQuestionModifyForm(@PathVariable Long id, Model model, HttpSession session) {
        User loginUser = HttpSessionUtils.getUserFromSession(session).orElseThrow(LoginRequiredException::new);
        Question question = getQuestionIfExist(id);

        if (!question.isWrittenBy(loginUser)) {
            return "redirect:/questions/" + id;
        }
        model.addAttribute("question", question);

        return "questions/modify-form";
    }

    @PutMapping("/{id}")
    public String updateQuestion(@PathVariable Long id,
                                 HttpSession session,
                                 @RequestParam String title,
                                 @RequestParam String contents) {
        User loginUser = HttpSessionUtils.getUserFromSession(session).orElseThrow(LoginRequiredException::new);
        Question question = getQuestionIfExist(id);

        if (!question.isWrittenBy(loginUser)) {
            return "redirect:/questions/" + id;
        }

        question.updateQuestionData(title, contents, LocalDateTime.now());
        questionRepository.save(question);
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public String deleteQuestion(@PathVariable Long id, HttpSession session) {
        User loginUser = HttpSessionUtils.getUserFromSession(session).orElseThrow(LoginRequiredException::new);
        Question question = getQuestionIfExist(id);

        if (!question.isWrittenBy(loginUser)) {
            return "redirect:/questions/" + id;
        }

        if (question.isDeletable()) {
            answerRepository.deleteAnswersInQuestion(question);
            questionRepository.save(question.delete());
            return "redirect:/";
        }
        return "redirect:/questions/" + id;
    }

    private Question getQuestionIfExist(Long id) {
        return questionRepository.findByIdAndHasDeleted(id, false).orElseThrow(QuestionNotFoundException::new);
    }

}
