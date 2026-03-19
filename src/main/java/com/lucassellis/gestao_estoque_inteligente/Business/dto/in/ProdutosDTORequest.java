package com.lucassellis.gestao_estoque_inteligente.Business.dto.out;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutosDTORequest {

    // aqui estamos isolando a tabela produtos, vamos receber os dados abaixo do request no caso o usuario

    private Long id;
    private String nome;
    private String sku;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
    private Integer quantidadeAlterada;



    // DICA: Não colocamos a lista de pedidos aqui para evitar o loop.
    // Se precisar mostrar em qual pedido o produto está,
    // criamos um endpoint específico para isso depois.
}