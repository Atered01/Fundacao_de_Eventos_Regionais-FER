package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByDataAfter(LocalDateTime data);

    List<Evento> findByCategoriaId(Long categoriaId);

    List<Evento> findByDataAfterAndCategoriaId(LocalDateTime data, Long categoriaId);

    Page<Evento> findAllByDataAfter(LocalDateTime data, Pageable pageable);

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
}
