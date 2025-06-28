package com.ferramas.pago.service;

import com.ferramas.detallepedido.repository.DetallePedidoRepository;
import com.ferramas.pago.dto.PagoDTO;
import com.ferramas.pago.model.Pago;
import com.ferramas.pago.repository.PagoRepository;
import com.ferramas.pedido.model.Pedido;
import com.ferramas.pedido.repository.PedidoRepository;
import com.ferramas.usuario.model.Usuario;
import com.ferramas.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> buscarPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public Pago guardar(Pago pago) {
        validarConfirmador(pago);
        Pago pagoGuardado = pagoRepository.save(pago);

        // Cargar pedido completo si está parcial
        Pedido pedidoReal = pedidoRepository.findById(pagoGuardado.getPedido().getIdPedido())
                .orElse(null);
        pagoGuardado.setPedido(pedidoReal);

        // Cargar usuario confirmador completo si corresponde
        if (pagoGuardado.getConfirmadoPor() != null) {
            Usuario usuarioReal = usuarioRepository.findById(pagoGuardado.getConfirmadoPor().getIdUsuario())
                    .orElse(null);
            pagoGuardado.setConfirmadoPor(usuarioReal);
        }

        return pagoGuardado;
    }


    public Pago actualizarPago(Long id, Pago datos) {
        validarConfirmador(datos);
        return pagoRepository.findById(id).map(p -> {
            p.setPedido(datos.getPedido());
            p.setFecha(datos.getFecha());
            p.setConfirmado(datos.isConfirmado());
            p.setConfirmadoPor(datos.getConfirmadoPor());
            return pagoRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    public void eliminar(Long id) {
        pagoRepository.deleteById(id);
    }

    public PagoDTO mapearPagoDTO(Pago pago) {
        Double montoTotal = detallePedidoRepository
                .findByPedido_IdPedido(pago.getPedido().getIdPedido())
                .stream()
                .mapToDouble(dp -> dp.getCantidad() * dp.getPrecioUnitario())
                .sum();

        String nombreConfirmador = pago.getConfirmadoPor() != null
                ? pago.getConfirmadoPor().getNombre()
                : null;

        return new PagoDTO(
                pago.getIdPago(),
                pago.getPedido().getMetodoPago(),
                montoTotal,
                pago.getFecha() != null ? pago.getFecha().toString() : null,
                pago.isConfirmado(),
                nombreConfirmador
        );
    }

    private void validarConfirmador(Pago pago) {
        if (!pago.isConfirmado()) {
            return; // No validar si el pago no está siendo confirmado
        }

        Pedido pedidoReal = pago.getPedido();
        if (pedidoReal.getMetodoPago() == null) {
            pedidoReal = pedidoRepository.findById(pedidoReal.getIdPedido())
                    .orElseThrow(() -> new RuntimeException("No se encontró el pedido asociado al pago."));
        }

        if (!"TRANSFERENCIA".equalsIgnoreCase(pedidoReal.getMetodoPago())) {
            throw new IllegalArgumentException("Solo los pagos con método TRANSFERENCIA pueden ser confirmados.");
        }

        Usuario confirmador = pago.getConfirmadoPor();
        if (confirmador == null) {
            throw new IllegalArgumentException("Debe indicar quién confirma el pago.");
        }

        Usuario usuarioReal = usuarioRepository.findById(confirmador.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario confirmador no encontrado."));

        if (!"CONTADOR".equalsIgnoreCase(usuarioReal.getTipoUsuario())) {
            throw new IllegalArgumentException("Solo un usuario con tipoUsuario CONTADOR puede confirmar pagos.");
        }
    }
}
