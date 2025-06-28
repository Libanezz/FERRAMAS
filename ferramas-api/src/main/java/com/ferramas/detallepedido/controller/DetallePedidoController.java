package com.ferramas.detallepedido.controller;

import com.ferramas.detallepedido.model.DetallePedido;
import com.ferramas.detallepedido.service.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles")
@CrossOrigin(origins = "*")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    @GetMapping
    public List<DetallePedido> listar() {
        return detallePedidoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedido> obtener(@PathVariable Long id) {
        return detallePedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DetallePedido crear(@RequestBody DetallePedido detalle) {
        return detallePedidoService.guardar(detalle);
    }

    @PutMapping("/{id}")
    public DetallePedido actualizar(@PathVariable Long id, @RequestBody DetallePedido detalle) {
        return detallePedidoService.actualizarDetalle(id, detalle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detallePedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}