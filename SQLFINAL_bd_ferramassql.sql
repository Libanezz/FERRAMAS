--  Desactivar validación de claves foráneas
SET FOREIGN_KEY_CHECKS = 0;

--  Limpieza completa
DELETE FROM Despacho;
DELETE FROM Pago;
DELETE FROM DetallePedido;
DELETE FROM Pedido;
DELETE FROM Usuario;
DELETE FROM Inventario;
DELETE FROM Producto;
DELETE FROM Marca;
DELETE FROM Sucursal;

--  Reinicio de auto_increment
ALTER TABLE Despacho AUTO_INCREMENT = 1;
ALTER TABLE Pago AUTO_INCREMENT = 1;
ALTER TABLE DetallePedido AUTO_INCREMENT = 1;
ALTER TABLE Pedido AUTO_INCREMENT = 1;
ALTER TABLE Usuario AUTO_INCREMENT = 1;
ALTER TABLE Inventario AUTO_INCREMENT = 1;
ALTER TABLE Producto AUTO_INCREMENT = 1;
ALTER TABLE Marca AUTO_INCREMENT = 1;
ALTER TABLE Sucursal AUTO_INCREMENT = 1;

--  Reactivar validación
SET FOREIGN_KEY_CHECKS = 1;

--  Sucursales
INSERT INTO Sucursal (nombre, direccion, comuna, region) VALUES
('Sucursal Centro', 'Av. Libertador Bernardo O’Higgins 1234', 'Santiago Centro', 'Región Metropolitana'),
('Sucursal Norte', 'Av. Américo Vespucio 5678', 'Conchalí', 'Región Metropolitana'),
('Sucursal Sur', 'Gran Avenida 9020', 'La Cisterna', 'Región Metropolitana'),
('Sucursal Poniente', 'Av. Pajaritos 1123', 'Maipú', 'Región Metropolitana');

--  Marcas
INSERT INTO Marca (nombre) VALUES
('Bosch'),
('Makita'),
('Stanley'),
('Sika'),
('DeWalt'),
('Black+Decker'),
('3M'),
('Tramontina'),
('Irwin'),
('Total');

--  Productos
INSERT INTO Producto (nombre, descripcion, precio, id_marca, url_imagen) VALUES
('Taladro Bosch 500W', 'Taladro eléctrico profesional', 59990, 1, 'https://imagedelivery.net/4fYuQyy-r8_rpBpcY7lH_A/sodimacCL/7318774_03/w=800,h=800,fit=pad'),
('Sierra Circular Makita', 'Corte de madera profesional', 89990, 2, 'https://herramientas.cl/media/catalog/product/2/2/22281.jpg?quality=100&fit=bounds&height=650&width=550'),
('Cinta métrica Stanley', '5 metros, metálica', 5990, 3, 'https://http2.mlstatic.com/D_Q_NP_633860-MLA74676490278_022024-O.webp'),
('Sellador Sika Flex', 'Sellador para grietas', 4990, 4, 'https://trongemen.cl/wp-content/uploads/Sikaflex_11C.jpg'),
('Atornillador DeWalt', 'Inalámbrico con batería', 65990, 5, 'https://herramientas.cl/media/catalog/product/d/c/dcf7871b-b3_1.jpg?quality=100&fit=bounds&height=650&width=550'),
('Lijadora Black+Decker', 'Multifunción 200W', 32990, 6, 'https://http2.mlstatic.com/D_NQ_NP_925699-MLC42119920241_062020-O-lijadora-13-135w-black-decker-cd450-b2c.webp'),
('Mascarillas 3M', 'N95, caja de 10 unidades', 9900, 7, 'https://multimedia.3m.com/mws/media/1203374J/3m-8801h-shell-hospital-mask.jpg?width=506'),
('Alicate Tramontina', 'Uso general, ergonómico', 7990, 8, 'https://herramientasferreteria.cl/wp-content/uploads/2024/01/44302016PDM001B-600x600.webp'),
('Nivel Irwin', '60 cm, magnético', 12990, 9, 'https://http2.mlstatic.com/D_Q_NP_876478-MLU74123306753_012024-O.webp'),
('Destornillador Total', 'Punta plana, mango de goma', 2990, 10, 'https://rimage.ripley.cl/home.ripley/Attachment/MKP/2/MPM00060501206/full_image-1.webp');

--  Usuarios por rol
INSERT INTO Usuario (nombre, rut, correo, telefono, nombre_usuario, contrasenia, tipo_usuario, cambio_password, id_sucursal) VALUES
('Cliente Ferramas', '11.111.111-1', 'cliente@ferramas.cl', '+56910010000', 'cliente', 'cliente123', 'cliente', FALSE, 1),
('Admin Ferramas', '22.222.222-2', 'admin@ferramas.cl', '+56920020000', 'admin', '22.222.222-2', 'administrador', TRUE, 2),
('Vendedor Ferramas', '33.333.333-3', 'vendedor@ferramas.cl', '+56930030000', 'vendedor', 'vendedor123', 'vendedor', FALSE, 3),
('Bodeguero Ferramas', '44.444.444-4', 'bodega@ferramas.cl', '+56940040000', 'bodeguero', 'bodega123', 'bodeguero', FALSE, 4),
('Contador Ferramas', '55.555.555-5', 'contador@ferramas.cl', '+56950050000', 'contador', 'contador123', 'contador', FALSE, 1);

--  Pedidos con variedad de métodos de pago
INSERT INTO Pedido (id_usuario_cliente, metodo_pago, retiro_en_tienda, estado) VALUES
(1, 'debito', TRUE, 'pendiente'),
(1, 'credito', FALSE, 'aprobado'),
(1, 'transferencia', TRUE, 'despachado'),
(1, 'paypal', FALSE, 'rechazado');

--  Detalle de pedidos
INSERT INTO DetallePedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES
(1, 1, 2, 59990),
(2, 2, 1, 89990),
(3, 3, 3, 5990),
(4, 4, 1, 4990);

--  Pagos
INSERT INTO Pago (id_pedido, confirmado_por, confirmado) VALUES
(1, 2, TRUE),
(2, 2, TRUE),
(3, 2, TRUE),
(4, 2, FALSE);

--  Despachos
INSERT INTO Despacho (id_pedido, encargado_despacho, fecha_despacho, direccion_entrega, entregado) VALUES
(1, 4, NOW(), 'Calle Ejemplo 123, Santiago', TRUE),
(3, 4, NOW(), 'Pasaje San Diego 456, La Cisterna', FALSE);

--  Inventario distribuido por sucursal y producto
INSERT INTO Inventario (id_sucursal, id_producto, stock) VALUES
(1, 1, 10),  -- Taladro Bosch - Sucursal Centro
(1, 3, 20),  -- Cinta métrica - Centro
(1, 5, 8),   -- DeWalt - Centro

(2, 2, 12),  -- Makita - Norte
(2, 6, 6),   -- Lijadora - Norte
(2, 8, 15),  -- Alicate - Norte

(3, 1, 5),   -- Taladro Bosch - Sur
(3, 4, 25),  -- Sika Flex - Sur
(3, 7, 18),  -- Mascarillas - Sur

(4, 9, 7),   -- Nivel Irwin - Poniente
(4, 10, 10), -- Destornillador - Poniente
(4, 2, 6);   -- Makita - Poniente
