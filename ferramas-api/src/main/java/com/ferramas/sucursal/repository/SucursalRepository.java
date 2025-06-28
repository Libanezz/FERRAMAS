package com.ferramas.sucursal.repository;

import com.ferramas.sucursal.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {}