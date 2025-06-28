package com.ferramas.producto.service;

import com.ferramas.producto.model.Producto;
import com.ferramas.producto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto datosNuevos) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(datosNuevos.getNombre());
            producto.setDescripcion(datosNuevos.getDescripcion());
            producto.setPrecio(datosNuevos.getPrecio());
            producto.setUrlImagen(datosNuevos.getUrlImagen());
            producto.setEstado(datosNuevos.getEstado());
            producto.setMarca(datosNuevos.getMarca());
            return productoRepository.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }


    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}