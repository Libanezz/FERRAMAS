package com.ferramas.inventario;

import com.ferramas.inventario.model.Inventario;
import com.ferramas.inventario.repository.InventarioRepository;
import com.ferramas.inventario.service.InventarioService;
import com.ferramas.producto.model.Producto;
import com.ferramas.producto.repository.ProductoRepository;
import com.ferramas.sucursal.model.Sucursal;
import com.ferramas.sucursal.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CP-INV-001 al CP-INV-007
 * Pruebas unitarias del servicio de Inventario (RF04, RF10, RF11, RNF05)
 * Cubre: catálogo con stock real, visualización de bodega, integridad de datos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CP-INV | Servicio de Inventario - Pruebas Unitarias")
class InventarioServiceTest {

    @Mock private InventarioRepository inventarioRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private SucursalRepository sucursalRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto taladroBosch;
    private Sucursal sucursalCentro;
    private Inventario inventarioTaladro;

    @BeforeEach
    void setUp() {
        // Producto real de la BD: Taladro Bosch 500W (id=1, precio=59990)
        taladroBosch = new Producto();
        taladroBosch.setIdProducto(1L);
        taladroBosch.setNombre("Taladro Bosch 500W");
        taladroBosch.setPrecio(new BigDecimal("59990"));
        taladroBosch.setDescripcion("Taladro eléctrico profesional");

        // Sucursal real: Centro (id=1)
        sucursalCentro = new Sucursal();
        sucursalCentro.setIdSucursal(1L);
        sucursalCentro.setNombre("Sucursal Centro");

        // Inventario real: taladro en sucursal centro, stock=0
        inventarioTaladro = new Inventario();
        inventarioTaladro.setIdInventario(1L);
        inventarioTaladro.setProducto(taladroBosch);
        inventarioTaladro.setSucursal(sucursalCentro);
        inventarioTaladro.setStock(0); // Stock real en BD = 0
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-INV-001 (RF04/RF10): Listar inventario retorna registros completos
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-INV-001 | RF04/RF10: Listar inventario retorna registros no nulos")
    void listarInventario_debeRetornarListaCompleta() {
        Inventario inv2 = new Inventario();
        inv2.setIdInventario(2L);
        inv2.setStock(20);

        when(inventarioRepository.findAll()).thenReturn(List.of(inventarioTaladro, inv2));

        List<Inventario> resultado = inventarioService.listarInventario();

        assertNotNull(resultado, "La lista de inventario no debe ser nula");
        assertEquals(2, resultado.size());
        verify(inventarioRepository, times(1)).findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-INV-002 (RF10): Buscar inventario por ID retorna datos del producto
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-INV-002 | RF10: Buscar inventario por ID retorna producto y sucursal")
    void buscarPorId_idExistente_retornaInventarioConProducto() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventarioTaladro));

        Optional<Inventario> resultado = inventarioService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Taladro Bosch 500W", resultado.get().getProducto().getNombre());
        assertEquals(0, resultado.get().getStock()); // Stock real = 0
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-INV-003 (RNF05): Stock puede ser 0 (sin bloquear el sistema)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-INV-003 | RNF05: Inventario con stock=0 no bloquea el sistema")
    void buscarPorId_stockCero_noDebeLanzarExcepcion() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventarioTaladro));

        assertDoesNotThrow(() -> {
            Optional<Inventario> resultado = inventarioService.buscarPorId(1L);
            assertTrue(resultado.isPresent());
            assertEquals(0, resultado.get().getStock());
        }, "Stock=0 es válido y no debe lanzar excepción");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-INV-004 (RF11): Crear registro de inventario guarda producto y sucursal
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-INV-004 | RF11: Crear inventario guarda correctamente y enriquece datos")
    void guardar_inventarioNuevo_debeGuardarYEnriquecerProductoYSucursal() {
        Inventario nuevo = new Inventario();
        nuevo.setProducto(taladroBosch);
        nuevo.setSucursal(sucursalCentro);
        nuevo.setStock(15);

        when(inventarioRepository.save(any(Inventario.class))).thenReturn(nuevo);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(taladroBosch));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalCentro));

        Inventario resultado = inventarioService.guardar(nuevo);

        assertNotNull(resultado);
        assertNotNull(resultado.getProducto(), "El producto debe estar enriquecido post-guardado");
        assertNotNull(resultado.getSucursal(), "La sucursal debe estar enriquecida post-guardado");
        assertEquals(15, resultado.getStock());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-INV-005 (RF10): Actualizar stock de inventario existente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-INV-005 | RF10: Actualizar stock de inventario existente")
    void actualizarInventario_stockNuevo_debeActualizar() {
        Inventario datosActualizados = new Inventario();
        datosActualizados.setProducto(taladroBosch);
        datosActualizados.setSucursal(sucursalCentro);
        datosActualizados.setStock(25); // Nuevo stock

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventarioTaladro));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(taladroBosch));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalCentro));

        Inventario resultado = inventarioService.actualizarInventario(1L, datosActualizados);

        assertEquals(25, resultado.getStock(), "El stock debe actualizarse a 25");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-INV-006: Actualizar inventario con ID inexistente → RuntimeException
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-INV-006 | Actualizar inventario inexistente debe lanzar RuntimeException")
    void actualizarInventario_idInexistente_debeLanzarExcepcion() {
        Inventario datos = new Inventario();
        datos.setProducto(taladroBosch);
        datos.setSucursal(sucursalCentro);
        datos.setStock(10);

        when(inventarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> inventarioService.actualizarInventario(999L, datos),
            "Debe lanzar RuntimeException cuando el inventario no existe"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-INV-007: Eliminar inventario llama al repositorio
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-INV-007 | Eliminar inventario invoca deleteById en el repositorio")
    void eliminarInventario_debeInvocarDeleteById() {
        doNothing().when(inventarioRepository).deleteById(1L);

        inventarioService.eliminar(1L);

        verify(inventarioRepository, times(1)).deleteById(1L);
    }
}
