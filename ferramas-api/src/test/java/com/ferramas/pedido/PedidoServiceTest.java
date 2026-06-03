package com.ferramas.pedido;

import com.ferramas.pedido.model.Pedido;
import com.ferramas.pedido.repository.PedidoRepository;
import com.ferramas.pedido.service.PedidoService;
import com.ferramas.usuario.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CP-PED-001 al CP-PED-008
 * Pruebas unitarias del servicio de Pedidos (RF05, RF07, RF09)
 * Cubre: carrito/pedido, selección de entrega, aprobación/rechazo por vendedor.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CP-PED | Servicio de Pedidos - Pruebas Unitarias")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario cliente;
    private Pedido pedidoPendiente;
    private Pedido pedidoAprobado;
    private Pedido pedidoRechazado;
    private Pedido pedidoDespacho;

    @BeforeEach
    void setUp() {
        cliente = new Usuario();
        cliente.setIdUsuario(1L);
        cliente.setNombre("Cliente Ferramas");
        cliente.setTipoUsuario("cliente");

        pedidoPendiente = new Pedido();
        pedidoPendiente.setIdPedido(1L);
        pedidoPendiente.setUsuario(cliente);
        pedidoPendiente.setMetodoPago("debito");
        pedidoPendiente.setEstado("pendiente");
        pedidoPendiente.setRetiroEnTienda(true);
        pedidoPendiente.setFecha(LocalDateTime.now());

        pedidoAprobado = new Pedido();
        pedidoAprobado.setIdPedido(2L);
        pedidoAprobado.setUsuario(cliente);
        pedidoAprobado.setMetodoPago("credito");
        pedidoAprobado.setEstado("aprobado");
        pedidoAprobado.setRetiroEnTienda(false);

        pedidoRechazado = new Pedido();
        pedidoRechazado.setIdPedido(4L);
        pedidoRechazado.setUsuario(cliente);
        pedidoRechazado.setMetodoPago("paypal");
        pedidoRechazado.setEstado("rechazado");
        pedidoRechazado.setRetiroEnTienda(false);

        pedidoDespacho = new Pedido();
        pedidoDespacho.setIdPedido(3L);
        pedidoDespacho.setUsuario(cliente);
        pedidoDespacho.setMetodoPago("transferencia");
        pedidoDespacho.setEstado("despachado");
        pedidoDespacho.setRetiroEnTienda(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PED-001 (RF05): Listar todos los pedidos
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PED-001 | RF05: Listar pedidos retorna todos los estados del sistema")
    void listarPedidos_debeRetornarTodosLosEstados() {
        when(pedidoRepository.findAll())
            .thenReturn(List.of(pedidoPendiente, pedidoAprobado, pedidoRechazado, pedidoDespacho));

        List<Pedido> resultado = pedidoService.listar();

        assertEquals(4, resultado.size());
        assertTrue(resultado.stream().anyMatch(p -> "pendiente".equals(p.getEstado())));
        assertTrue(resultado.stream().anyMatch(p -> "aprobado".equals(p.getEstado())));
        assertTrue(resultado.stream().anyMatch(p -> "rechazado".equals(p.getEstado())));
        assertTrue(resultado.stream().anyMatch(p -> "despachado".equals(p.getEstado())));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PED-002 (RF05): Crear pedido nuevo con carrito
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PED-002 | RF05: Crear pedido nuevo (carrito confirmado) se guarda correctamente")
    void guardarPedido_nuevoPedido_debeGuardarseCorrectamente() {
        Pedido nuevo = new Pedido();
        nuevo.setUsuario(cliente);
        nuevo.setMetodoPago("debito");
        nuevo.setEstado("pendiente");
        nuevo.setRetiroEnTienda(false);

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(nuevo);

        Pedido resultado = pedidoService.guardar(nuevo);

        assertNotNull(resultado);
        assertEquals("pendiente", resultado.getEstado());
        verify(pedidoRepository, times(1)).save(nuevo);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PED-003 (RF07): Pedido con retiro en tienda
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PED-003 | RF07: Pedido con retiroEnTienda=true se guarda correctamente")
    void guardarPedido_retiroEnTienda_debeGuardarFlag() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPendiente);

        Pedido resultado = pedidoService.guardar(pedidoPendiente);

        assertTrue(resultado.getRetiroEnTienda(), "retiroEnTienda debe ser true (RF07)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PED-004 (RF07): Pedido con despacho a domicilio
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PED-004 | RF07: Pedido con despacho a domicilio tiene retiroEnTienda=false")
    void guardarPedido_despachoADomicilio_retiroEnTiendaDebeSeFalse() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoAprobado);

        Pedido resultado = pedidoService.guardar(pedidoAprobado);

        assertFalse(resultado.getRetiroEnTienda(), "Despacho a domicilio: retiroEnTienda=false (RF07)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PED-005 (RF09): Vendedor aprueba pedido pendiente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PED-005 | RF09: Vendedor aprueba pedido - estado cambia a 'aprobado'")
    void actualizarPedido_vendedorAprueba_estadoCambiaAAprobado() {
        Pedido datosAprobacion = new Pedido();
        datosAprobacion.setEstado("aprobado");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.actualizarPedido(1L, datosAprobacion);

        assertEquals("aprobado", resultado.getEstado(),
            "El vendedor debe poder cambiar estado de 'pendiente' a 'aprobado' (RF09)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PED-006 (RF09): Vendedor rechaza pedido pendiente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PED-006 | RF09: Vendedor rechaza pedido - estado cambia a 'rechazado'")
    void actualizarPedido_vendedorRechaza_estadoCambiaARechazado() {
        Pedido datosRechazo = new Pedido();
        datosRechazo.setEstado("rechazado");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.actualizarPedido(1L, datosRechazo);

        assertEquals("rechazado", resultado.getEstado(),
            "El vendedor debe poder rechazar pedidos (RF09)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PED-007: Buscar pedido por ID existente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PED-007 | Buscar pedido por ID existente retorna datos correctos")
    void buscarPorId_pedidoExistente_retornaDatosCorrectos() {
        when(pedidoRepository.findById(2L)).thenReturn(Optional.of(pedidoAprobado));

        Optional<Pedido> resultado = pedidoService.buscarPorId(2L);

        assertTrue(resultado.isPresent());
        assertEquals("credito", resultado.get().getMetodoPago());
        assertEquals("aprobado", resultado.get().getEstado());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PED-008: Actualizar pedido inexistente → RuntimeException
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PED-008 | Actualizar pedido inexistente debe lanzar RuntimeException")
    void actualizarPedido_idInexistente_debeLanzarExcepcion() {
        Pedido datos = new Pedido();
        datos.setEstado("aprobado");

        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> pedidoService.actualizarPedido(999L, datos),
            "Debe lanzar RuntimeException cuando el pedido no existe"
        );
    }
}
