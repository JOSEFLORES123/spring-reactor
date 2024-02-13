package com.mitocode.service.impl;

import com.mitocode.model.Dish;
import com.mitocode.repo.IDishRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDImpl<Dish, String> implements IDishService {

    /*@Autowired*/
    private final IDishRepo repo;

    @Override
    protected IGenericRepo<Dish, String> getRepo() {
        return repo;
    }



    /*public DishServiceImpl(IDishRepo repo) {
        this.repo = repo;
    }*/

    /*@Override
    public Mono<Dish> save(Dish dish) {
        return repo.save(dish);
    }

    @Override
    public Mono<Dish> update(String id, Dish dish) {
        return repo.findById(id).flatMap(e -> repo.save(dish));
    }

    @Override
    public Flux<Dish> findAll() {
        return repo.findAll();
    }

    @Override
    public Mono<Dish> findById(String id) {
        return repo.findById(id);
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return repo.findById(id)
                .hasElement()
                .flatMap(result -> {
                    if(result){
                        return repo.deleteById(id).thenReturn(true);
                    }else{
                        return Mono.just(false);
                    }
                });
    }*/
}
