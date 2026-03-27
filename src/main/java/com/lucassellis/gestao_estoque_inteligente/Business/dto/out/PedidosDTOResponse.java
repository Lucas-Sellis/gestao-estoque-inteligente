package com.lucassellis.gestao_estoque_inteligente.Business.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidosDTOResponse {

    // aqui no caso é a devolucao dos dados ao cliente eu acho
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dataPedido;
    private String cliente;

    // Em vez da Entity pura, usamos uma lista de DTOs simples de produto
    private Set<ProdutoResumoDTO> produtos;
}