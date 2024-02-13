package com.mitocode.service;

import com.mitocode.model.Invoice;
import reactor.core.publisher.Mono;

public interface IInvoiceService extends ICRUD<Invoice, String>{

    Mono<byte[]> generateReport(String idInvoice);

}
