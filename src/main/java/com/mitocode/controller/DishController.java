package com.mitocode.controller;

import com.mitocode.dto.DishDTO;
import com.mitocode.model.Dish;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IDishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.Objects;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final IDishService service;
    @Qualifier("defaultMapper")
    private final ModelMapper mapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<DishDTO>>> findAll() {
        return service.findAll()
                .map(this::convertToDto)
                .collectList()
                .map(dishDTOS -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Flux.fromIterable(dishDTOS)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DishDTO>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<DishDTO>> save(@Valid @RequestBody DishDTO dto, final ServerHttpRequest req) {
        return service.save(convertToDocument(dto))
                .map(this::convertToDto)
                .map(e -> ResponseEntity.created(
                                        URI.create(req.getURI().toString().concat("/").concat(e.getId()))
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<DishDTO>> update(@Valid @PathVariable("id") String id, @RequestBody DishDTO dto) {
        return service.findById(id).hasElement()
                .thenReturn(Objects.requireNonNull(service.update(id, convertToDocument(dto)).block()))
                .map(this::convertToDto)
                .map(e-> ResponseEntity.ok().body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());

        /*return Mono.just(dto)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap( e -> service.update(id, convertToDocument(dto)))
                .map(this::convertToDto)
                .map(e -> ResponseEntity
                        .ok()
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());*/
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id) {
        return service.delete(id)
                .flatMap(result -> {
                    if(result){
                        return Mono.just(ResponseEntity.noContent().build());
                    }else{
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<DishDTO>> getHateoas(@PathVariable("id") String id){
        Mono<Link> monoLink = linkTo(methodOn(DishController.class).findById(id)).withRel("dish-info").toMono();
        return service.findById(id)
                .map(this::convertToDto)
                .zipWith(monoLink, EntityModel::of);  //(d, link) -> EntityModel.of(d, link)
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<DishDTO>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "2") int size
    ){
        return service.getPage(PageRequest.of(page, size))
                .map(pageSupport -> new PageSupport<>(
                            pageSupport.getContent().stream().map(this::convertToDto).toList(),
                            pageSupport.getPageNumber(),
                            pageSupport.getPageSize(),
                            pageSupport.getTotalElements()
                            )
                )
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private DishDTO convertToDto(Dish model){
        return mapper.map(model, DishDTO.class);
    }

    private Dish convertToDocument(DishDTO dto){
        return mapper.map(dto, Dish.class);
    }
}
