package com.lucassellis.gestao_estoque_inteligente.Business.Service;

import com.lucassellis.gestao_estoque_inteligente.Business.Mapper.PedidosMapper;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.in.PedidosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.PedidosDTOResponse;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.PedidosEntity;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.ProdutosEntity;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.exceptions.ResourceNotFoundException;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.repository.PedidosRepository;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.repository.ProdutosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidosService {

    private final PedidosRepository pedidoRepository;
    private final ProdutosRepository produtoRepository;
    private final ProdutosService produtosService; // Para mexer no estoque
    private final PedidosMapper mapper;

    @Transactional
    @CacheEvict(value = {"produtos", "pedidos"}, allEntries = true)
    public PedidosDTOResponse criarPedido(PedidosDTORequest dto) {
        PedidosEntity pedido = new PedidosEntity();
        pedido.setCliente(dto.getCliente());
        pedido.setDataPedido(LocalDateTime.now());

        // REGRA DE OURO: Baixa o estoque de cada item na esteira
        Set<ProdutosEntity> produtos = dto.getProdutosIds().stream()
                .map(id -> {
                    produtosService.atualizarEstoque(id, -1); // Tira 1 do estoque
                    return produtoRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Produto " + id + " não existe."));
                })
                .collect(Collectors.toSet());

        pedido.setProdutos(produtos);
        return mapper.toResponseDto(pedidoRepository.save(pedido));
    }

    @Cacheable(value = "pedidos")
    public List<PedidosDTOResponse> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "pedidos", key = "#id")
    public PedidosDTOResponse buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido " + id + " não encontrado."));
    }

    @Transactional
    @CacheEvict(value = {"produtos", "pedidos"}, allEntries = true)
    public void deletarPedido(Long id) {
        PedidosEntity pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));

        // Devolve o estoque antes de excluir o registro
        pedido.getProdutos().forEach(p -> produtosService.atualizarEstoque(p.getId(), 1));

        pedidoRepository.delete(pedido);
    }

    @Transactional
    @CacheEvict(value = {"produtos", "pedidos"}, allEntries = true)
    public PedidosDTOResponse atualizarPedido(Long id, PedidosDTORequest dto) {
        PedidosEntity pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));

        // 1. Devolve estoque dos produtos antigos
        pedido.getProdutos().forEach(p -> produtosService.atualizarEstoque(p.getId(), 1));

        // 2. Baixa estoque dos produtos novos que vieram no DTO
        Set<ProdutosEntity> novosProdutos = dto.getProdutosIds().stream()
                .map(prodId -> {
                    produtosService.atualizarEstoque(prodId, -1);
                    return produtoRepository.findById(prodId).get();
                })
                .collect(Collectors.toSet());

        pedido.setCliente(dto.getCliente());
        pedido.setProdutos(novosProdutos);

        return mapper.toResponseDto(pedidoRepository.save(pedido));
    }
}