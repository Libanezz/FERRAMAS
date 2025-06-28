package com.ferramas.marca.repository;

import com.ferramas.marca.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {}