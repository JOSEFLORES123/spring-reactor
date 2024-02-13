package com.mitocode.service.impl;

import com.mitocode.model.Menu;
import com.mitocode.repo.IMenuRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends CRUDImpl<Menu, String> implements IMenuService {


    private final IMenuRepo repo;


    @Override
    protected IGenericRepo<Menu, String> getRepo() {
        return repo;
    }
}
