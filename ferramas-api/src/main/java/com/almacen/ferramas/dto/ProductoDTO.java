package com.almacen.ferramas.dto;

public class ProductoDTO {
    private String nombre;
    private String descripcion;
    private int precio;
    private int id_marca;

    //constructores vacios
    public ProductoDTO() {
    }

    //constructores

    public ProductoDTO(String nombre, String descripcion, int precio, int id_marca) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.id_marca = id_marca;
    }

    //getter and setter
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getId_marca() {
        return id_marca;
    }

    public void setId_marca(int id_marca) {
        this.id_marca = id_marca;
    }
}
