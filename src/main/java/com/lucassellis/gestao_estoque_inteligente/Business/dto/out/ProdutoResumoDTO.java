package com.lucassellis.gestao_estoque_inteligente.Business.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProdutoResumoDTO {

    private String nome;

    private String sku;

    // essa qui nao entendi nada, ela foi criada com nome e sku eu acho que é para devolver algo simples ao cliente

}