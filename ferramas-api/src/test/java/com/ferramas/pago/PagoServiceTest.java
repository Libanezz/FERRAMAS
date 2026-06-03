package com.ferramas.pago;

import com.ferramas.detallepedido.repository.DetallePedidoRepository;
import com.ferramas.pago.model.Pago;
import com.ferramas.pago.repository.PagoRepository;
import com.ferramas.pago.service.PagoService;
import com.ferramas.pedido.model.Pedido;
import com.ferramas.pedido.repository.PedidoRepository;
import com.ferramas.usuario.model.Usuario;
import com.ferramas.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CP-PAGO-001 al CP-PAGO-010
 * Pruebas unitarias del servicio de Pagos (RF08, RF13)
 * Cubre: confirmación de transferencias, validación de rol CONTADOR,
 * reglas de negocio críticas de pago.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CP-PAGO | Servicio de Pagos - Pruebas Unitarias")
class PagoServiceTest {

    @Mock private PagoRepository pagoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private DetallePedidoRepository detallePedidoRepository;

    @InjectMocks
    private PagoService pagoService;

    private Usuario contador;
    private Usuario vendedor;
    private Pedido pedidoTransferencia;
    private Pedido pedidoDebito;

    @BeforeEach
    void setUp() {
        // Usuario CONTADOR (id=5 según BD real)
        contador = new Usuario();
        contador.setIdUsuario(5L);
        contador.setNombre("Contador Ferramas");
        contador.setTipoUsuario("CONTADOR");

        // Usuario VENDEDOR (no puede confirmar pagos)
        vendedor = new Usuario();
        vendedor.setIdUsuario(3L);
        vendedor.setNombre("Vendedor Ferramas");
        vendedor.setTipoUsuario("VENDEDOR");

        // Pedido con método TRANSFERENCIA
        pedidoTransferencia = new Pedido();
        pedidoTransferencia.setIdPedido(3L);
        pedidoTransferencia.setMetodoPago("TRANSFERENCIA");
        pedidoTransferencia.setEstado("aprobado");

        // Pedido con método DEBITO
        pedidoDebito = new Pedido();
        pedidoDebito.setIdPedido(1L);
        pedidoDebito.setMetodoPago("debito");
        pedidoDebito.setEstado("pendiente");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-001: Listar todos los pagos
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-001 | Listar pagos retorna lista no nula")
    void listarPagos_debeRetornarLista() {
        Pago p1 = new Pago(1L, pedidoDebito, LocalDateTime.now(), true, contador);
        when(pagoRepository.findAll()).thenReturn(List.of(p1));

        List<Pago> resultado = pagoService.listar();

        assertNotNull(resultado, "La lista de pagos no debe ser nula");
        assertEquals(1, resultado.size());
        verify(pagoRepository, times(1)).findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-002: Buscar pago por ID existente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-002 | Buscar pago por ID existente retorna Optional con valor")
    void buscarPorId_idExistente_retornaOptional() {
        Pago pago = new Pago(1L, pedidoDebito, LocalDateTime.now(), false, null);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        Optional<Pago> resultado = pagoService.buscarPorId(1L);

        assertTrue(resultado.isPresent(), "Debe encontrar el pago con ID=1");
        assertEquals(1L, resultado.get().getIdPago());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-003: Buscar pago por ID inexistente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-003 | Buscar pago por ID inexistente retorna Optional vacío")
    void buscarPorId_idInexistente_retornaVacio() {
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pago> resultado = pagoService.buscarPorId(999L);

        assertFalse(resultado.isPresent(), "No debe encontrar pago con ID=999");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-004 (RF13): Confirmar pago TRANSFERENCIA por CONTADOR → ÉXITO
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-004 | RF13: Contador confirma pago por TRANSFERENCIA - debe guardar OK")
    void guardar_pagoTransferenciaConContador_debeGuardarExitosamente() {
        Pago pago = new Pago();
        pago.setConfirmado(true);
        pago.setPedido(pedidoTransferencia);
        pago.setConfirmadoPor(contador);

        when(pedidoRepository.findById(3L)).thenReturn(Optional.of(pedidoTransferencia));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(contador));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Pago resultado = pagoService.guardar(pago);

        assertNotNull(resultado);
        assertTrue(resultado.isConfirmado());
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-005 (RF13): Confirmar pago con método DÉBITO → EXCEPCIÓN
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-005 | RF13: Confirmar pago DÉBITO debe lanzar excepción - solo TRANSFERENCIA es confirmable")
    void guardar_pagoDebitoConfirmado_debeLanzarExcepcion() {
        Pago pago = new Pago();
        pago.setConfirmado(true);
        pago.setPedido(pedidoDebito); // método = "debito"
        pago.setConfirmadoPor(contador);

        // El pedido no tiene metodoPago en memoria, debe consultar BD
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDebito));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> pagoService.guardar(pago),
            "Debe rechazar confirmación de pago que no sea TRANSFERENCIA"
        );

        assertTrue(ex.getMessage().contains("TRANSFERENCIA"),
            "El mensaje de error debe mencionar TRANSFERENCIA");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-006 (RF13): Confirmar pago TRANSFERENCIA con usuario VENDEDOR → EXCEPCIÓN
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-006 | RF13: Vendedor NO puede confirmar pago - solo CONTADOR puede")
    void guardar_confirmadorNoEsContador_debeLanzarExcepcion() {
        Pago pago = new Pago();
        pago.setConfirmado(true);
        pago.setPedido(pedidoTransferencia);
        pago.setConfirmadoPor(vendedor); // VENDEDOR intenta confirmar

        when(pedidoRepository.findById(3L)).thenReturn(Optional.of(pedidoTransferencia));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(vendedor));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> pagoService.guardar(pago),
            "Solo CONTADOR puede confirmar - VENDEDOR debe ser rechazado"
        );

        assertTrue(ex.getMessage().contains("CONTADOR"),
            "El mensaje debe indicar que se requiere rol CONTADOR");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-007 (RF13): Confirmar pago sin indicar confirmador → EXCEPCIÓN
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-007 | RF13: Confirmar pago sin indicar quién confirma debe lanzar excepción")
    void guardar_confirmadoSinConfirmador_debeLanzarExcepcion() {
        Pago pago = new Pago();
        pago.setConfirmado(true);
        pago.setPedido(pedidoTransferencia);
        pago.setConfirmadoPor(null); // Sin confirmador

        when(pedidoRepository.findById(3L)).thenReturn(Optional.of(pedidoTransferencia));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> pagoService.guardar(pago)
        );

        assertNotNull(ex.getMessage());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-008: Pago no confirmado (débito/crédito) se guarda sin validar rol
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-008 | Pago no confirmado (débito) se guarda sin restricciones de rol")
    void guardar_pagoNoConfirmado_debeGuardarSinValidarRol() {
        Pago pago = new Pago();
        pago.setConfirmado(false); // No requiere validación
        pago.setPedido(pedidoDebito);
        pago.setConfirmadoPor(null);

        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDebito));

