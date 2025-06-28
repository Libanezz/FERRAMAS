package com.ferramas.despacho.repository;

import com.ferramas.despacho.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {}