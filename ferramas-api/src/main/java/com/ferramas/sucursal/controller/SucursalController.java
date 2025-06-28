package com.ferramas.sucursal.controller;

import com.ferramas.sucursal.model.Sucursal;
import com.ferramas.sucursal.service.SucursalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@CrossOrigin(origins = "*")
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @GetMapping
    public List<Sucursal> obtenerTodas() {
        return sucursalService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> obtenerPorId(@PathVariable Long id) {
        return sucursalService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Sucursal crear(@RequestBody Sucursal sucursal) {
        return sucursalService.guardar(sucursal);
    }

    @PutMapping("/{id}")
    public Sucursal actualizar(@PathVariable Long id, @RequestBody Sucursal sucursal) {
        return sucursalService.actualizarSucursal(id, sucursal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}