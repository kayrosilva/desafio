package com.github.kayrosilva.desafio.model.repository;

import com.github.kayrosilva.desafio.model.entity.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {


    @Query("SELECT c FROM Cliente c WHERE " +
            "(FUNCTION('TIMESTAMPDIFF', YEAR, c.nacimento, CURRENT_DATE) >= :idade AND :tipo = 'maior') OR " +
            "(FUNCTION('TIMESTAMPDIFF', YEAR, c.nacimento, CURRENT_DATE) < :idade AND :tipo = 'menor')")
    Page<Cliente> findAllByIdadeFiltered(@Param("idade") Integer idade,
                                         @Param("tipo") String tipo,
                                         Pageable pageable);
}

