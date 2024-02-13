package com.mitocode.service.impl;

import com.mitocode.model.User;
import com.mitocode.repo.IUserRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDImpl<User, String> implements IUserService {


    private final IUserRepo repo;


    @Override
    protected IGenericRepo<User, String> getRepo() {
        return repo;
    }
}
