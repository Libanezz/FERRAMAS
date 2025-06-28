package com.ferramas.marca.controller;

import com.ferramas.marca.model.Marca;
import com.ferramas.marca.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@CrossOrigin(origins = "*")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    public List<Marca> obtenerTodas() {
        return marcaService.listarMarcas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca> obtenerPorId(@PathVariable Long id) {
        return marcaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Marca crear(@RequestBody Marca marca) {
        return marcaService.guardarMarca(marca);
    }

    @PutMapping("/{id}")
    public Marca actualizar(@PathVariable Long id, @RequestBody Marca marca) {
        return marcaService.actualizarMarca(id, marca);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        marcaService.eliminarMarca(id);
        return ResponseEntity.noContent().build();
    }
}