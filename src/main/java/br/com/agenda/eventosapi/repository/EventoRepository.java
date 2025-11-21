package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.dto.ranking.RankingOrganizadorDTO;
import br.com.agenda.eventosapi.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByDataAfter(LocalDateTime data);

    List<Evento> findByCategoriaId(Long categoriaId);

    List<Evento> findByDataAfterAndCategoriaId(LocalDateTime data, Long categoriaId);

    List<Evento> findAllByDataBetween(LocalDateTime inicio, LocalDateTime fim);

    Page<Evento> findAllByDataAfter(LocalDateTime data, Pageable pageable);
    Page<Evento> findByOrganizadorId(Long organizadorId, Pageable pageable);
    long countByDataAfter(LocalDateTime data);

    @Query("""
        SELECT new br.com.agenda.eventosapi.dto.ranking.RankingOrganizadorDTO(o.nome, COUNT(e))
        FROM Evento e JOIN e.organizador o
        GROUP BY o.id, o.nome
        ORDER BY COUNT(e) DESC
    """)
    List<RankingOrganizadorDTO> findRankingOrganizadores();

    @Query(value = """
        SELECT e.* FROM eventos e
        INNER JOIN enderecos en ON e.endereco_id = en.id
        WHERE
            (6371 * ACOS(
                COS(RADIANS(:userLat)) * COS(RADIANS(en.latitude)) *
                COS(RADIANS(en.longitude) - RADIANS(:userLon)) +
                SIN(RADIANS(:userLat)) * SIN(RADIANS(en.latitude))
            )) < :radius
        AND e.data > NOW() -- Opcional: Adicionado para buscar apenas eventos futuros
        ORDER BY
            (6371 * ACOS(
                COS(RADIANS(:userLat)) * COS(RADIANS(en.latitude)) *
                COS(RADIANS(en.longitude) - RADIANS(:userLon)) +
                SIN(RADIANS(:userLat)) * SIN(RADIANS(en.latitude))
            )) ASC -- Ordena do mais próximo para o mais distante
    """, nativeQuery = true)
    List<Evento> findEventosProximos(
            @Param("userLat") BigDecimal userLat,
            @Param("userLon") BigDecimal userLon,
            @Param("radius") Double radius
    );

    @Query("SELECT e FROM Evento e WHERE " +
            "(:nome IS NULL OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:cidade IS NULL OR LOWER(e.endereco.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))) AND " +
            "(:categoria IS NULL OR e.categoria.nome = :categoria) AND " +
            "(:data IS NULL OR CAST(e.data AS LocalDate) = :data)")
    Page<Evento> encontrarComFiltros( // <-- Retorna Page<Evento>
                                      @Param("nome") String nome,
                                      @Param("cidade") String cidade,
                                      @Param("categoria") String categoria,
                                      @Param("data") LocalDate data,
                                      Pageable pageable);

    @Query("SELECT DISTINCT e.endereco.cidade FROM Evento e WHERE e.endereco.cidade IS NOT NULL ORDER BY e.endereco.cidade ASC")
    List<String> findCidadesDistintas();
}
