package com.iCritic.iCritic.core.usecase.boundary;

import com.iCritic.iCritic.core.model.User;

public interface FindUserByIdBoundary {

    User execute(Long id);
}