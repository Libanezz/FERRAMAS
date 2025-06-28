package com.ferramas.usuario.service;

import com.ferramas.usuario.model.Usuario;
import com.ferramas.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario datos) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(datos.getNombre());
            usuario.setRut(datos.getRut());
            usuario.setCorreo(datos.getCorreo());
            usuario.setTelefono(datos.getTelefono());
            usuario.setNombreUsuario(datos.getNombreUsuario());
            usuario.setContrasenia(datos.getContrasenia());
            usuario.setTipoUsuario(datos.getTipoUsuario());
            usuario.setCambioPassword(datos.isCambioPassword());
            usuario.setSucursal(datos.getSucursal());
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
