package com.lucassellis.gestao_estoque_inteligente.Infrastructure.controller;

import com.lucassellis.gestao_estoque_inteligente.Business.Service.PedidosService;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.in.PedidosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.PedidosDTOResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidosController {

    private final PedidosService service;

    @PostMapping
    public ResponseEntity<PedidosDTOResponse> criar(@RequestBody @Valid PedidosDTORequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarPedido(dto));
    }

    @GetMapping
    public ResponseEntity<List<PedidosDTOResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidosDTOResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}") // A porta que estava dando erro 405!
    public ResponseEntity<PedidosDTOResponse> atualizar(@PathVariable Long id, @RequestBody @Valid PedidosDTORequest dto) {
        return ResponseEntity.ok(service.atualizarPedido(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }
}