package com.lucassellis.gestao_estoque_inteligente.Business.Service;

import com.lucassellis.gestao_estoque_inteligente.Business.Mapper.ProdutosMapper;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.ProdutosDTORequest;
import com.lucassellis.gestao_estoque_inteligente.Business.dto.out.ProdutosDTOResponse;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.entity.ProdutosEntity;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.exceptions.ConflictException;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.exceptions.ResourceNotFoundException;
import com.lucassellis.gestao_estoque_inteligente.Infrastructure.repository.ProdutosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutosService {

    private final ProdutosRepository repository;

    private final ProdutosMapper mapper;

    /**
     * @CacheEvict: "Limpa a mesa". Quando você cria um produto novo, a lista antiga no Redis
     * fica desatualizada. Essa anotação avisa ao Redis para apagar a lista antiga e
     * forçar o sistema a buscar a nova no próximo "listar()".
     */
    @CacheEvict(value = "produtos", allEntries = true)
    public ProdutosDTOResponse criar(ProdutosDTORequest dto) {

        try {
            // Converte o DTO que chegou da API para a Entity que o banco entende
            ProdutosEntity entity = mapper.toEntity(dto);

            // Regra de Negócio: Padroniza o SKU para sempre ser salvo em MAIÚSCULO
            if (entity.getSku() != null) {
                entity.setSku(entity.getSku().toUpperCase());
            }

            // Salva no PostgreSQL e converte o resultado de volta para DTO (Response)
            ProdutosEntity salva = repository.save(entity);
            return mapper.toResponseDto(salva);

        } catch (DataIntegrityViolationException e) {
            // Tratamento para evitar duplicidade de nomes ou SKUs únicos
            throw new ConflictException("Esse produto (SKU ou Nome) já existe no sistema.");
        }
    }

    /**
     * @Cacheable: "Busca na mesa". Antes de ir no banco (Postgres), o Spring olha se a
     * lista já está no Redis. Se estiver, ele devolve na hora (muito mais rápido).
     */
    @Cacheable(value = "produtos")
    public List<ProdutosDTOResponse> listar() {
        // Se você ver essa mensagem no console, significa que o Redis estava vazio
        System.out.println("Buscando no banco de dados (Postgres)...");

        return repository.findAll().stream() // Abre a "esteira" com todas as entidades
                .map(mapper::toResponseDto)   // Transforma cada Entity em DTO
                .collect(Collectors.toList()); // Fecha a esteira em uma Lista
    }

    /**
     * @Transactional: Garante que, se algo der errado na conta (ex: estoque negativo),
     * nada seja salvo no banco. Ou faz tudo, ou não faz nada.
     */
    @Transactional
    @CacheEvict(value = "produtos", allEntries = true)
    public ProdutosDTOResponse atualizarEstoque(Long id, ProdutosDTORequest dto) {

        ProdutosEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        int novoEstoque = entity.getQuantidadeEstoque() + dto.getQuantidadeAlterada();

        if (novoEstoque < 0) {
            throw new ConflictException("Estoque insuficiente para realizar essa operação.");
        }

        entity.setQuantidadeEstoque(novoEstoque);

        return mapper.toResponseDto(repository.save(entity));
    }

    // MÉTODO AUXILIAR (ATALHO)
// Aqui eu continuo podendo chamar com número direto (ex: -1)
// Ele transforma o número em DTO e reaproveita o método principal
    public ProdutosDTOResponse atualizarEstoque(Long id, Integer quantidadeAlterada) {

        ProdutosDTORequest dto = new ProdutosDTORequest();
        dto.setQuantidadeAlterada(quantidadeAlterada);

        return atualizarEstoque(id, dto);
    }

    @Cacheable(value = "produtos", key = "#id") // "Anota no post-it" usando o ID como chave
    public ProdutosDTOResponse buscarPorId(Long id) {
        // 1. O repository busca no Postgres e devolve um Optional (pode ter ou não o produto)
        return repository.findById(id)

                // 2. Se achou, a "esteira" (map) pega a Entity e transforma no DTO de saída (Response)
                .map(mapper::toResponseDto)

                // 3. Se a esteira estiver vazia (não achou o ID), lança o nosso erro personalizado
                .orElseThrow(() -> new ResourceNotFoundException("Produto com ID " + id + " não encontrado."));
    }

    @Transactional
    public void deletar(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não existe.");
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // IMPORTANTE: Se tiver produtos dentro da categoria, o banco não deixa deletar.
            throw new ConflictException("Não é possível deletar: existem produtos vinculados.");
        }
    }


}