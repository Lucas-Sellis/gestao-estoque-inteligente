package com.lucassellis.gestao_estoque_inteligente.Business.Mapper;


import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.ProdutosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.ProdutosDTOResponse;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.ProdutosEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutosMapper {

    // Transforma o Request em Entidade (Ignora o ID pois o banco gera)
    @Mapping(target = "id", ignore = true) // aqui vamos ignorar o id pois ele ja é gerado no banco
    @Mapping(target = "pedidos", ignore = true) // mesma coisa sera ignorado
    ProdutosEntity toEntity(ProdutosDTORequest dto);

    // Transforma Entidade em Response (O que o usuário vê)
    ProdutosDTOResponse toResponseDto(ProdutosEntity entity); //  isso exatamente ele pega a entity e volta pra response
}