package com.lucassellis.gestao_estoque_inteligente.Infrastructure.repository;

import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.PedidosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidosRepository extends JpaRepository<PedidosEntity, Long> {

}