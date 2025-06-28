package com.ferramas.pedido.model;

import jakarta.persistence.*;
import com.ferramas.usuario.model.Usuario;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @ManyToOne
    @JoinColumn(name = "id_usuario_cliente", nullable = false)
    private Usuario usuario;

    private LocalDateTime fecha;

    private String estado; // 'pendiente', 'aprobado', etc.

    @Column(name = "metodo_pago")
    private String metodoPago; // 'debito', 'credito', etc.

    @Column(name = "retiro_en_tienda")
    private Boolean retiroEnTienda;

    public Pedido() {}

    // Getters y Setters

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Boolean getRetiroEnTienda() {
        return retiroEnTienda;
    }

    public void setRetiroEnTienda(Boolean retiroEnTienda) {
        this.retiroEnTienda = retiroEnTienda;
    }
}
