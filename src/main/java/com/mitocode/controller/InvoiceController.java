package com.mitocode.controller;

import com.mitocode.dto.InvoiceDTO;
import com.mitocode.model.Invoice;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IInvoiceService;
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

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceService service;
    @Qualifier("invoiceMapper")
    private final ModelMapper mapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<InvoiceDTO>>> findAll() {
        //ModelMapper modelMapper = new ModelMapper();
        Flux<InvoiceDTO> fx = service.findAll().map(this::convertToDto); //e -> convertToDto(e)

        return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fx)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDTO>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<InvoiceDTO>> save(@Valid @RequestBody InvoiceDTO dto, final ServerHttpRequest req) {
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
    public Mono<ResponseEntity<InvoiceDTO>> update(@Valid @PathVariable("id") String id, @RequestBody InvoiceDTO dto) {
        return Mono.just(dto)
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
                .defaultIfEmpty(ResponseEntity.notFound().build());
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
    public Mono<EntityModel<InvoiceDTO>> getHateoas(@PathVariable("id") String id){
        Mono<Link> monoLink = linkTo(methodOn(InvoiceController.class).findById(id)).withRel("invoice-info").toMono();
        return service.findById(id)
                .map(this::convertToDto)
                .zipWith(monoLink, EntityModel::of);  //(d, link) -> EntityModel.of(d, link)
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<InvoiceDTO>>> getPage(
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

    @GetMapping("/generateReport/{id}")
    private Mono<ResponseEntity<byte[]>> generateReport(@PathVariable("id") String id)
    {
        return service.generateReport(id)
                .map(bytes -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(bytes))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    private InvoiceDTO convertToDto(Invoice model){
        return mapper.map(model, InvoiceDTO.class);
    }

    private Invoice convertToDocument(InvoiceDTO dto){
        return mapper.map(dto, Invoice.class);
    }
}
