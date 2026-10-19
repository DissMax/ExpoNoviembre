/**
 * MERCADO CENTRAL - CRUD DE PRODUCTOS CON HIBERNATE ORM
 * Arquitectura por Capas NetBeans / Java EE
 * Tecnólogo ADSO - SENA
 */

document.addEventListener('DOMContentLoaded', () => {

  // ========================================================
  // 1. INVENTORY STATE (DATOS INICIALES DEL MERCADO)
  // ========================================================
  let products = [
    {
      id: 1,
      codigo: 'MKT-001',
      nombre: 'Manzana Roja Royal Gala',
      categoria: 'Frutas y Verduras',
      precio: 4500,
      stock: 48,
      unidadMedida: 'Kilo',
      descripcion: 'Manzanas frescas seleccionadas de alta calidad, dulces y crocantes.'
    },
    {
      id: 2,
      codigo: 'MKT-002',
      nombre: 'Plátano Maduro Criollo',
      categoria: 'Frutas y Verduras',
      precio: 2800,
      stock: 65,
      unidadMedida: 'Kilo',
      descripcion: 'Plátanos maduros ideales para freír, asar o consumo diario.'
    },
    {
      id: 3,
      codigo: 'MKT-003',
      nombre: 'Aguacate Hass Premium',
      categoria: 'Frutas y Verduras',
      precio: 7200,
      stock: 24,
      unidadMedida: 'Kilo',
      descripcion: 'Aguacate cremoso especial para ensaladas, guacamoles y tostadas.'
    },
    {
      id: 4,
      codigo: 'MKT-004',
      nombre: 'Leche Entera Larga Vida',
      categoria: 'Lácteos y Huevos',
      precio: 4200,
      stock: 80,
      unidadMedida: 'Litro',
      descripcion: 'Leche 100% pura pasteurizada enriquecida con vitaminas A y D.'
    },
    {
      id: 5,
      codigo: 'MKT-005',
      nombre: 'Queso Campesino Tradicional',
      categoria: 'Lácteos y Huevos',
      precio: 11500,
      stock: 18,
      unidadMedida: 'Bloque 500g',
      descripcion: 'Queso fresco artesanal semiduro, bajo en sal, ideal para el desayuno.'
    },
    {
      id: 6,
      codigo: 'MKT-006',
      nombre: 'Huevos AA Rojos (Cubeta x30)',
      categoria: 'Lácteos y Huevos',
      precio: 17900,
      stock: 35,
      unidadMedida: 'Cubeta',
      descripcion: 'Huevos frescos de granja campesina clasificados tamaño AA.'
    },
    {
      id: 7,
      codigo: 'MKT-007',
      nombre: 'Pechuga de Pollo Fresca',
      categoria: 'Carnes y Aves',
      precio: 14500,
      stock: 22,
      unidadMedida: 'Kilo',
      descripcion: 'Pechuga de pollo limpia sin piel ni hueso, lista para filetear.'
    },
    {
      id: 8,
      codigo: 'MKT-008',
      nombre: 'Carne de Res para Asar',
      categoria: 'Carnes y Aves',
      precio: 24900,
      stock: 14,
      unidadMedida: 'Kilo',
      descripcion: 'Corte fino de res tierno y jugoso con maduración controlada.'
    },
    {
      id: 9,
      codigo: 'MKT-009',
      nombre: 'Arroz Blanco Grano Entero',
      categoria: 'Abarrotes y Granos',
      precio: 4600,
      stock: 120,
      unidadMedida: 'Bolsa 1kg',
      descripcion: 'Arroz de grano largo seleccionado, rinde más y no se pega.'
    },
    {
      id: 10,
      codigo: 'MKT-010',
      nombre: 'Aceite Vegetal de Girasol',
      categoria: 'Abarrotes y Granos',
      precio: 13800,
      stock: 40,
      unidadMedida: 'Botella 900ml',
      descripcion: 'Aceite 100% puro libre de colesterol, ideal para toda preparación.'
    },
    {
      id: 11,
      codigo: 'MKT-011',
      nombre: 'Frijol Rojo Calima',
      categoria: 'Abarrotes y Granos',
      precio: 6200,
      stock: 50,
      unidadMedida: 'Bolsa 500g',
      descripcion: 'Grano seleccionado de cosecha reciente, suave y de fácil cocción.'
    },
    {
      id: 12,
      codigo: 'MKT-012',
      nombre: 'Pan Tajado Blanco Familiar',
      categoria: 'Panadería',
      precio: 5900,
      stock: 30,
      unidadMedida: 'Paquete 450g',
      descripcion: 'Pan suave enriquecido con calcio, perfecto para sándwiches escolares.'
    },
    {
      id: 13,
      codigo: 'MKT-013',
      nombre: 'Café Colombiano Molido',
      categoria: 'Bebidas e Infusiones',
      precio: 16500,
      stock: 45,
      unidadMedida: 'Bolsa 500g',
      descripcion: 'Café 100% arábica de origen Boyacá, tostión media con notas a caramelo.'
    },
    {
      id: 14,
      codigo: 'MKT-014',
      nombre: 'Detergente en Polvo Multiacción',
      categoria: 'Aseo y Limpieza',
      precio: 18900,
      stock: 8,
      unidadMedida: 'Bolsa 2kg',
      descripcion: 'Remoción profunda de manchas en ropa blanca y de color con aroma fresco.'
    },
    {
      id: 15,
      codigo: 'MKT-015',
      nombre: 'Jabón Líquido Lavaplatos',
      categoria: 'Aseo y Limpieza',
      precio: 8400,
      stock: 5,
      unidadMedida: 'Frasco 750ml',
      descripcion: 'Fórmula ultra desengrasante con extracto de limón y cuidado de manos.'
    }
  ];

  let nextId = 16;
  let activeCategory = 'TODAS';
  let activeStockFilter = 'todos'; // 'todos' | 'disponible' | 'bajo' | 'agotado'
  let targetDeleteId = null;

  // ========================================================
  // 2. DOM ELEMENTS
  // ========================================================
  const productsGrid = document.getElementById('productsGrid');
  const emptyState = document.getElementById('emptyState');
  const resultsCounter = document.getElementById('resultsCounter');

  // Stats
  const statTotalProductos = document.getElementById('statTotalProductos');
  const statTotalStock = document.getElementById('statTotalStock');
  const statValorInventario = document.getElementById('statValorInventario');
  const statBajoStock = document.getElementById('statBajoStock');

  // Search & Filters
  const searchInput = document.getElementById('searchInput');
  const btnClearSearch = document.getElementById('btnClearSearch');
  const sortSelect = document.getElementById('sortSelect');
  const catChips = document.querySelectorAll('.cat-chip');
  const stockToggles = document.querySelectorAll('.btn-stock-toggle');
  const minPriceInput = document.getElementById('minPriceInput');
  const maxPriceInput = document.getElementById('maxPriceInput');
  const btnApplyPriceFilter = document.getElementById('btnApplyPriceFilter');
  const btnResetFilters = document.getElementById('btnResetFilters');
  const btnResetEmpty = document.getElementById('btnResetEmpty');

  // Modal Create/Edit
  const btnOpenNewModal = document.getElementById('btnOpenNewModal');
  const productModalOverlay = document.getElementById('productModalOverlay');
  const btnCloseModal = document.getElementById('btnCloseModal');
  const btnCancelModal = document.getElementById('btnCancelModal');
  const productForm = document.getElementById('productForm');
  const modalTitle = document.getElementById('modalTitle');
  const modalIconBadge = document.getElementById('modalIconBadge');
  const btnSaveText = document.getElementById('btnSaveText');
  const ormCodePreview = document.getElementById('ormCodePreview');

  // Form Fields
  const formProductId = document.getElementById('formProductId');
  const formCodigo = document.getElementById('formCodigo');
  const formCategoria = document.getElementById('formCategoria');
  const formNombre = document.getElementById('formNombre');
  const formPrecio = document.getElementById('formPrecio');
  const formStock = document.getElementById('formStock');
  const formUnidad = document.getElementById('formUnidad');
  const formDescripcion = document.getElementById('formDescripcion');

  // Modal Delete
  const deleteModalOverlay = document.getElementById('deleteModalOverlay');
  const deleteConfirmText = document.getElementById('deleteConfirmText');
  const deleteProductPreview = document.getElementById('deleteProductPreview');
  const btnCancelDelete = document.getElementById('btnCancelDelete');
  const btnConfirmDelete = document.getElementById('btnConfirmDelete');

  // Layers Drawer
  const btnToggleLayersPanel = document.getElementById('btnToggleLayersPanel');
  const layersDrawer = document.getElementById('layersDrawer');
  const btnCloseDrawer = document.getElementById('btnCloseDrawer');
  const drawerConsoleBody = document.getElementById('drawerConsoleBody');
  const btnClearDrawerLogs = document.getElementById('btnClearDrawerLogs');
  const filePills = document.querySelectorAll('.file-pill');
  const previewFilePath = document.getElementById('previewFilePath');
  const previewFileCode = document.getElementById('previewFileCode');
  const btnCopyLayerCode = document.getElementById('btnCopyLayerCode');

  // ========================================================
  // 3. LAYER REPOSITORY FILES (NETBEANS PROJECT CODE)
  // ========================================================
  const layerFiles = {
    producto: {
      path: 'src/main/java/com/mercado/model/Producto.java',
      code: `package com.mercado.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad JPA mapeada con Hibernate ORM.
 * Representa la tabla física 'productos' en MySQL / H2.
 */
@Entity
@Table(name = "productos")
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "categoria", nullable = false, length = 60)
    private String categoria;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso = LocalDateTime.now();

    public Producto() {}

    // Getters, Setters y constructores...
}`
    },
    dao: {
      path: 'src/main/java/com/mercado/dao/ProductoDAOImpl.java',
      code: `package com.mercado.dao;

import com.mercado.model.Producto;
import com.mercado.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class ProductoDAOImpl implements IProductoDAO {

    @Override
    public void guardar(Producto producto) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(producto); // Hibernate genera el INSERT
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<Producto> buscarPorNombre(String termino) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Consulta HQL orientada a objetos (no a tablas)
            String hql = "FROM Producto p WHERE lower(p.nombre) LIKE :termino OR lower(p.codigo) LIKE :termino";
            Query<Producto> q = session.createQuery(hql, Producto.class);
            q.setParameter("termino", "%" + termino.toLowerCase().trim() + "%");
            return q.list();
        }
    }

    @Override
    public List<Producto> filtrarPorCategoria(String categoria) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Producto p WHERE p.categoria = :categoria ORDER BY p.nombre ASC";
            Query<Producto> q = session.createQuery(hql, Producto.class);
            q.setParameter("categoria", categoria);
            return q.list();
        }
    }
}`
    },
    service: {
      path: 'src/main/java/com/mercado/service/ProductoServiceImpl.java',
      code: `package com.mercado.service;

import com.mercado.dao.IProductoDAO;
import com.mercado.dao.ProductoDAOImpl;
import com.mercado.model.Producto;
import java.util.List;

public class ProductoServiceImpl implements IProductoService {

    private final IProductoDAO productoDAO = new ProductoDAOImpl();

    @Override
    public void registrarProducto(Producto p) {
        // Validación de reglas de negocio
        if (p.getPrecio() == null || p.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
        if (p.getStock() == null || p.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        productoDAO.guardar(p);
    }

    @Override
    public List<Producto> buscarPorNombre(String termino) {
        return productoDAO.buscarPorNombre(termino);
    }
}`
    },
    controller: {
      path: 'src/main/java/com/mercado/controller/ProductoController.java',
      code: `package com.mercado.controller;

import com.mercado.model.Producto;
import com.mercado.service.IProductoService;
import com.mercado.service.ProductoServiceImpl;
import java.util.List;

public class ProductoController {

    private final IProductoService service = new ProductoServiceImpl();

    public List<Producto> listarProductos() {
        return service.obtenerTodos();
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return service.buscarPorNombre(nombre);
    }

    public String registrarProducto(Producto producto) {
        try {
            service.registrarProducto(producto);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}`
    },
    util: {
      path: 'src/main/java/com/mercado/util/HibernateUtil.java',
      code: `package com.mercado.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception ex) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}`
    },
    config: {
      path: 'src/main/resources/hibernate.cfg.xml',
      code: `<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/mercado_db?createDatabaseIfNotExist=true</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">admin123</property>
        <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>

        <!-- Logging SQL -->
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>
        <property name="hibernate.hbm2ddl.auto">update</property>

        <mapping class="com.mercado.model.Producto"/>
    </session-factory>
</hibernate-configuration>`
    },
    pom: {
      path: 'pom.xml',
      code: `<!-- Dependencias Maven: Hibernate Core + MySQL + H2 -->
<dependencies>
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.4.4.Final</version>
    </dependency>
    <dependency>
        <groupId>jakarta.persistence</groupId>
        <artifactId>jakarta.persistence-api</artifactId>
        <version>3.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>
</dependencies>`
    }
  };

  function renderLayerPreview(fileKey) {
    const file = layerFiles[fileKey];
    if (file) {
      previewFilePath.textContent = file.path;
      previewFileCode.textContent = file.code;
    }
  }
  renderLayerPreview('producto');

  filePills.forEach(pill => {
    pill.addEventListener('click', () => {
      filePills.forEach(p => p.classList.remove('active'));
      pill.classList.add('active');
      const fileKey = pill.getAttribute('data-file');
      renderLayerPreview(fileKey);
    });
  });

  btnCopyLayerCode.addEventListener('click', () => {
    navigator.clipboard.writeText(previewFileCode.textContent)
      .then(() => showToast('✔ Archivo copiado al portapapeles', 'success'))
      .catch(() => showToast('Error al copiar', 'warning'));
  });

  // ========================================================
  // 4. HIBERNATE SQL LOGGER (SIMULACIÓN DE SHOW_SQL)
  // ========================================================
  function logHibernateSql(operation, hql, sql, params = []) {
    const logItem = document.createElement('div');
    logItem.classList.add('console-log-line');

    let formattedSql = sql
      .replace(/\b(insert|into|values|select|from|update|set|where|delete|order by|like)\b/gi, '<span class="sql-kw">$1</span>')
      .replace(/\b(productos|p)\b/gi, '<span class="sql-table">$1</span>')
      .replace(/\?/g, '<span class="sql-param">?</span>');

    const paramsStr = params.length > 0 ? `<br><small style="color:#94A3B8;">Bindings: [${params.join(', ')}]</small>` : '';

    logItem.innerHTML = `
      <div style="color: #64748B; font-size: 0.68rem;">[${new Date().toLocaleTimeString()}] <strong>${operation}</strong></div>
      <div style="color: #A78BFA; font-size: 0.72rem;">HQL: ${hql}</div>
      <div style="margin-top: 2px;">${formattedSql}${paramsStr}</div>
    `;

    drawerConsoleBody.appendChild(logItem);
    drawerConsoleBody.scrollTop = drawerConsoleBody.scrollHeight;
  }

  // Initial welcome log
  logHibernateSql('INIT', 'Hibernate SessionFactory online', 'SELECT 1 FROM productos LIMIT 1;');

  btnClearDrawerLogs.addEventListener('click', () => {
    drawerConsoleBody.innerHTML = '';
    logHibernateSql('RESET', 'Terminal reiniciada', '-- Listo para registrar operaciones HQL');
  });

  // ========================================================
  // 5. STATS CALCULATION
  // ========================================================
  function updateStats() {
    const totalProductos = products.length;
    let totalStock = 0;
    let valorTotal = 0;
    let bajoStock = 0;

    products.forEach(p => {
      const stock = parseInt(p.stock) || 0;
      const precio = parseFloat(p.precio) || 0;
      totalStock += stock;
      valorTotal += (stock * precio);
      if (stock <= 10) {
        bajoStock++;
      }
    });

    statTotalProductos.textContent = totalProductos;
    statTotalStock.textContent = `${totalStock} u`;
    statValorInventario.textContent = new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0
    }).format(valorTotal);
    statBajoStock.textContent = bajoStock;
  }

  // ========================================================
  // 6. CATEGORY FORMATTING HELPERS
  // ========================================================
  function getCategoryTagClass(categoria) {
    switch (categoria) {
      case 'Frutas y Verduras': return 'tag-frutas';
      case 'Lácteos y Huevos': return 'tag-lacteos';
      case 'Carnes y Aves': return 'tag-carnes';
      case 'Abarrotes y Granos': return 'tag-abarrotes';
      case 'Panadería': return 'tag-panaderia';
      case 'Bebidas e Infusiones': return 'tag-bebidas';
      case 'Aseo y Limpieza': return 'tag-aseo';
      default: return 'tag-abarrotes';
    }
  }

  function getCategoryEmoji(categoria) {
    switch (categoria) {
      case 'Frutas y Verduras': return '🍎';
      case 'Lácteos y Huevos': return '🧀';
      case 'Carnes y Aves': return '🥩';
      case 'Abarrotes y Granos': return '🌾';
      case 'Panadería': return '🥖';
      case 'Bebidas e Infusiones': return '🧃';
      case 'Aseo y Limpieza': return '🧼';
      default: return '📦';
    }
  }

  // ========================================================
  // 7. FILTER & SEARCH ENGINE
  // ========================================================
  function getFilteredProducts() {
    const query = searchInput.value.trim().toLowerCase();
    const minPrice = parseFloat(minPriceInput.value) || null;
    const maxPrice = parseFloat(maxPriceInput.value) || null;
    const sortMode = sortSelect.value;

    let filtered = products.filter(p => {
      // 1. Text search by name, code or description
      if (query) {
        const matchesName = p.nombre.toLowerCase().includes(query);
        const matchesCode = p.codigo.toLowerCase().includes(query);
        const matchesDesc = (p.descripcion || '').toLowerCase().includes(query);
        if (!matchesName && !matchesCode && !matchesDesc) {
          return false;
        }
      }

      // 2. Category filter
      if (activeCategory !== 'TODAS' && p.categoria !== activeCategory) {
        return false;
      }

      // 3. Stock status filter
      if (activeStockFilter === 'disponible' && p.stock <= 0) return false;
      if (activeStockFilter === 'bajo' && (p.stock <= 0 || p.stock > 10)) return false;
      if (activeStockFilter === 'agotado' && p.stock > 0) return false;

      // 4. Price range filter
      if (minPrice !== null && p.precio < minPrice) return false;
      if (maxPrice !== null && p.precio > maxPrice) return false;

      return true;
    });

    // 5. Sorting
    filtered.sort((a, b) => {
      switch (sortMode) {
        case 'nombre-asc': return a.nombre.localeCompare(b.nombre);
        case 'nombre-desc': return b.nombre.localeCompare(a.nombre);
        case 'precio-asc': return a.precio - b.precio;
        case 'precio-desc': return b.precio - a.precio;
        case 'stock-desc': return b.stock - a.stock;
        case 'stock-asc': return a.stock - b.stock;
        default: return 0;
      }
    });

    return filtered;
  }

  // ========================================================
  // 8. RENDER PRODUCTS GRID
  // ========================================================
  function renderProducts() {
    const filtered = getFilteredProducts();
    productsGrid.innerHTML = '';

    resultsCounter.innerHTML = `Mostrando <strong>${filtered.length}</strong> de ${products.length} productos`;

    if (filtered.length === 0) {
      emptyState.style.display = 'block';
    } else {
      emptyState.style.display = 'none';

      filtered.forEach(p => {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.dataset.id = p.id;

        const formattedPrice = new Intl.NumberFormat('es-CO', {
          style: 'currency',
          currency: 'COP',
          maximumFractionDigits: 0
        }).format(p.precio);

        let stockClass = 'stock-ok';
        let stockLabel = `En Stock · ${p.stock} ${p.unidadMedida || 'u'}`;
        if (p.stock <= 0) {
          stockClass = 'stock-out';
          stockLabel = 'Agotado (0 u)';
        } else if (p.stock <= 10) {
          stockClass = 'stock-low';
          stockLabel = `Bajo Stock · ${p.stock} ${p.unidadMedida || 'u'}`;
        }

        const tagClass = getCategoryTagClass(p.categoria);
        const emoji = getCategoryEmoji(p.categoria);

        card.innerHTML = `
          <div>
            <div class="product-card-top">
              <span class="category-tag ${tagClass}">
                <span>${emoji}</span> ${p.categoria}
              </span>
              <span class="sku-badge">${p.codigo}</span>
            </div>

            <h3 class="product-title">${p.nombre}</h3>
            <p class="product-desc">${p.descripcion || 'Sin descripción adicional.'}</p>
          </div>

          <div>
            <div class="product-meta-row">
              <div class="price-block">
                <span class="price-unit">Precio por ${p.unidadMedida || 'unidad'}</span>
                <span class="price-value">${formattedPrice}</span>
              </div>
              <span class="stock-pill ${stockClass}">
                <span class="stock-dot"></span>
                <span>${stockLabel}</span>
              </span>
            </div>

            <div class="product-actions-bar">
              <button class="btn-card-edit" data-id="${p.id}" title="Editar con session.merge()">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                <span>Editar</span>
              </button>
              <button class="btn-card-delete" data-id="${p.id}" title="Eliminar con session.remove()">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                <span>Eliminar</span>
              </button>
            </div>
          </div>
        `;

        productsGrid.appendChild(card);
      });
    }

    updateStats();
  }

  // ========================================================
  // 9. EVENT LISTENERS: SEARCH & FILTERS
  // ========================================================
  let searchTimeout = null;
  searchInput.addEventListener('input', () => {
    const val = searchInput.value;
    btnClearSearch.style.display = val ? 'block' : 'none';

    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
      renderProducts();
      if (val.trim()) {
        logHibernateSql(
          'DAO: buscarPorNombre()',
          `FROM Producto p WHERE lower(p.nombre) LIKE :q`,
          `SELECT p1_0.id, p1_0.codigo, p1_0.nombre, p1_0.categoria, p1_0.precio, p1_0.stock FROM productos p1_0 WHERE lower(p1_0.nombre) LIKE ?`,
          [`'%${val.trim().toLowerCase()}%'`]
        );
      }
    }, 180);
  });

  btnClearSearch.addEventListener('click', () => {
    searchInput.value = '';
    btnClearSearch.style.display = 'none';
    renderProducts();
    searchInput.focus();
  });

  sortSelect.addEventListener('change', () => {
    renderProducts();
    logHibernateSql(
      'DAO: listarTodos() / Ordenamiento',
      `FROM Producto p ORDER BY ${sortSelect.value.startsWith('precio') ? 'p.precio' : 'p.nombre'}`,
      `SELECT p1_0.* FROM productos p1_0 ORDER BY ...`
    );
  });

  catChips.forEach(chip => {
    chip.addEventListener('click', () => {
      catChips.forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      activeCategory = chip.getAttribute('data-category');
      renderProducts();

      logHibernateSql(
        'DAO: filtrarPorCategoria()',
        activeCategory === 'TODAS' ? `FROM Producto p` : `FROM Producto p WHERE p.categoria = :cat`,
        activeCategory === 'TODAS' ? `SELECT * FROM productos` : `SELECT p1_0.* FROM productos p1_0 WHERE p1_0.categoria = ?`,
        activeCategory === 'TODAS' ? [] : [`'${activeCategory}'`]
      );
    });
  });

  stockToggles.forEach(toggle => {
    toggle.addEventListener('click', () => {
      stockToggles.forEach(t => t.classList.remove('active'));
      toggle.classList.add('active');
      activeStockFilter = toggle.getAttribute('data-stock');
      renderProducts();
    });
  });

  btnApplyPriceFilter.addEventListener('click', () => {
    renderProducts();
    showToast('Filtro de precio aplicado', 'info');
    logHibernateSql(
      'DAO: filtrarAvanzado() [Rango Precio]',
      `FROM Producto p WHERE p.precio BETWEEN :min AND :max`,
      `SELECT * FROM productos WHERE precio >= ? AND precio <= ?`,
      [minPriceInput.value || '0', maxPriceInput.value || 'MAX']
    );
  });

  function resetAllFilters() {
    searchInput.value = '';
    btnClearSearch.style.display = 'none';
    activeCategory = 'TODAS';
    catChips.forEach(c => c.classList.remove('active'));
    document.querySelector('.cat-chip[data-category="TODAS"]').classList.add('active');

    activeStockFilter = 'todos';
    stockToggles.forEach(t => t.classList.remove('active'));
    document.querySelector('.btn-stock-toggle[data-stock="todos"]').classList.add('active');

    minPriceInput.value = '';
    maxPriceInput.value = '';
    sortSelect.value = 'nombre-asc';

    renderProducts();
    showToast('Filtros restablecidos', 'info');
  }

  btnResetFilters.addEventListener('click', resetAllFilters);
  btnResetEmpty.addEventListener('click', resetAllFilters);

  // ========================================================
  // 10. MODAL: REGISTRAR & EDITAR (CRUD)
  // ========================================================
  btnOpenNewModal.addEventListener('click', () => {
    productForm.reset();
    formProductId.value = '';
    // Suggest next code
    formCodigo.value = `MKT-${String(nextId).padStart(3, '0')}`;
    modalTitle.textContent = 'Registrar Nuevo Producto';
    modalIconBadge.textContent = '➕';
    btnSaveText.textContent = 'Guardar con session.persist()';
    ormCodePreview.textContent = 'session.persist(nuevoProducto); // Hibernate INSERT INTO productos';

    productModalOverlay.classList.add('active');
    formNombre.focus();
  });

  function closeModal() {
    productModalOverlay.classList.remove('active');
  }

  btnCloseModal.addEventListener('click', closeModal);
  btnCancelModal.addEventListener('click', closeModal);
  productModalOverlay.addEventListener('click', (e) => {
    if (e.target === productModalOverlay) closeModal();
  });

  // Open Edit Modal
  productsGrid.addEventListener('click', (e) => {
    const editBtn = e.target.closest('.btn-card-edit');
    if (editBtn) {
      const id = parseInt(editBtn.getAttribute('data-id'));
      const prod = products.find(p => p.id === id);
      if (prod) {
        formProductId.value = prod.id;
        formCodigo.value = prod.codigo;
        formCategoria.value = prod.categoria;
        formNombre.value = prod.nombre;
        formPrecio.value = prod.precio;
        formStock.value = prod.stock;
        formUnidad.value = prod.unidadMedida;
        formDescripcion.value = prod.descripcion || '';

        modalTitle.textContent = 'Editar Producto de Mercado';
        modalIconBadge.textContent = '✏️';
        btnSaveText.textContent = 'Actualizar con session.merge()';
        ormCodePreview.textContent = `session.merge(productoId_${prod.id}); // Hibernate UPDATE productos SET ...`;

        productModalOverlay.classList.add('active');
      }
      return;
    }

    const deleteBtn = e.target.closest('.btn-card-delete');
    if (deleteBtn) {
      const id = parseInt(deleteBtn.getAttribute('data-id'));
      const prod = products.find(p => p.id === id);
      if (prod) {
        targetDeleteId = prod.id;
        deleteProductPreview.innerHTML = `<strong>${prod.codigo}</strong> — ${prod.nombre} (${prod.categoria}) · Stock: ${prod.stock}`;
        deleteModalOverlay.classList.add('active');
      }
    }
  });

  // Handle Form Submit (Create or Update)
  productForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const id = formProductId.value ? parseInt(formProductId.value) : null;
    const codigo = formCodigo.value.trim();
    const categoria = formCategoria.value;
    const nombre = formNombre.value.trim();
    const precio = parseFloat(formPrecio.value);
    const stock = parseInt(formStock.value);
    const unidadMedida = formUnidad.value;
    const descripcion = formDescripcion.value.trim();

    // Validations (Service Layer Rule Check)
    if (!codigo || !categoria || !nombre || isNaN(precio) || isNaN(stock) || !unidadMedida) {
      showToast('Por favor completa todos los campos requeridos (*)', 'warning');
      return;
    }

    if (precio <= 0) {
      showToast('El precio debe ser mayor a $0', 'warning');
      return;
    }

    if (stock < 0) {
      showToast('El stock no puede ser negativo', 'warning');
      return;
    }

    // Code uniqueness check
    const existingCode = products.find(p => p.codigo.toLowerCase() === codigo.toLowerCase() && p.id !== id);
    if (existingCode) {
      showToast(`El código ${codigo} ya está asignado a otro producto`, 'danger');
      return;
    }

    if (id) {
      // UPDATE (session.merge)
      const index = products.findIndex(p => p.id === id);
      if (index !== -1) {
        products[index] = {
          ...products[index],
          codigo,
          categoria,
          nombre,
          precio,
          stock,
          unidadMedida,
          descripcion
        };

        logHibernateSql(
          'DAO: actualizar() / session.merge()',
          `session.merge(producto)`,
          `UPDATE productos SET codigo=?, nombre=?, categoria=?, precio=?, stock=?, unidad_medida=?, descripcion=? WHERE id=?`,
          [codigo, nombre, categoria, precio, stock, unidadMedida, descripcion, id]
        );

        showToast(`✔ Producto '${nombre}' actualizado correctamente`, 'success');
      }
    } else {
      // CREATE (session.persist)
      const newProduct = {
        id: nextId++,
        codigo,
        categoria,
        nombre,
        precio,
        stock,
        unidadMedida,
        descripcion
      };

      products.unshift(newProduct);

      logHibernateSql(
        'DAO: guardar() / session.persist()',
        `session.persist(nuevoProducto)`,
        `INSERT INTO productos (codigo, nombre, categoria, precio, stock, unidad_medida, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?)`,
        [codigo, nombre, categoria, precio, stock, unidadMedida, descripcion]
      );

      showToast(`✔ Producto '${nombre}' guardado en base de datos con ID ${newProduct.id}`, 'success');
    }

    closeModal();
    renderProducts();
  });

  // ========================================================
  // 11. MODAL: ELIMINAR (session.remove)
  // ========================================================
  function closeDeleteModal() {
    deleteModalOverlay.classList.remove('active');
    targetDeleteId = null;
  }

  btnCancelDelete.addEventListener('click', closeDeleteModal);
  deleteModalOverlay.addEventListener('click', (e) => {
    if (e.target === deleteModalOverlay) closeDeleteModal();
  });

  btnConfirmDelete.addEventListener('click', () => {
    if (targetDeleteId) {
      const prod = products.find(p => p.id === targetDeleteId);
      const nombre = prod ? prod.nombre : `#${targetDeleteId}`;

      products = products.filter(p => p.id !== targetDeleteId);

      logHibernateSql(
        'DAO: eliminar() / session.remove()',
        `session.remove(session.get(Producto.class, ${targetDeleteId}))`,
        `DELETE FROM productos WHERE id = ?`,
        [targetDeleteId]
      );

      showToast(`✔ Producto '${nombre}' eliminado del inventario`, 'danger');
      closeDeleteModal();
      renderProducts();
    }
  });

  // ========================================================
  // 12. DRAWER: ARQUITECTURA POR CAPAS (NETBEANS EXPLORER)
  // ========================================================
  btnToggleLayersPanel.addEventListener('click', () => {
    layersDrawer.classList.add('active');
  });

  btnCloseDrawer.addEventListener('click', () => {
    layersDrawer.classList.remove('active');
  });

  // ========================================================
  // 13. TOAST NOTIFICATION HELPER
  // ========================================================
  const toastContainer = document.getElementById('toastContainer');
  function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let icon = 'ℹ️';
    if (type === 'success') icon = '✔';
    if (type === 'warning') icon = '⚠️';
    if (type === 'danger') icon = '🗑️';

    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  }

  // Initial render
  renderProducts();

});
