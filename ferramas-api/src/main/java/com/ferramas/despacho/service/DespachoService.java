package com.ferramas.despacho.service;

import com.ferramas.despacho.model.Despacho;
import com.ferramas.despacho.repository.DespachoRepository;
import com.ferramas.pedido.repository.PedidoRepository;
import com.ferramas.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DespachoService {

    @Autowired
    private DespachoRepository despachoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<Despacho> listar() {
        return despachoRepository.findAll();
    }

    public Optional<Despacho> buscarPorId(Long id) {
        return despachoRepository.findById(id);
    }

    public Despacho guardar(Despacho despacho) {
        Despacho guardado = despachoRepository.save(despacho);

        // Cargar el pedido completo
        guardado.setPedido(
                pedidoRepository.findById(guardado.getPedido().getIdPedido()).orElse(null)
        );

        // Cargar el usuario encargado completo (si existe)
        if (guardado.getEncargado() != null) {
            guardado.setEncargado(
                    usuarioRepository.findById(guardado.getEncargado().getIdUsuario()).orElse(null)
            );
        }

        return guardado;
    }


    public Despacho actualizarDespacho(Long id, Despacho datos) {
        return despachoRepository.findById(id).map(d -> {
            d.setPedido(datos.getPedido());
            d.setEncargado(datos.getEncargado());
            d.setFechaDespacho(datos.getFechaDespacho());
            d.setDireccionEntrega(datos.getDireccionEntrega());
            d.setEntregado(datos.isEntregado());
            return despachoRepository.save(d);
        }).orElseThrow(() -> new RuntimeException("Despacho no encontrado con ID: " + id));
    }


    public void eliminar(Long id) {
        despachoRepository.deleteById(id);
    }
}