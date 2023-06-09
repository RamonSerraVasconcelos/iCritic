package com.iCritic.users.core.usecase;

import com.iCritic.users.core.model.User;
import com.iCritic.users.core.usecase.boundary.FindUserByEmailBoundary;
import com.iCritic.users.exception.ResourceViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignInUserUseCase {

    private final FindUserByEmailBoundary findUserByEmailBoundary;

    private final BCryptPasswordEncoder bcrypt;

    public User execute(User userData) {
        User user = findUserByEmailBoundary.execute(userData.getEmail());

        if (!nonNull(user)) {
            throw new ResourceViolationException("Invalid email or password");
        }

        boolean isPasswordValid = bcrypt.matches(userData.getPassword(), user.getPassword());

        if (!isPasswordValid) {
            throw new ResourceViolationException("Invalid email or password");
        }

        return user;
    }
}
