package com.ferramas.inventario.service;

import com.ferramas.inventario.model.Inventario;
import com.ferramas.inventario.repository.InventarioRepository;
import com.ferramas.producto.model.Producto;
import com.ferramas.producto.repository.ProductoRepository;
import com.ferramas.sucursal.model.Sucursal;
import com.ferramas.sucursal.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    public List<Inventario> listarInventario() {
        return inventarioRepository.findAll();
    }

    public Optional<Inventario> buscarPorId(Long id) {
        return inventarioRepository.findById(id);
    }

    public Inventario guardar(Inventario inventario) {
        Inventario nuevo = inventarioRepository.save(inventario);

        Producto productoCompleto = productoRepository.findById(inventario.getProducto().getIdProducto()).orElse(null);
        Sucursal sucursalCompleta = sucursalRepository.findById(inventario.getSucursal().getIdSucursal()).orElse(null);

        nuevo.setProducto(productoCompleto);
        nuevo.setSucursal(sucursalCompleta);

        return nuevo;
    }

    public Inventario actualizarInventario(Long id, Inventario datos) {
        return inventarioRepository.findById(id).map(inv -> {
            inv.setProducto(datos.getProducto());
            inv.setSucursal(datos.getSucursal());
            inv.setStock(datos.getStock());

            Inventario actualizado = inventarioRepository.save(inv);

            Producto productoCompleto = productoRepository.findById(datos.getProducto().getIdProducto()).orElse(null);
            Sucursal sucursalCompleta = sucursalRepository.findById(datos.getSucursal().getIdSucursal()).orElse(null);

            actualizado.setProducto(productoCompleto);
            actualizado.setSucursal(sucursalCompleta);

            return actualizado;
        }).orElseThrow(() -> new RuntimeException("Inventario no encontrado con ID: " + id));
    }

    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }
}
