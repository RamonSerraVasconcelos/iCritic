package com.iCritic.users.dataprovider.gateway.database.impl;

import com.iCritic.users.core.usecase.boundary.UpdateUserStatusBoundary;
import com.iCritic.users.dataprovider.gateway.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserStatusGateway implements UpdateUserStatusBoundary {

    private final UserRepository userRepository;

    public void execute(Long id, boolean active) {
        userRepository.updateStatus(id, active);
    }
}
