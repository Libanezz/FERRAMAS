package com.ferramas.pedido.service;

import com.ferramas.pedido.model.Pedido;
import com.ferramas.pedido.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarPedido(Long id, Pedido datos) {
        return pedidoRepository.findById(id).map(p -> {
            if (datos.getUsuario() != null) {
                p.setUsuario(datos.getUsuario());
            }
            if (datos.getFecha() != null) {
                p.setFecha(datos.getFecha());
            }
            if (datos.getEstado() != null) {
                p.setEstado(datos.getEstado());
            }
            if (datos.getMetodoPago() != null) {
                p.setMetodoPago(datos.getMetodoPago());
            }
            if (datos.getRetiroEnTienda() != null) {
                p.setRetiroEnTienda(datos.getRetiroEnTienda());
            }
            return pedidoRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }



    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }
}