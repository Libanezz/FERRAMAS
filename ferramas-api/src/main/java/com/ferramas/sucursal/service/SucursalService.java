package com.ferramas.sucursal.service;

import com.ferramas.sucursal.model.Sucursal;
import com.ferramas.sucursal.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SucursalService {

    @Autowired
    private SucursalRepository sucursalRepository;

    public List<Sucursal> listar() {
        return sucursalRepository.findAll();
    }

    public Optional<Sucursal> buscarPorId(Long id) {
        return sucursalRepository.findById(id);
    }

    public Sucursal guardar(Sucursal sucursal) {
        return sucursalRepository.save(sucursal);
    }

    public Sucursal actualizarSucursal(Long id, Sucursal datos) {
        return sucursalRepository.findById(id).map(sucursal -> {
            sucursal.setNombre(datos.getNombre());
            sucursal.setDireccion(datos.getDireccion());
            sucursal.setComuna(datos.getComuna());
            sucursal.setRegion(datos.getRegion());
            return sucursalRepository.save(sucursal);
        }).orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
    }


    public void eliminar(Long id) {
        sucursalRepository.deleteById(id);
    }
}