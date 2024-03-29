package com.mitocode.config;

import com.mitocode.dto.ClientDTO;
import com.mitocode.dto.InvoiceDTO;
import com.mitocode.model.Client;
import com.mitocode.model.Invoice;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class  MapperConfig {

    @Bean("defaultMapper")
    public ModelMapper defaultMapper(){
        return new ModelMapper();
    }

    @Bean("clientMapper")
    public ModelMapper clientMapper(){
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //Escritura
        TypeMap<ClientDTO, Client> typeMap1 = mapper.createTypeMap(ClientDTO.class, Client.class);
        typeMap1.addMapping(ClientDTO::getNameClient, (dest, v) -> dest.setFirstName((String) v));
        typeMap1.addMapping(ClientDTO::getSurnameClient, (dest, v) -> dest.setLastName((String) v));
        typeMap1.addMapping(ClientDTO::getBirthDateClient, (dest, v) -> dest.setBirthDate((LocalDate) v));
        typeMap1.addMapping(ClientDTO::getUrlPhotoClient, (dest, v)-> dest.setUrlPhoto((String) v));

        //Lectura
        TypeMap<Client, ClientDTO> typeMap2 = mapper.createTypeMap(Client.class, ClientDTO.class);
        typeMap2.addMapping(Client::getFirstName, (dest, v) -> dest.setNameClient((String) v));
        typeMap2.addMapping(Client::getLastName, (dest, v) -> dest.setSurnameClient((String) v));
        typeMap2.addMapping(Client::getBirthDate, (dest, v) -> dest.setBirthDateClient((LocalDate) v));
        typeMap2.addMapping(Client::getUrlPhoto, (dest, v) -> dest.setUrlPhotoClient((String) v));

        return mapper;
    }

    @Bean("invoiceMapper")
    public ModelMapper invoiceMapper(){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //Lectura get
        TypeMap<Invoice, InvoiceDTO> typeMap1 = mapper.createTypeMap(Invoice.class, InvoiceDTO.class);
        typeMap1.addMapping(e -> e.getClient().getId(), (dest, v) -> dest.getClient().setId((String) v));
        typeMap1.addMapping(e -> e.getClient().getFirstName(), (dest, v) -> dest.getClient().setNameClient((String) v));
        typeMap1.addMapping(e -> e.getClient().getLastName(), (dest, v) -> dest.getClient().setSurnameClient((String) v));

        //Escritura post
        TypeMap<InvoiceDTO, Invoice> typeMap2 = mapper.createTypeMap(InvoiceDTO.class, Invoice.class);
        typeMap2.addMapping(e -> e.getClient().getId(), (dest, v) -> dest.getClient().setId((String) v));
        typeMap2.addMapping(e -> e.getClient().getNameClient(), (dest, v) -> dest.getClient().setFirstName((String) v));
        typeMap2.addMapping(e -> e.getClient().getSurnameClient(), (dest, v) -> dest.getClient().setLastName((String) v));

        return mapper;
    }

}