        Pago resultado = pagoService.guardar(pago);

        assertNotNull(resultado);
        assertFalse(resultado.isConfirmado());
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-009: Actualizar pago existente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-009 | Actualizar pago existente debe retornar pago actualizado")
    void actualizarPago_idExistente_debeActualizar() {
        Pago pagoExistente = new Pago(1L, pedidoDebito, LocalDateTime.now(), false, null);
        Pago datosNuevos = new Pago();
        datosNuevos.setPedido(pedidoDebito);
        datosNuevos.setConfirmado(false);
        datosNuevos.setFecha(LocalDateTime.now());
        datosNuevos.setConfirmadoPor(null);

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoExistente));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoExistente);

        Pago resultado = pagoService.actualizarPago(1L, datosNuevos);

        assertNotNull(resultado);
        verify(pagoRepository).save(any(Pago.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-PAGO-010: Actualizar pago con ID inexistente → RuntimeException
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-PAGO-010 | Actualizar pago con ID inexistente debe lanzar RuntimeException")
    void actualizarPago_idInexistente_debeLanzarRuntimeException() {
        Pago datos = new Pago();
        datos.setPedido(pedidoDebito);
        datos.setConfirmado(false);
        datos.setConfirmadoPor(null);

        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> pagoService.actualizarPago(999L, datos),
            "Debe lanzar excepción cuando el pago no existe"
        );
    }
}
