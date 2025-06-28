package com.ferramas.despacho.model;

import com.ferramas.pedido.model.Pedido;
import com.ferramas.usuario.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "despacho")
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_despacho")
    private Long idDespacho;

    @OneToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "encargado_despacho")
    private Usuario encargado;

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(name = "direccion_entrega")
    private String direccionEntrega;

    @Column(name = "entregado")
    private boolean entregado;

    public Despacho() {}

    public Despacho(Long idDespacho, Pedido pedido, Usuario encargado, LocalDateTime fechaDespacho, String direccionEntrega, boolean entregado) {
        this.idDespacho = idDespacho;
        this.pedido = pedido;
        this.encargado = encargado;
        this.fechaDespacho = fechaDespacho;
        this.direccionEntrega = direccionEntrega;
        this.entregado = entregado;
    }

    public Long getIdDespacho() { return idDespacho; }
    public void setIdDespacho(Long idDespacho) { this.idDespacho = idDespacho; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Usuario getEncargado() { return encargado; }
    public void setEncargado(Usuario encargado) { this.encargado = encargado; }

    public LocalDateTime getFechaDespacho() { return fechaDespacho; }
    public void setFechaDespacho(LocalDateTime fechaDespacho) { this.fechaDespacho = fechaDespacho; }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public boolean isEntregado() { return entregado; }
    public void setEntregado(boolean entregado) { this.entregado = entregado; }
}
