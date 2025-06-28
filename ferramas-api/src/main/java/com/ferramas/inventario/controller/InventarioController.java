package com.ferramas.inventario.controller;

import com.ferramas.inventario.model.Inventario;
import com.ferramas.inventario.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public List<Inventario> listar() {
        return inventarioService.listarInventario();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtener(@PathVariable Long id) {
        return inventarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Inventario crear(@RequestBody Inventario inventario) {
        return inventarioService.guardar(inventario);
    }

    @PutMapping("/{id}")
    public Inventario actualizar(@PathVariable Long id, @RequestBody Inventario inventario) {
        return inventarioService.actualizarInventario(id, inventario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}