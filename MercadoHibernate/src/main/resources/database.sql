-- ========================================================
-- SCRIPT DE BASE DE DATOS: mercado_db
-- Sistema de Gestión de Productos de Supermercado
-- Relaciones ORM con Hibernate 6 (@ManyToOne Categoria)
-- ========================================================

CREATE DATABASE IF NOT EXISTS mercado_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mercado_db;

-- 1. TABLA: categorias
CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(300),
    icono VARCHAR(20) DEFAULT '📦',
    color_badge VARCHAR(40) DEFAULT 'cat-abarrotes',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserción de Categorías base
INSERT INTO categorias (codigo, nombre, descripcion, icono, color_badge) VALUES
('CAT-001', 'Frutas y Verduras', 'Frutas, hortalizas y verduras frescas del campo', '🍎', 'cat-frutas'),
('CAT-002', 'Lácteos y Huevos', 'Leche, quesos campesinos, yogures y huevos de granja', '🧀', 'cat-lacteos'),
('CAT-003', 'Carnes y Aves', 'Cortes de res, pollo fresco y carnes seleccionadas', '🥩', 'cat-carnes'),
('CAT-004', 'Abarrotes y Granos', 'Arroces, aceites, frijoles y alimentos no perecederos', '🌾', 'cat-abarrotes'),
('CAT-005', 'Panadería', 'Panes tajados, pastelería y productos horneados', '🥖', 'cat-panaderia'),
('CAT-006', 'Bebidas e Infusiones', 'Cafés especiales, tés, jugos e infusiones', '🧃', 'cat-bebidas'),
('CAT-007', 'Aseo y Limpieza', 'Detergentes, jabones y artículos para el hogar', '🧼', 'cat-aseo')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- 2. TABLA: fichas_nutricionales (@OneToOne con productos)
CREATE TABLE IF NOT EXISTS fichas_nutricionales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    calorias INT NOT NULL DEFAULT 0,
    proteinas DOUBLE DEFAULT 0.0,
    grasas_totales DOUBLE DEFAULT 0.0,
    azucares DOUBLE DEFAULT 0.0,
    sodio DOUBLE DEFAULT 0.0,
    alergenos VARCHAR(150) DEFAULT 'Ninguno',
    registro_invima VARCHAR(50) UNIQUE
);

-- 3. TABLA: proveedores (@ManyToMany con productos)
CREATE TABLE IF NOT EXISTS proveedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nit VARCHAR(30) NOT NULL UNIQUE,
    razon_social VARCHAR(120) NOT NULL,
    contacto VARCHAR(80),
    telefono VARCHAR(30),
    ciudad VARCHAR(60),
    email VARCHAR(100)
);

-- Inserción de Proveedores
INSERT INTO proveedores (nit, razon_social, contacto, telefono, ciudad, email) VALUES
('900.182.341-1', 'AgroBoyacá S.A.S.', 'Ing. Fernando Castro', '312 450 8921', 'Sogamoso', 'ventas@agroboyaca.com'),
('900.293.812-4', 'Lácteos del Altiplano Ltda.', 'Dra. Marcela Reyes', '310 894 1234', 'Duitama', 'pedidos@lacteosaltiplano.co'),
('900.410.552-8', 'Carnes y Cortes San Martín', 'Héctor Fabio Suárez', '315 721 3490', 'Sogamoso', 'comercial@carnessanmartin.com'),
('900.589.601-3', 'Abarrotes y Granos Sugamuxi', 'Gloria Inés Patiño', '314 380 9102', 'Tunja', 'ventas@granosugamuxi.com'),
('900.702.119-9', 'Panificadora Central Boyacá', 'Ricardo Morales', '318 642 7855', 'Sogamoso', 'panaderia@centralboyaca.com'),
('900.834.770-2', 'Distribuidora BioAseo Express', 'Laura Camila Silva', '320 901 4422', 'Bogotá', 'contacto@bioaseoexpress.co')
ON DUPLICATE KEY UPDATE razon_social=VALUES(razon_social);

-- 4. TABLA: productos (con FK hacia categorias y FK @OneToOne hacia fichas_nutricionales)
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    categoria_id BIGINT,
    ficha_id BIGINT UNIQUE,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    unidad_medida VARCHAR(30) NOT NULL,
    descripcion TEXT,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT,
    CONSTRAINT fk_producto_ficha FOREIGN KEY (ficha_id) REFERENCES fichas_nutricionales(id) ON DELETE SET NULL
);

-- 5. TABLA INTERMEDIA: productos_proveedores (@ManyToMany)
CREATE TABLE IF NOT EXISTS productos_proveedores (
    producto_id BIGINT NOT NULL,
    proveedor_id BIGINT NOT NULL,
    PRIMARY KEY (producto_id, proveedor_id),
    CONSTRAINT fk_pp_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_pp_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE CASCADE
);

