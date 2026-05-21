package com.ferramas.inventario.model;

import jakarta.persistence.*;

import com.ferramas.producto.model.Producto;
import com.ferramas.sucursal.model.Sucursal;

@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long idInventario;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "ultima_actualizacion_por")
    private String ultimaActualizacionPor;

    public Inventario() {}

    public Inventario(Long idInventario, Producto producto, Sucursal sucursal, Integer stock, String ultimaActualizacionPor) {
        this.idInventario = idInventario;
        this.producto = producto;
        this.sucursal = sucursal;
        this.stock = stock;
        this.ultimaActualizacionPor = ultimaActualizacionPor;
    }

    public Long getIdInventario() { return idInventario; }
    public void setIdInventario(Long idInventario) { this.idInventario = idInventario; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getUltimaActualizacionPor() { return ultimaActualizacionPor; }
    public void setUltimaActualizacionPor(String ultimaActualizacionPor) { this.ultimaActualizacionPor = ultimaActualizacionPor; }
}
