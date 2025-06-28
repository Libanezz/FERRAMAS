package com.ferramas.despacho.controller;

import com.ferramas.despacho.model.Despacho;
import com.ferramas.despacho.service.DespachoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despachos")
@CrossOrigin(origins = "*")
public class DespachoController {

    @Autowired
    private DespachoService despachoService;

    @GetMapping
    public List<Despacho> listar() {
        return despachoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despacho> obtener(@PathVariable Long id) {
        return despachoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Despacho crear(@RequestBody Despacho despacho) {
        return despachoService.guardar(despacho);
    }

    @PutMapping("/{id}")
    public Despacho actualizar(@PathVariable Long id, @RequestBody Despacho despacho) {
        return despachoService.actualizarDespacho(id, despacho);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        despachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}