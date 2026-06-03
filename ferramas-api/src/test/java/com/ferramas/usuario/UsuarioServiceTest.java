package com.ferramas.usuario;

import com.ferramas.usuario.model.Usuario;
import com.ferramas.usuario.repository.UsuarioRepository;
import com.ferramas.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CP-USR-001 al CP-USR-008
 * Pruebas unitarias del servicio de Usuarios (RF01, RF02, RF03)
 * Cubre: autenticación multi-rol, registro, cambio de clave obligatorio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CP-USR | Servicio de Usuarios - Pruebas Unitarias")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioCliente;
    private Usuario usuarioAdmin;
    private Usuario usuarioContador;

    @BeforeEach
    void setUp() {
        usuarioCliente = new Usuario();
        usuarioCliente.setIdUsuario(1L);
        usuarioCliente.setNombre("Cliente Ferramas");
        usuarioCliente.setRut("11.111.111-1");
        usuarioCliente.setCorreo("cliente@ferramas.cl");
        usuarioCliente.setNombreUsuario("cliente");
        // Hash bcrypt real de "password123" (como en la BD)
        usuarioCliente.setContrasenia("$2b$12$tO9/Qdziq5rOkQU2a57GeOh/0WiF9K50BMW8in61jpajmzPQsKd1G");
        usuarioCliente.setTipoUsuario("cliente");
        usuarioCliente.setCambioPassword(false);

        usuarioAdmin = new Usuario();
        usuarioAdmin.setIdUsuario(2L);
        usuarioAdmin.setNombre("Admin Ferramas");
        usuarioAdmin.setRut("22.222.222-2");
        usuarioAdmin.setCorreo("admin@ferramas.cl");
        usuarioAdmin.setNombreUsuario("admin");
        usuarioAdmin.setContrasenia("$2b$12$BdtB7XEn4w/JBaj4IRuBeucHvE5tyf3rGhjXVlnrGQTm4pdQSJ/Km");
        usuarioAdmin.setTipoUsuario("administrador");
        usuarioAdmin.setCambioPassword(false);

        usuarioContador = new Usuario();
        usuarioContador.setIdUsuario(5L);
        usuarioContador.setNombre("Contador Ferramas");
        usuarioContador.setRut("55.555.555-5");
        usuarioContador.setCorreo("contador@ferramas.cl");
        usuarioContador.setNombreUsuario("contador");
        usuarioContador.setTipoUsuario("CONTADOR");
        usuarioContador.setCambioPassword(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-USR-001 (RF01): Listar usuarios retorna todos los roles
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-USR-001 | RF01: Listar usuarios retorna todos los roles del sistema")
    void listarUsuarios_debeRetornarTodosLosRoles() {
        when(usuarioRepository.findAll())
            .thenReturn(List.of(usuarioCliente, usuarioAdmin, usuarioContador));

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertEquals(3, resultado.size(), "Debe retornar 3 usuarios");
        assertTrue(resultado.stream().anyMatch(u -> "cliente".equals(u.getTipoUsuario())));
        assertTrue(resultado.stream().anyMatch(u -> "administrador".equals(u.getTipoUsuario())));
        assertTrue(resultado.stream().anyMatch(u -> "CONTADOR".equals(u.getTipoUsuario())));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-USR-002 (RF01): Buscar usuario por ID existente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-USR-002 | RF01: Buscar usuario existente por ID retorna datos correctos")
    void buscarPorId_usuarioExistente_retornaDatosCorrectos() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioCliente));

        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("cliente@ferramas.cl", resultado.get().getCorreo());
        assertEquals("cliente", resultado.get().getTipoUsuario());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-USR-003 (RF01): Buscar usuario por ID inexistente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-USR-003 | RF01: Buscar usuario con ID inexistente retorna Optional vacío")
    void buscarPorId_idInexistente_retornaVacio() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorId(999L);

        assertFalse(resultado.isPresent(), "No debe encontrar usuario con ID=999");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-USR-004 (RF02): Registrar nuevo cliente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-USR-004 | RF02: Registrar nuevo cliente guarda correctamente")
    void guardarUsuario_nuevoCliente_debeGuardarCorrectamente() {
        Usuario nuevoCliente = new Usuario();
        nuevoCliente.setNombre("Nuevo Cliente");
        nuevoCliente.setCorreo("nuevo@ferramas.cl");
        nuevoCliente.setRut("99.999.999-9");
        nuevoCliente.setNombreUsuario("nuevo_cliente");
        nuevoCliente.setContrasenia("hashbcrypt123");
        nuevoCliente.setTipoUsuario("cliente");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(nuevoCliente);

        Usuario resultado = usuarioService.guardarUsuario(nuevoCliente);

        assertNotNull(resultado);
        assertEquals("cliente", resultado.getTipoUsuario());
        assertEquals("nuevo@ferramas.cl", resultado.getCorreo());
        verify(usuarioRepository, times(1)).save(nuevoCliente);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-USR-005 (RF03): Flag cambioPassword en admin recién creado
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-USR-005 | RF03: Admin nuevo tiene cambioPassword=false (pendiente cambio)")
    void guardarUsuario_adminNuevo_cambioPasswordDebeSerFalse() {
        Usuario adminNuevo = new Usuario();
        adminNuevo.setNombre("Nuevo Admin");
        adminNuevo.setTipoUsuario("administrador");
        adminNuevo.setCambioPassword(false); // Aún no ha cambiado la clave del RUT

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(adminNuevo);

        Usuario resultado = usuarioService.guardarUsuario(adminNuevo);

        assertFalse(resultado.isCambioPassword(),
            "El admin no debe tener marcado el cambio de password hasta que lo realice (RF03)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-USR-006 (RF03): Actualizar admin con cambioPassword=true tras cambiar clave
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-USR-006 | RF03: Actualizar admin marca cambioPassword=true tras cambio de clave")
    void actualizarUsuario_adminCambiaClave_cambioPasswordDebeSerTrue() {
        Usuario adminActualizado = new Usuario();
        adminActualizado.setNombre("Admin Ferramas");
        adminActualizado.setRut("22.222.222-2");
        adminActualizado.setCorreo("admin@ferramas.cl");
        adminActualizado.setTelefono("+56920020000");
        adminActualizado.setNombreUsuario("admin");
        adminActualizado.setContrasenia("nuevaClaveHasheada");
        adminActualizado.setTipoUsuario("administrador");
        adminActualizado.setCambioPassword(true); // Ya cambió la clave

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.actualizarUsuario(2L, adminActualizado);

        assertTrue(resultado.isCambioPassword(),
            "Después de cambiar la clave, cambioPassword debe ser true (RF03)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-USR-007: Actualizar usuario con ID inexistente → RuntimeException
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-USR-007 | Actualizar usuario inexistente debe lanzar RuntimeException")
    void actualizarUsuario_idInexistente_debeLanzarExcepcion() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> usuarioService.actualizarUsuario(999L, usuarioCliente),
            "Debe lanzar excepción cuando el usuario no existe"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CP-USR-008: Eliminar usuario llama al repositorio
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CP-USR-008 | Eliminar usuario invoca deleteById en el repositorio")
    void eliminarUsuario_debeInvocarDeleteById() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
