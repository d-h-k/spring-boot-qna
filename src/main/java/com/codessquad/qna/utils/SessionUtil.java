package com.codessquad.qna.utils;

import com.codessquad.qna.domain.User;
import com.codessquad.qna.exception.NotLoggedInException;

import javax.servlet.http.HttpSession;

import static com.codessquad.qna.exception.ExceptionMessages.ANYONE_NOT_LOGGED_IN;


public class SessionUtil {

    private static final String SESSION_KEY_LOGIN_USER = "loginUser";

    private SessionUtil() {
    }

    public static boolean isLoginUser(HttpSession session) {
        return session.getAttribute(SESSION_KEY_LOGIN_USER) != null;
    }

    public static User getLoginUser(HttpSession session) {
        User loginUser = (User) session.getAttribute(SESSION_KEY_LOGIN_USER);
        if (loginUser == null) {
            throw new NotLoggedInException(ANYONE_NOT_LOGGED_IN);
        }
        return loginUser;
    }

    public static void setLoginUser(HttpSession session, User user) {
        session.setAttribute(SESSION_KEY_LOGIN_USER, user);
    }

    public static void removeLoginUser(HttpSession session) {
        session.removeAttribute(SESSION_KEY_LOGIN_USER);
    }

}
