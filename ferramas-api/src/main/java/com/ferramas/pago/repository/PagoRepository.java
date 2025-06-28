package com.ferramas.pago.repository;

import com.ferramas.pago.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {}