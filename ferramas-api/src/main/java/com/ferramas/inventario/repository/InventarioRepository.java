package com.ferramas.inventario.repository;

import com.ferramas.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {}