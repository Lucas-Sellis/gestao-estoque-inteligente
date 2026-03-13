package com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_pedidos")
public class PedidosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data do pedido é obrigatória")
    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido; // LocalDateTime é melhor que String para datas

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Column(name = "cliente", nullable = false)
    private String cliente;

    @ManyToMany
    @JoinTable(
            name = "pedido_produto", // Nome da tabela intermediária que o JPA criará
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    private Set<ProdutosEntity> produtos;

    //No seu caso, o Pedido faz todo o sentido ser o dono, pois é ele quem "escolhe" os produtos.
}