-- Inserción de Fichas Nutricionales de ejemplo
INSERT INTO fichas_nutricionales (id, calorias, proteinas, grasas_totales, azucares, sodio, alergenos, registro_invima) VALUES
(1, 52, 0.3, 0.2, 10.4, 1.0, 'Ninguno', 'INVIMA-FRUT-00124'),
(2, 89, 1.1, 0.3, 12.2, 1.0, 'Ninguno', 'INVIMA-FRUT-00125'),
(3, 160, 2.0, 14.7, 0.7, 7.0, 'Ninguno', 'INVIMA-FRUT-00126'),
(4, 61, 3.2, 3.3, 4.8, 44.0, 'Contiene lactosa', 'RSA-001890-2023'),
(5, 260, 18.0, 20.0, 1.2, 480.0, 'Contiene leche y derivados', 'RSA-002145-2023'),
(6, 143, 12.6, 9.5, 0.7, 142.0, 'Contiene huevo', 'RSA-003412-2022'),
(7, 165, 31.0, 3.6, 0.0, 74.0, 'Ninguno', 'INVIMA-CARN-00891'),
(8, 250, 26.0, 17.0, 0.0, 72.0, 'Ninguno', 'INVIMA-CARN-00892'),
(9, 130, 2.7, 0.3, 0.1, 1.0, 'Ninguno (Libre de gluten)', 'RSA-004501-2021'),
(10, 884, 0.0, 100.0, 0.0, 0.0, 'Ninguno', 'RSA-005112-2022'),
(11, 337, 22.5, 1.1, 2.1, 12.0, 'Ninguno', 'RSA-006203-2021'),
(12, 265, 8.9, 3.2, 5.0, 490.0, 'Contiene trigo y gluten', 'RSA-007890-2023'),
(13, 2, 0.1, 0.0, 0.0, 2.0, 'Ninguno', 'RSA-008104-2024'),
(14, 0, 0.0, 0.0, 0.0, 0.0, 'No comestible (Aseo)', 'NSOH-00192-2020')
ON DUPLICATE KEY UPDATE calorias=VALUES(calorias);

-- Inserción de productos asociados a las categorías y fichas nutricionales
INSERT INTO productos (codigo, nombre, categoria_id, ficha_id, precio, stock, unidad_medida, descripcion) VALUES
('MKT-001', 'Manzana Roja Royal Gala', 1, 1, 4500.00, 48, 'Kilo', 'Manzanas frescas seleccionadas de alta calidad.'),
('MKT-002', 'Plátano Maduro Criollo', 1, 2, 2800.00, 65, 'Kilo', 'Plátanos maduros ideales para cocinar y consumo diario.'),
('MKT-003', 'Aguacate Hass Premium', 1, 3, 7200.00, 24, 'Kilo', 'Aguacate cremoso especial para ensaladas y guacamoles.'),
('MKT-004', 'Leche Entera Larga Vida', 2, 4, 4200.00, 80, 'Litro', 'Leche pasteurizada enriquecida con calcio y vitaminas.'),
('MKT-005', 'Queso Campesino Tradicional', 2, 5, 11500.00, 18, 'Bloque 500g', 'Queso fresco artesanal de mesa semiduro.'),
('MKT-006', 'Huevos AA Rojos (Cubeta x30)', 2, 6, 17900.00, 35, 'Cubeta', 'Huevos frescos de granja seleccionados tamaño AA.'),
('MKT-007', 'Pechuga de Pollo Fresca', 3, 7, 14500.00, 22, 'Kilo', 'Pechuga limpia sin piel ni hueso lista para preparar.'),
('MKT-008', 'Carne de Res para Asar', 3, 8, 24900.00, 14, 'Kilo', 'Corte fino de res tierno y jugoso.'),
('MKT-009', 'Arroz Blanco Grano Entero', 4, 9, 4600.00, 120, 'Bolsa 1kg', 'Arroz premium grano largo y seleccionado.'),
('MKT-010', 'Aceite Vegetal de Girasol', 4, 10, 13800.00, 40, 'Botella 900ml', 'Aceite 100% puro libre de colesterol.'),
('MKT-011', 'Frijol Rojo Calima', 4, 11, 6200.00, 50, 'Bolsa 500g', 'Grano seleccionado de cosecha reciente.'),
('MKT-012', 'Pan Tajado Blanco Familiar', 5, 12, 5900.00, 30, 'Paquete 450g', 'Pan suave enriquecido ideal para sándwiches.'),
('MKT-013', 'Café Colombiano Molido', 6, 13, 16500.00, 45, 'Bolsa 500g', 'Café 100% arábica de origen Boyacense tostado medio.'),
('MKT-014', 'Detergente en Polvo Multiacción', 7, 14, 18900.00, 28, 'Bolsa 2kg', 'Remoción profunda de manchas con aroma floral.'),
('MKT-015', 'Jabón Líquido Lavaplatos', 7, NULL, 8400.00, 5, 'Frasco 750ml', 'Poder desengrasante instantáneo concentrado.')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- Inserción en la tabla intermedia productos_proveedores
INSERT IGNORE INTO productos_proveedores (producto_id, proveedor_id) VALUES
(1, 1), (1, 4),
(3, 1),
(4, 2),
(5, 2),
(6, 1), (6, 2),
(7, 3),
(8, 3),
(9, 4),
(10, 4),
(11, 1), (11, 4),
(12, 5),
(13, 4),
(14, 6), (15, 6);
