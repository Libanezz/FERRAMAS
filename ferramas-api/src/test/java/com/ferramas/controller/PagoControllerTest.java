package com.ferramas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferramas.pago.controller.PagoController;
import com.ferramas.pago.dto.PagoDTO;
import com.ferramas.pago.model.Pago;
import com.ferramas.pago.service.PagoService;
import com.ferramas.pedido.model.Pedido;
import com.ferramas.usuario.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CP-CTRL-001 al CP-CTRL-008
 * Pruebas de integración con MockMvc para los endpoints REST de Pagos (RF08, RF13)
 * Valida: códigos HTTP, estructura JSON, reglas de negocio a nivel de controlador.
 */
@WebMvcTest(PagoController.class)
@DisplayName("CP-CTRL | Controller de Pagos - Pruebas de Integración MockMvc")
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoService pagoService;

    @Autowired
    private ObjectMapper objectMapper;

    private PagoDTO pagoDTO1;
    private PagoDTO pagoDTO2;
    private Pago pagoEntidad;
    private Pedido pedido;
    private Usuario contador;

    @BeforeEach
    void setUp() {
        pagoDTO1 = new PagoDTO(1L, "debito", 119980.0, "2026-05-16T21:22:40", true, "Contador Ferramas");
        pagoDTO2 = new PagoDTO(2L, "credito", 89990.0, "2026-05-16T21:22:40", true, "Contador Ferramas");

        contador = new Usuario();
        contador.setIdUsuario(5L);
        contador.setNombre("Contador Ferramas");
        contador.setTipoUsuario("CONTADOR");

        pedido = new Pedido();
        pedido.setIdPedido(3L);
        pedido.setMetodoPago("TRANSFERENCIA");

        pagoEntidad = new Pago();
        pagoEntidad.setIdPago(1L);
        pagoEntidad.setPedido(pedido);
        pagoEntidad.setConfirmado(true);
        pagoEntidad.setConfirmadoPor(contador);
        pagoEntidad.setFecha(LocalDateTime.now());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-CTRL-001 (RF08): GET /api/pagos retorna HTTP 200 y lista JSON
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-CTRL-001 | RF08: GET /api/pagos retorna HTTP 200 con lista de pagos")
    void getPagos_debeRetornar200ConListaJson() throws Exception {
        when(pagoService.listar()).thenReturn(List.of(pagoEntidad, new Pago()));
        when(pagoService.mapearPagoDTO(any())).thenReturn(pagoDTO1, pagoDTO2);

        mockMvc.perform(get("/api/pagos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].idPago", is(1)))
            .andExpect(jsonPath("$[0].metodoPago", is("debito")));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-CTRL-002 (RF08): GET /api/pagos/{id} con ID existente retorna HTTP 200
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-CTRL-002 | RF08: GET /api/pagos/1 retorna HTTP 200 con datos del pago")
    void getPagoPorId_idExistente_retorna200() throws Exception {
        when(pagoService.buscarPorId(1L)).thenReturn(Optional.of(pagoEntidad));
        when(pagoService.mapearPagoDTO(any())).thenReturn(pagoDTO1);

        mockMvc.perform(get("/api/pagos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idPago", is(1)))
            .andExpect(jsonPath("$.confirmado", is(true)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-CTRL-003 (RF08): GET /api/pagos/{id} con ID inexistente retorna HTTP 404
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-CTRL-003 | RF08: GET /api/pagos/999 retorna HTTP 404 Not Found")
    void getPagoPorId_idInexistente_retorna404() throws Exception {
        when(pagoService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pagos/999"))
            .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-CTRL-004 (RF13): POST /api/pagos crea pago correctamente → HTTP 200
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-CTRL-004 | RF13: POST /api/pagos crea pago y retorna HTTP 200")
    void crearPago_datoValido_retorna200ConPagoCreado() throws Exception {
        when(pagoService.guardar(any(Pago.class))).thenReturn(pagoEntidad);

        String body = """
            {
              "pedido": { "idPedido": 3 },
              "confirmado": false,
              "confirmadoPor": null
            }
            """;

        mockMvc.perform(post("/api/pagos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-CTRL-005 (RF13): PUT /api/pagos/{id} confirma transferencia → HTTP 200
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-CTRL-005 | RF13: PUT /api/pagos/1 confirma transferencia con contador - HTTP 200")
    void actualizarPago_confirmacionTransferencia_retorna200() throws Exception {
        when(pagoService.actualizarPago(eq(1L), any(Pago.class))).thenReturn(pagoEntidad);

        String body = """
            {
              "pedido": { "idPedido": 3, "metodoPago": "TRANSFERENCIA" },
              "confirmado": true,
              "confirmadoPor": { "idUsuario": 5 }
            }
            """;

        mockMvc.perform(put("/api/pagos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.confirmado", is(true)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-CTRL-006 (RF13): PUT /api/pagos/{id} con error de negocio → HTTP 500
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-CTRL-006 | RF13: PUT /api/pagos/1 con rol incorrecto retorna error de servidor")
    void actualizarPago_rolIncorrecto_retornaError() throws Exception {
        when(pagoService.actualizarPago(eq(1L), any(Pago.class)))
            .thenThrow(new IllegalArgumentException("Solo un usuario con tipoUsuario CONTADOR puede confirmar pagos."));

        String body = """
            {
              "pedido": { "idPedido": 3, "metodoPago": "TRANSFERENCIA" },
              "confirmado": true,
              "confirmadoPor": { "idUsuario": 3 }
            }
            """;

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(put("/api/pagos/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-CTRL-007 (RF08): DELETE /api/pagos/{id} retorna HTTP 204
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-CTRL-007 | RF08: DELETE /api/pagos/1 retorna HTTP 204 No Content")
    void eliminarPago_idExistente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/pagos/1"))
            .andExpect(status().isNoContent());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-CTRL-008: Verificar CORS habilitado para todos los orígenes
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-CTRL-008 | CORS habilitado - GET /api/pagos acepta origen externo")
    void getPagos_corsHabilitado_debeAceptarOrigenExterno() throws Exception {
        when(pagoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/pagos")
                .header("Origin", "http://localhost:8000")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk());
    }
}
