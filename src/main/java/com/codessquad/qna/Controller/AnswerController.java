package com.codessquad.qna.Controller;

import com.codessquad.qna.domain.Answer;
import com.codessquad.qna.domain.AnswerRepository;
import com.codessquad.qna.domain.QuestionRepostory;
import com.codessquad.qna.domain.User;
import com.codessquad.qna.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

import static com.codessquad.qna.utils.SessionUtil.*;

@Controller
public class AnswerController {

    private static final Logger logger = LoggerFactory.getLogger(AnswerController.class);

    private final QuestionRepostory questionRepostory;
    private final AnswerRepository answerRepository;

    public AnswerController(QuestionRepostory questionRepostory, AnswerRepository answerRepository) {
        this.questionRepostory = questionRepostory;
        this.answerRepository = answerRepository;
    }

    @PostMapping("/qna/{questionId}/answers")
    public String createAnswer(@PathVariable Long questionId, String contents, HttpSession session) {
        if (!isLoginUser(session)) {
            logger.info("답변달기 - 실패 : 권한(로그인)되지 않은 사용자의 답변달기 시도가 실패함");
            return "redirect:/user/login";
        }
        User loginUser = getLoginUser(session);
        Answer answer = new Answer(loginUser, questionRepostory.getOne(questionId), contents);
        answerRepository.save(answer);
        logger.info("답변달기 - 성공 : 답변이 정상적으로 추가됨");
        return String.format("redirect:/qna/%d", questionId);
    }

    @DeleteMapping("/qna/{questionId}/answers/{answerId}")
    public String removeAnswer(@PathVariable Long questionId, @PathVariable Long answerId, HttpSession session) {
        User ownerUser = questionRepostory.findById(questionId).orElseThrow(NotFoundException::new).getWriter();
        if (!isValidUser(session, ownerUser)) {
            logger.info("답변달기 - 실패 : 권한(로그인)되지 않은 사용자의 답변달기 시도가 실패함");
            return String.format("redirect:/qna/%d", questionId);
        }
        answerRepository.delete(answerRepository.findById(answerId).orElseThrow(NotFoundException::new));
        return String.format("redirect:/qna/%d", questionId);
    }

}
