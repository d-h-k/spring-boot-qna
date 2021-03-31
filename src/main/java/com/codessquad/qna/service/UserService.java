package com.codessquad.qna.service;

import com.codessquad.qna.domain.User;
import com.codessquad.qna.domain.UserRepository;
import com.codessquad.qna.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.codessquad.qna.exception.ExceptionMessages.*;
import static com.codessquad.qna.utils.SessionUtil.getLoginUser;
import static com.codessquad.qna.utils.SessionUtil.setLoginUser;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            throw new UnacceptableDuplicationException(REDUNDANT_USERID);
        }
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User showProfile(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUNDED_USER));
    }


    public void updateUser(Long id, String pastPassword, User updatedUser, HttpSession session) {
        User sessionUser = getLoginUser(session);
        User currentUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUNDED_USER));

        if (!currentUser.isMatchingPassword(pastPassword)) {
            throw new UnauthorizedProfileModificationException();
        }

        if (sessionUser.equals(updatedUser)) {
            sessionUser.update(updatedUser);
        }

        userRepository.save(sessionUser);
        logger.debug("update User {}", sessionUser.getUserId());
    }

    public void validationCheck(Long id, HttpSession session) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUNDED_USER));
        if (!foundUser.isSessionSameAsUser(session)) {
            logger.debug(PROFILE_MODIFICATION_FAIL);
            throw new UnauthorizedProfileModificationException();
        }
    }

    public void login(String userId, String password, HttpSession session) {
        User foundUser = userRepository.findByUserId(userId).orElseThrow(() -> new LoginFailedException());

        if (!foundUser.isMatchingPassword(password)) {
            throw new LoginFailedException();
        }

        logger.debug("Login Success");
        setLoginUser(session, foundUser);
    }
}
