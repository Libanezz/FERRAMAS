package com.ferramas.marca.service;

import com.ferramas.marca.model.Marca;
import com.ferramas.marca.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository marcaRepository;

    public List<Marca> listarMarcas() {
        return marcaRepository.findAll();
    }

    public Optional<Marca> buscarPorId(Long id) {
        return marcaRepository.findById(id);
    }

    public Marca guardarMarca(Marca marca) {
        return marcaRepository.save(marca);
    }

    public Marca actualizarMarca(Long id, Marca datos) {
        return marcaRepository.findById(id).map(marca -> {
            marca.setNombre(datos.getNombre());
            return marcaRepository.save(marca);
        }).orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + id));
    }

    public void eliminarMarca(Long id) {
        marcaRepository.deleteById(id);
    }
}