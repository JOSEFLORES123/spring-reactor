package com.mitocode.service.impl;

import com.mitocode.model.Invoice;
import com.mitocode.model.InvoiceDetail;
import com.mitocode.repo.IClientRepo;
import com.mitocode.repo.IDishRepo;
import com.mitocode.repo.IInvoiceRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDImpl<Invoice, String> implements IInvoiceService {


    private final IInvoiceRepo invoiceRepo;
    private final IClientRepo iClientRepo;
    private final IDishRepo iDishRepo;


    @Override
    protected IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

    private Mono<Invoice> populateClient(Invoice invoice) {
        return iClientRepo.findById(invoice.getClient().getId())
                .map(client -> {
                    invoice.setClient(client);
                    return invoice;
                });
    }

    private Mono<Invoice> populateItems(Invoice invoice) {

        List<Mono<InvoiceDetail>> lst = invoice.getItems().stream()
                .map(item -> iDishRepo.findById(item.getDish().getId())
                        .map(dish -> {
                            item.setDish(dish);
                            return item;
                        })
                ).toList();
        return Mono.when(lst).then(Mono.just(invoice));
    }

    public byte[] generatePdfReport(Invoice invoice) {
        try (InputStream inputStream = getClass().getResourceAsStream("/facturas.jrxml")) {
            //generar PDF > mapas
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("txt_client", invoice.getClient().getFirstName());
            //compilar el archivo jasper
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            //poblar reporte : (archivo a compilar,bloque de parametros,lista a poblar de tipo fill)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                    new JRBeanCollectionDataSource(invoice.getItems()));
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return invoiceRepo.findById(idInvoice)
                .flatMap(this::populateClient)
                .flatMap(this::populateItems)
                .map(this::generatePdfReport)
                .onErrorResume(e-> Mono.empty());
    }
}
