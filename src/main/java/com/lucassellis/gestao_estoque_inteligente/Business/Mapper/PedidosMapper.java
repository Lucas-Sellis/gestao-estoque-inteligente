package com.lucassellis.gestao_estoque_inteligente.Business.Mapper;

import com.lucassellis.gestao_estoque_inteligente.Business.dto.in.PedidosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.PedidosDTOResponse;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.PedidosEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidosMapper {

    // o mapper esta fazendo a conversao automatica de dto para entity nesse caso um pouco mais complexo pois estamos usando response e request

    @Mapping(target = "id", ignore = true) // aqui vamos ignorar o id pois ele sera gerado automaticamenre
    @Mapping(target = "produtos", ignore = true) // A Service buscará os produtos pelos IDs
    @Mapping(target = "dataPedido", ignore = true) // A Service colocará a data atual
    PedidosEntity toEntity(PedidosDTORequest dto); // aqui vamos converter dto requeest em entity

    PedidosDTOResponse toResponseDto(PedidosEntity entity); // aqui vamos converter entitu em dto
}