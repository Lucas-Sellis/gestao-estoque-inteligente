package com.lucassellis.gestao_estoque_inteligente.Controller;

import com.lucassellis.gestao_estoque_inteligente.Business.Service.PedidosService;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.in.PedidosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.PedidosDTOResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gerenciamento de vendas e movimentação de estoque")
public class PedidosController {

    private final PedidosService service;

    @PostMapping
    @Operation(summary = "Realizar novo pedido", description = "Cria um pedido e abate automaticamente 1 unidade de cada produto no estoque.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido realizado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Erro: Um ou mais produtos estão sem estoque"),
            @ApiResponse(responseCode = "404", description = "Erro: Produto informado não existe")
    })
    public ResponseEntity<PedidosDTOResponse> criar(@RequestBody @Valid PedidosDTORequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarPedido(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os pedidos", description = "Retorna o histórico de pedidos. Cacheado via Redis.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    public ResponseEntity<List<PedidosDTOResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna os detalhes de um pedido e os produtos vinculados.")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    public ResponseEntity<PedidosDTOResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Permite alterar o cliente ou os produtos. O sistema recalcula o estoque automaticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Erro: Estoque insuficiente para os novos produtos")
    })
    public ResponseEntity<PedidosDTOResponse> atualizar(@PathVariable Long id, @RequestBody @Valid PedidosDTORequest dto) {
        return ResponseEntity.ok(service.atualizarPedido(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar/Deletar pedido", description = "Remove o pedido e devolve os produtos ao estoque.")
    @ApiResponse(responseCode = "204", description = "Pedido deletado e estoque devolvido")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }
}