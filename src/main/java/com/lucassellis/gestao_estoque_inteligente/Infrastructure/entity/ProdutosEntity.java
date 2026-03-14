package com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal; // Importante para o preço
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_produtos")
public class ProdutosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "nome", unique = true, nullable = false)
    private String nome;

    @NotBlank(message = "O SKU é obrigatório")
    @Column(name = "sku", unique = true, nullable = false)
    private String sku; //SKU significa Stock Keeping Unit (Unidade de Manutenção de Estoque).
    // O SKU é o código simplificado que o lojista cria para achar esse produto rápido no estoque.

    @NotNull(message = "O preço é obrigatório")
    @PositiveOrZero(message = "O preço deve ser zero ou maior")
    @Column(name = "preco")
    private BigDecimal preco; // Mudamos de String para BigDecimal

    @NotNull(message = "A quantidade é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque; // Mudamos de String para Integer e corrigimos o nome

    @ManyToMany(mappedBy = "produtos") // Indica que o lado dono é a PedidosEntity
    private Set<PedidosEntity> pedidos;

 // as entites nada mais sao que nossa tbela no banco de dados aqui no caso essa tabela produtos é ligada a tabela pedidos
    // pois podem haver varios pedidos com o mesmo produto

}