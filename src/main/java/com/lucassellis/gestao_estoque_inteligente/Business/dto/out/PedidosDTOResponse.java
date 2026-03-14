package com.lucassellis.gestao_estoque_inteligente.Business.dto.out;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidosDTOResponse {

    // aqui no caso é a devolucao dos dados ao cliente eu acho
    private Long id;
    private LocalDateTime dataPedido;
    private String cliente;

    // Em vez da Entity pura, usamos uma lista de DTOs simples de produto
    private List<ProdutoResumoDTO> produtos;
}