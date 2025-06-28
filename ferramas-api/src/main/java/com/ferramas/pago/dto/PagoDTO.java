package com.ferramas.pago.dto;

public class PagoDTO {

    private Long idPago;
    private String metodoPago;
    private Double montoTotal;
    private String fechaPago;
    private boolean confirmado;
    private String confirmadoPor;

    public PagoDTO() {}

    public PagoDTO(Long idPago, String metodoPago, Double montoTotal,
                   String fechaPago, boolean confirmado, String confirmadoPor) {
        this.idPago = idPago;
        this.metodoPago = metodoPago;
        this.montoTotal = montoTotal;
        this.fechaPago = fechaPago;
        this.confirmado = confirmado;
        this.confirmadoPor = confirmadoPor;
    }

    public Long getIdPago() { return idPago; }
    public void setIdPago(Long idPago) { this.idPago = idPago; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }

    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }

    public String getConfirmadoPor() { return confirmadoPor; }
    public void setConfirmadoPor(String confirmadoPor) { this.confirmadoPor = confirmadoPor; }
}
