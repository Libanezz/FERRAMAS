package com.ferramas.detallepedido.service;

import com.ferramas.detallepedido.model.DetallePedido;
import com.ferramas.detallepedido.repository.DetallePedidoRepository;
import com.ferramas.pedido.model.Pedido;
import com.ferramas.pedido.repository.PedidoRepository;
import com.ferramas.producto.model.Producto;
import com.ferramas.producto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<DetallePedido> listar() {
        return detallePedidoRepository.findAll();
    }

    public Optional<DetallePedido> buscarPorId(Long id) {
        return detallePedidoRepository.findById(id);
    }

    public DetallePedido guardar(DetallePedido detalle) {
        DetallePedido nuevo = detallePedidoRepository.save(detalle);

        Pedido pedidoCompleto = pedidoRepository.findById(detalle.getPedido().getIdPedido()).orElse(null);
        Producto productoCompleto = productoRepository.findById(detalle.getProducto().getIdProducto()).orElse(null);

        nuevo.setPedido(pedidoCompleto);
        nuevo.setProducto(productoCompleto);

        return nuevo;
    }

    public DetallePedido actualizarDetalle(Long id, DetallePedido datos) {
        return detallePedidoRepository.findById(id).map(d -> {
            d.setPedido(datos.getPedido());
            d.setProducto(datos.getProducto());
            d.setCantidad(datos.getCantidad());
            d.setPrecioUnitario(datos.getPrecioUnitario());

            DetallePedido actualizado = detallePedidoRepository.save(d);

            Pedido pedidoCompleto = pedidoRepository.findById(datos.getPedido().getIdPedido()).orElse(null);
            Producto productoCompleto = productoRepository.findById(datos.getProducto().getIdProducto()).orElse(null);

            actualizado.setPedido(pedidoCompleto);
            actualizado.setProducto(productoCompleto);

            return actualizado;
        }).orElseThrow(() -> new RuntimeException("Detalle no encontrado con ID: " + id));
    }

    public void eliminar(Long id) {
        detallePedidoRepository.deleteById(id);
    }
}
