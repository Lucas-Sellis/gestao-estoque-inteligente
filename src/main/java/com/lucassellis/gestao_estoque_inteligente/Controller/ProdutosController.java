package com.lucassellis.gestao_estoque_inteligente.Controller; // Ajustei o pacote para a pasta certa

import com.lucassellis.gestao_estoque_inteligente.Business.Service.ProdutosService;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.ProdutosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.ProdutosDTOResponse;
import io.swagger.v3.oas.annotations.tags.Tag; // Import correto do Swagger
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos") // Endereço da "loja": localhost:8080/produtos
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento de estoque e catálogo")
public class ProdutosController {

    private final ProdutosService service;

    @PostMapping // Porta para Criar
    public ResponseEntity<ProdutosDTOResponse> criar(@RequestBody @Valid ProdutosDTORequest dto) {
        // Retorna 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping // Porta para Listar Todos
    public ResponseEntity<List<ProdutosDTOResponse>> listar() {
        // Retorna 200 OK
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}") // Porta para buscar um específico
    public ResponseEntity<ProdutosDTOResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}") // Porta para atualizar (mudar preço, nome, etc)
    public ResponseEntity<ProdutosDTOResponse> atualizarEstoque(@PathVariable Long id, @RequestBody @Valid ProdutosDTORequest dto) {
        // Aqui usamos o DTORequest porque o usuário está enviando dados NOVOS
        return ResponseEntity.ok(service.atualizarEstoque(id, dto)); // problema aqui no dto
    }

    @PatchMapping("/{id}/estoque") // Porta específica para mexer SÓ na quantidade
    public ResponseEntity<ProdutosDTOResponse> atualizarEstoque(@PathVariable Long id, @RequestParam Integer quantidade) {
        // Usamos o @RequestParam para mandar o número direto na URL ex: /estoque?quantidade=-5
        return ResponseEntity.ok(service.atualizarEstoque(id, quantidade));
    }

    @DeleteMapping("/{id}") // Porta para Deletar
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        // Retorna 204 No Content (sucesso, mas sem corpo de resposta)
        return ResponseEntity.noContent().build();
    }
}