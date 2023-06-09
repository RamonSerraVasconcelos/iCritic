package com.iCritic.users.dataprovider.gateway.database.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iCritic.users.core.fixture.UserFixture;
import com.iCritic.users.core.model.User;
import com.iCritic.users.dataprovider.gateway.database.entity.UserEntity;
import com.iCritic.users.dataprovider.gateway.database.fixture.UserEntityFixture;
import com.iCritic.users.dataprovider.gateway.database.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FindUserByEmailGatewayTest {

    @InjectMocks
    private FindUserByEmailGateway findUserByEmailGateway;

    @Mock
    private UserRepository userRepository;

    @Test
    void givenExecution_callUserRepository_thenReturnUser() {
        User user = UserFixture.load();
        UserEntity userEntity = UserEntityFixture.load();

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        User foundUser = findUserByEmailGateway.execute(user.getEmail());

        verify(userRepository).findByEmail(anyString());

        assertNotNull(foundUser);
        assertEquals(foundUser.getName(), user.getName());
        assertEquals(foundUser.getEmail(), user.getEmail());
        assertEquals(foundUser.getDescription(), user.getDescription());
        assertEquals(foundUser.getPassword(), user.getPassword());
        assertEquals(foundUser.isActive(), user.isActive());
        assertEquals(foundUser.getRole(), user.getRole());
        assertEquals(foundUser.getCountry().getId(), user.getCountry().getId());
        assertEquals(foundUser.getCountry().getName(), user.getCountry().getName());
        assertEquals(foundUser.getCreatedAt(), userEntity.getCreatedAt());
    }
}
