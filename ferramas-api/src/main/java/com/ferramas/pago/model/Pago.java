package com.ferramas.pago.model;

import jakarta.persistence.*;
import com.ferramas.pedido.model.Pedido;
import com.ferramas.usuario.model.Usuario;

import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @OneToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @Column(name = "fecha_pago")
    private LocalDateTime fecha;

    private boolean confirmado;

    @ManyToOne
    @JoinColumn(name = "confirmado_por")
    private Usuario confirmadoPor;

    public Pago() {}

    public Pago(Long idPago, Pedido pedido, LocalDateTime fecha, boolean confirmado, Usuario confirmadoPor) {
        this.idPago = idPago;
        this.pedido = pedido;
        this.fecha = fecha;
        this.confirmado = confirmado;
        this.confirmadoPor = confirmadoPor;
    }

    public Long getIdPago() { return idPago; }
    public void setIdPago(Long idPago) { this.idPago = idPago; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }

    public Usuario getConfirmadoPor() { return confirmadoPor; }
    public void setConfirmadoPor(Usuario confirmadoPor) { this.confirmadoPor = confirmadoPor; }
}
