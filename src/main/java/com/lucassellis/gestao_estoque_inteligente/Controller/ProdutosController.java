package com.lucassellis.gestao_estoque_inteligente.Controller;

import com.lucassellis.gestao_estoque_inteligente.Business.Service.ProdutosService;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.ProdutosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.ProdutosDTOResponse;
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
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento de estoque e catálogo de produtos")
public class ProdutosController {

    private final ProdutosService service;

    @PostMapping
    @Operation(summary = "Cadastrar produto", description = "Cria um novo produto no catálogo e limpa o cache de listagem.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito: SKU ou Nome já existente"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<ProdutosDTOResponse> criar(@RequestBody @Valid ProdutosDTORequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos. Utiliza cache Redis para performance.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<ProdutosDTOResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um produto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutosDTOResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza todos os dados de um produto (Nome, Preço, SKU).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutosDTOResponse> atualizar(@PathVariable Long id, @RequestBody @Valid ProdutosDTORequest dto) {
        return ResponseEntity.ok(service.atualizarEstoque(id, dto));
    }

    @PatchMapping("/{id}/estoque")
    @Operation(summary = "Ajustar quantidade em estoque", description = "Soma ou subtrai itens do estoque atual. Use valores negativos para saídas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque ajustado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Erro: Estoque insuficiente"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutosDTOResponse> ajustarQuantidade(@PathVariable Long id, @RequestParam Integer quantidade) {
        return ResponseEntity.ok(service.atualizarEstoque(id, quantidade));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema se não houver pedidos vinculados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "409", description = "Erro: Produto possui vínculos e não pode ser deletado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}