package com.ferramas.producto.repository;

import com.ferramas.producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {}