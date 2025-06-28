package com.ferramas.pago.controller;

import com.ferramas.pago.dto.PagoDTO;
import com.ferramas.pago.model.Pago;
import com.ferramas.pago.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public List<PagoDTO> listar() {
        return pagoService.listar()
                .stream()
                .map(pagoService::mapearPagoDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtener(@PathVariable Long id) {
        return pagoService.buscarPorId(id)
                .map(pagoService::mapearPagoDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pago crear(@RequestBody Pago pago) {
        return pagoService.guardar(pago);
    }

    @PutMapping("/{id}")
    public Pago actualizar(@PathVariable Long id, @RequestBody Pago pago) {
        return pagoService.actualizarPago(id, pago);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
