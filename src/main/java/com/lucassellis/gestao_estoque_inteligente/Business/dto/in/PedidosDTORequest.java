package com.lucassellis.gestao_estoque_inteligente.Business.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidosDTORequest {

    // aqui estamos isolando a entity  o request é oque vai vir d fora pelo cliente

    @NotBlank(message = "O nome do cliente é obrigatório") // ele tem que informar o nome do cliente
    private String cliente;

    @NotEmpty(message = "O pedido deve conter pelo menos um ID de produto") // ele vai ter que preencer o id do pedido
    private Set<Long> produtosIds; // O usuário manda apenas: [1, 5, 22]
}