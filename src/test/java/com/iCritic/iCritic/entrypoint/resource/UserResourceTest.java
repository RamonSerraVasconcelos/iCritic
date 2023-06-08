package com.iCritic.iCritic.entrypoint.resource;

import com.iCritic.iCritic.core.enums.BanActionEnum;
import com.iCritic.iCritic.core.fixture.CountryFixture;
import com.iCritic.iCritic.core.fixture.UserFixture;
import com.iCritic.iCritic.core.model.User;
import com.iCritic.iCritic.core.usecase.*;
import com.iCritic.iCritic.entrypoint.fixture.UserBanDtoFixture;
import com.iCritic.iCritic.entrypoint.fixture.UserRequestDtoFixture;
import com.iCritic.iCritic.entrypoint.mapper.UserDtoMapper;
import com.iCritic.iCritic.entrypoint.model.UserBanDto;
import com.iCritic.iCritic.entrypoint.model.UserRequestDto;
import com.iCritic.iCritic.entrypoint.model.UserResponseDto;
import com.iCritic.iCritic.entrypoint.validation.RoleValidator;
import com.iCritic.iCritic.exception.ResourceViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {

    @InjectMocks
    private UserResource userResource;

    @InjectMocks
    MockHttpServletRequest request;

    @Mock
    private FindUsersUseCase findUsersUseCase;

    @Mock
    private FindUserByIdUseCase findUserByIdUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private UpdateUserRoleUseCase updateUserRoleUseCase;

    @Mock
    private UpdateUserStatusUseCase updateUserStatusUseCase;

    @Mock
    private Validator validator;

    @Mock
    private RoleValidator roleValidator;

    @Captor
    private ArgumentCaptor<BanActionEnum> actionCaptor;

    @Test
    void givenCallToLoadAllUsers_thenCallFindUsersUseCaseAndReturnUsers() {
        List<User> users = List.of(UserFixture.load(), UserFixture.load(), UserFixture.load());

        when(findUsersUseCase.execute()).thenReturn(users);

        List<UserResponseDto> returnedUsers = userResource.loadAll();

        assertNotNull(returnedUsers);
        assertEquals(returnedUsers.get(0).getName(), users.get(0).getName());
        assertEquals(returnedUsers.get(0).getEmail(), users.get(0).getEmail());
        assertEquals(returnedUsers.get(0).getDescription(), users.get(0).getDescription());
        assertEquals(returnedUsers.get(0).isActive(), users.get(0).isActive());
        assertEquals(returnedUsers.get(0).getRole(), users.get(0).getRole());
        assertEquals(returnedUsers.get(0).getCountry().getId(), users.get(0).getCountry().getId());
        assertEquals(returnedUsers.get(0).getCountry().getName(), users.get(0).getCountry().getName());
        assertEquals(returnedUsers.get(0).getCreatedAt(), users.get(0).getCreatedAt());
    }

    @Test
    void givenCallToGetUserWithValidParameters_thenCallFindUserByIdUseCaseAndReturnUser() {
        User user = UserFixture.load();

        when(findUserByIdUseCase.execute(user.getId())).thenReturn(user);

        UserResponseDto returnedUser = userResource.get(user.getId());

        verify(findUserByIdUseCase).execute(user.getId());
        assertNotNull(returnedUser);
        assertEquals(returnedUser.getName(), user.getName());
        assertEquals(returnedUser.getEmail(), user.getEmail());
        assertEquals(returnedUser.getDescription(), user.getDescription());
        assertEquals(returnedUser.isActive(), user.isActive());
        assertEquals(returnedUser.getRole(), user.getRole());
        assertEquals(returnedUser.getCountry().getId(), user.getCountry().getId());
        assertEquals(returnedUser.getCountry().getName(), user.getCountry().getName());
        assertEquals(returnedUser.getCreatedAt(), user.getCreatedAt());
    }

    @Test
    void givenCallToUpdateUserWithValidParameters_thenCallUpdateUserUseCaseAndReturnUser() {
        UserRequestDto userRequestDto = UserRequestDtoFixture.load();
        User user = UserDtoMapper.INSTANCE.userRequestDtoToUser(userRequestDto);

        when(validator.validate(userRequestDto)).thenReturn(Collections.emptySet());

        user.setId(1L);
        user.setCountry(CountryFixture.load());
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);

        when(updateUserUseCase.execute(any(), any())).thenReturn(user);

        request.setAttribute("userId", user.getId());

        UserResponseDto updatedUser = userResource.update(request, userRequestDto);

        verify(validator).validate(userRequestDto);
        verify(updateUserUseCase).execute(any(), any());

        assertNotNull(updatedUser);
        assertNotNull(updatedUser.getId());
        assertEquals(updatedUser.getName(), user.getName());
        assertEquals(updatedUser.getEmail(), user.getEmail());
        assertEquals(updatedUser.getDescription(), user.getDescription());
        assertEquals(updatedUser.getRole(), user.getRole());
        assertEquals(updatedUser.getCountry().getId(), user.getCountry().getId());
        assertEquals(updatedUser.getCountry().getName(), user.getCountry().getName());
        assertNotNull(updatedUser.getCreatedAt());
        assertTrue(updatedUser.isActive());
    }

    @Test
    void givenCallToUpdateUserWithInvalidParameters_thenThrowResourceViolationException() {
        UserRequestDto userRequestDto = UserRequestDtoFixture.load();

        Set<ConstraintViolation<UserRequestDto>> violations = new HashSet<>();
        violations.add(createMockViolation("name"));

        when(validator.validate(userRequestDto)).thenReturn(violations);

        assertThrows(ResourceViolationException.class, () -> userResource.update(request, userRequestDto));
    }

    @Test
    void givenCallToUpdateUserWithInvalidParameters_whenParametersAreEmailOrPassword_thenDontThrowException() {
        UserRequestDto userRequestDto = UserRequestDtoFixture.load();
        User user = UserDtoMapper.INSTANCE.userRequestDtoToUser(userRequestDto);
        user.setId(1L);

        Set<ConstraintViolation<UserRequestDto>> violations = new HashSet<>();
        violations.add(createMockViolation("email"));
        violations.add(createMockViolation("password"));

        request.setAttribute("userId", user.getId());

        when(validator.validate(userRequestDto)).thenReturn(violations);
        when(updateUserUseCase.execute(any(), any())).thenReturn(user);

        assertDoesNotThrow(() -> userResource.update(request, userRequestDto));
    }

    @Test
    void givenCallToChangeRoleWithValidParameters_thenCallUpdateUserRoleUseCase() {
        UserRequestDto userRequestDto = UserRequestDtoFixture.load();

        request.setAttribute("role", userRequestDto.getRole());

        doNothing().when(roleValidator).validate(any(), any());
        doNothing().when(updateUserRoleUseCase).execute(any(), any());

        userResource.changeRole(request, userRequestDto.getId(), userRequestDto);

        verify(roleValidator).validate(any(), any());
        verify(updateUserRoleUseCase).execute(any(), any());
    }

    @Test
    void givenCallToBanUserWithValidParameters_thenCallUpdateUserStatusUseCaseWithActionBan_andReturnResponseOk() {
        Long id = 1L;
        UserBanDto userBanDto = UserBanDtoFixture.load();

        request.setAttribute("role", "ADMIN");

        when(validator.validate(userBanDto)).thenReturn(Collections.emptySet());
        doNothing().when(updateUserStatusUseCase).execute(any(), any(), actionCaptor.capture());

        ResponseEntity<Void> response = userResource.ban(request, id, userBanDto);

        assertEquals(actionCaptor.getValue(), BanActionEnum.BAN);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void givenCallToBanUserWithInvalidParameters_thenThrowResourceViolationException() {
        Long id = 1L;
        UserBanDto userBanDto = UserBanDtoFixture.load();

        request.setAttribute("role", "ADMIN");

        Set<ConstraintViolation<UserBanDto>> violations = new HashSet<>();
        violations.add(createMockViolation("motive"));

        when(validator.validate(userBanDto)).thenReturn(violations);

        assertThrows(ResourceViolationException.class, () -> userResource.ban(request, id, userBanDto));
    }

    @Test
    void givenCallToUnbanUserWithValidParameters_thenCallUpdateUserStatusUseCaseWithActionUnban_andReturnResponseOk() {
        Long id = 1L;
        UserBanDto userBanDto = UserBanDtoFixture.load();

        request.setAttribute("role", "ADMIN");

        when(validator.validate(userBanDto)).thenReturn(Collections.emptySet());
        doNothing().when(updateUserStatusUseCase).execute(any(), any(), actionCaptor.capture());

        ResponseEntity<Void> response = userResource.unban(request, id, userBanDto);

        assertEquals(actionCaptor.getValue(), BanActionEnum.UNBAN);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void givenCallToUnbanUserWithInvalidParameters_thenThrowResourceViolationException() {
        Long id = 1L;
        UserBanDto userBanDto = UserBanDtoFixture.load();

        request.setAttribute("role", "ADMIN");

        Set<ConstraintViolation<UserBanDto>> violations = new HashSet<>();
        violations.add(createMockViolation("motive"));

        when(validator.validate(userBanDto)).thenReturn(violations);

        assertThrows(ResourceViolationException.class, () -> userResource.unban(request, id, userBanDto));
    }

    private <T> ConstraintViolation<T> createMockViolation(String propertyName) {
        ConstraintViolation<T> violation = Mockito.mock(ConstraintViolation.class);
        Path propertyPath = Mockito.mock(Path.class);
        Mockito.when(propertyPath.toString()).thenReturn(propertyName);
        Mockito.when(violation.getPropertyPath()).thenReturn(propertyPath);
        return violation;
    }
}
