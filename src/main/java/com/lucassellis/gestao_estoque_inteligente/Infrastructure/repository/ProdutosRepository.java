package com.lucassellis.gestao_estoque_inteligente.Infrastructure.repository;

import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.ProdutosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutosRepository extends JpaRepository<ProdutosEntity, Long> {

    // Retorna o produto completo se encontrar
    // entao vamos fazer uma busca nos produtos
    Optional<ProdutosEntity> findBySku(String sku);

    // Retorna apenas true ou false (muito rápido para validações)
    // boolean é sim ou nao se tem beleza se nao ele mostra tambem
    boolean existsBySku(String sku);

    // aqui alem de usar todos as funcionalidaddes jpa demos mais duas tarefas especificas que é porcurar o produto pelo sku
    // e verificar se existe pelo sku

}