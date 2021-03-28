package com.codessquad.qna.service;

import com.codessquad.qna.domain.Question;
import com.codessquad.qna.domain.QuestionRepostory;
import com.codessquad.qna.domain.User;
import com.codessquad.qna.exception.NotFoundException;
import com.codessquad.qna.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.codessquad.qna.exception.ExceptionMessages.FREE2ASK_BUT_DELETE;
import static com.codessquad.qna.exception.ExceptionMessages.UNAUTHORIZED_FAILED_QUESTION;
import static com.codessquad.qna.utils.SessionUtil.isValidUser;

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepostory questionRepostory;

    public QuestionService(QuestionRepostory questionRepostory) {
        this.questionRepostory = questionRepostory;
    }

    public void createQuestion(Question question, User user) {
        Question addNewQuestion = new Question(question, user);
        questionRepostory.save(addNewQuestion);
        //@Todo 질문을 만드는데 질문이 필요하다..?
        //@Todo 비어있는 타이틀이나, 비어있는 컨텐츠가 들어오면 그냥 저장되는 문제..
    }

    public Question showDetailQuestion(Long id) {
        return questionRepostory.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<Question> findAll() {
        return questionRepostory.findAllByDeletedFalse();
    }

    public void updateQuestion(Long id, String title, String contents) {
        Question question = questionRepostory.findById(id).orElseThrow(NotFoundException::new);
        question.update(title, contents);
        questionRepostory.save(question);
        logger.info("질문글 업데이트됨, questionId : {}", id);
    }

    public void deleteQuestion(Long questionId, HttpSession session) {
        Question question = questionRepostory.findById(questionId).orElseThrow(NotFoundException::new);

        if (!isValidUser(session, question.getWriter())) {
            logger.info(UNAUTHORIZED_FAILED_QUESTION);
            throw new UnauthorizedException(UNAUTHORIZED_FAILED_QUESTION);
        }

        if (!question.isDeletable()) {
            throw new UnauthorizedException(FREE2ASK_BUT_DELETE);
        }
        question.deleteQuestion();
        questionRepostory.save(question);
        logger.info("질문글 삭제 - 성공");

    }

    public void updateForm(Long id, Model model, HttpSession session) {
        Question question = questionRepostory.findById(id).orElseThrow(NotFoundException::new);
        if (!isValidUser(session, question.getWriter())) {
            logger.info(UNAUTHORIZED_FAILED_QUESTION);
            throw new UnauthorizedException(UNAUTHORIZED_FAILED_QUESTION);
        }
        logger.info("글을 수정하는 사람 : {}", question.getTitle());
        model.addAttribute("question", question);
    }


}
