package com.almacen.ferramas.controller;

import com.almacen.ferramas.dto.ProductoDTO;
import com.almacen.ferramas.entity.Producto;
import com.almacen.ferramas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos/")
public class ProductoController {
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping// obtener todos los productos
    public List<Producto> obtenerProductos(){
        return productoRepository.findAll();
    }

    @GetMapping("/{id_producto}")
    public Producto buscarProductoPorId(@PathVariable Integer id_producto){
        return productoRepository.findById(id_producto)
                .orElseThrow(()-> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Producto no encontrado con ID" + id_producto));

    }


    //@GetMapping // obtener todos los productos DTO
    //public List<ProductoDTO> leerProductosDTO(){
      //  List<Producto> productos = productoRepository.findAll();
       // List<ProductoDTO> productoDTO = new ArrayList<>();
        //for (Producto prod : productos){
        //    productoDTO.add(new ProductoDTO( prod.getNombre(), prod.getDescripcion(),prod.getPrecio(),prod.getId_marca()));
        //}
        //return productoDTO;
    //}

    @PostMapping // añadir productos
    public Producto crearProducto(@RequestBody Producto producto){
        return productoRepository.save(producto);
    }

    @PutMapping ("/{id}")
    public Producto reemplazarProducto(@PathVariable int id, @RequestBody Producto productoActualizado){
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoActualizado.getNombre());
            producto.setDescripcion(productoActualizado.getDescripcion());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setId_marca(productoActualizado.getId_marca());
            return productoRepository.save(producto);
        }).orElseGet(() ->{
            productoActualizado.setId_producto(id);
            return productoRepository.save(productoActualizado);
        });
    }

    @DeleteMapping ("/{id}")
    public void eliminarProducto(@PathVariable int id){
        productoRepository.deleteById(id);
    }




}
