package com.lucassellis.gestao_estoque_inteligente.Business.dto.out;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutosDTOResponse {
    // aqui é a resposta ao usuario

    private Long id;
    private String nome;
    private String sku;
    private BigDecimal preco;
    private Integer quantidadeEstoque;

    // Removendo o Set<PedidosEntity> para evitar o loop infinito de JSON
}