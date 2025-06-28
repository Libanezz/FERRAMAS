package com.almacen.ferramas.repository;
import com.almacen.ferramas.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
