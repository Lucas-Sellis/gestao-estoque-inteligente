package com.lucassellis.gestao_estoque_inteligente.Business.Service;

import com.lucassellis.gestao_estoque_inteligente.Business.Mapper.PedidosMapper;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.in.PedidosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.PedidosDTOResponse;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.PedidosEntity;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.ProdutosEntity;
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

@Service // service dos pedidos
@RequiredArgsConstructor // trazemos os contrutores (Lombok)
public class PedidosService {

    private final PedidosRepository pedidoRepository;
    private final ProdutosRepository produtoRepository;
    private final ProdutosService produtosService; // Injetamos a service para usar a lógica de estoque
    private final PedidosMapper mapper;

    @Transactional
    @CacheEvict(value = {"produtos", "pedidos"}, allEntries = true) // Limpa o "post-it" (Redis) porque o estoque mudou e tem pedido novo
    public PedidosDTOResponse criarPedido(PedidosDTORequest dto) { // criando um pedido

        // 1. Criar a entidade do Pedido e definir o básico
        PedidosEntity pedido = new PedidosEntity(); // instanciando um pedido novo
        pedido.setCliente(dto.getCliente()); // colocando esse pedido no nome do cliente
        pedido.setDataPedido(LocalDateTime.now()); // gravando a hora que o pedido foi feito

        // 2. Buscar os produtos no banco e aplicar a REGRA DE OURO
        Set<ProdutosEntity> produtosDoPedido =
                dto.getProdutosIds() // pegando só os IDs que vieram no Request
                        .stream() // colocando os IDs na esteira
                        .map(id -> { // passando por cada ID com o map

                            // REGRA: Chama o outro service para tirar 1 do estoque
                            // Se não tiver estoque, ele já trava tudo e lança o erro de conflito
                            produtosService.atualizarEstoque(id, -1);

                            // Depois de baixar o estoque, pega o produto completo no banco
                            return produtoRepository.findById(id).get();
                        })
                        .collect(Collectors.toSet()); // tira da esteira e joga dentro de um Set (caixa que não repete)

        // 3. Vincular os produtos ao pedido e salvar
        pedido.setProdutos(produtosDoPedido); // pegando os produtos da esteira e colocando no pedido

        // Salvando o pedido no banco (Postgres)
        PedidosEntity pedidoSalvo = pedidoRepository.save(pedido);

        // 4. Retornar o Response formatado
        return mapper.toResponseDto(pedidoSalvo); // voltamos para o usuário como DTO (Response)
    }

    @Cacheable(value = "pedidos") // busca no Redis pra ser mais rápido
    public List<PedidosDTOResponse> listarTodos() {
        System.out.println("Buscando pedidos no banco...");

        return pedidoRepository.findAll().stream() // coloca os pedidos na esteira
                .map(mapper::toResponseDto) // transforma em DTO de saída
                .collect(Collectors.toList()); // joga tudo numa lista
    }
}