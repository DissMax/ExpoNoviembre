package com.mercado.service;

import com.mercado.dao.CategoriaDAOImpl;
import com.mercado.dao.ICategoriaDAO;
import com.mercado.model.Categoria;

import java.util.List;

/**
 * Implementación de la Capa de Lógica de Negocio (Service) para Categoria.
 * Valida unicidad, integridad referencial y gestiona reglas comerciales.
 */
public class CategoriaServiceImpl implements ICategoriaService {

    private final ICategoriaDAO categoriaDAO;

    public CategoriaServiceImpl() {
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    // Constructor para inyección de dependencias / testing
    public CategoriaServiceImpl(ICategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    @Override
    public void registrarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Los datos de la categoría no pueden ser nulos.");
        }

        String nombre = categoria.getNombre();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }

        nombre = nombre.trim();
        if (nombre.length() < 3) {
            throw new IllegalArgumentException("El nombre de la categoría debe tener al menos 3 caracteres.");
        }

        // Validación de unicidad de nombre
        Categoria existente = categoriaDAO.buscarPorNombre(nombre);
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe una categoría registrada con el nombre: '" + nombre + "'.");
        }

        // Generar código único si no se especificó
        if (categoria.getCodigo() == null || categoria.getCodigo().trim().isEmpty()) {
            List<Categoria> todas = categoriaDAO.listarTodas();
            String codigoGenerado = "CAT-" + String.format("%03d", todas.size() + 1);
            categoria.setCodigo(codigoGenerado);
        } else {
            categoria.setCodigo(categoria.getCodigo().trim().toUpperCase());
            Categoria codExistente = categoriaDAO.buscarPorCodigo(categoria.getCodigo());
            if (codExistente != null) {
                throw new IllegalArgumentException("El código de categoría '" + categoria.getCodigo() + "' ya está en uso.");
            }
        }

        // Icono y color por defecto si no vienen especificados
        if (categoria.getIcono() == null || categoria.getIcono().trim().isEmpty()) {
            categoria.setIcono("📦");
        }
        if (categoria.getColorBadge() == null || categoria.getColorBadge().trim().isEmpty()) {
            categoria.setColorBadge("cat-abarrotes");
        }

        categoriaDAO.guardar(categoria);
    }

    @Override
    public void modificarCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new IllegalArgumentException("Se requiere el identificador de la categoría para actualizar.");
        }

        Categoria existente = categoriaDAO.buscarPorId(categoria.getId());
        if (existente == null) {
            throw new IllegalArgumentException("No se encontró la categoría con ID: " + categoria.getId());
        }

        String nombre = categoria.getNombre();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }

        nombre = nombre.trim();
        // Verificar que no choque con otra categoría de diferente ID
        Categoria porNombre = categoriaDAO.buscarPorNombre(nombre);
        if (porNombre != null && !porNombre.getId().equals(categoria.getId())) {
            throw new IllegalArgumentException("Ya existe otra categoría con el nombre: '" + nombre + "'.");
        }

        existente.setNombre(nombre);
        existente.setDescripcion(categoria.getDescripcion());
        if (categoria.getIcono() != null && !categoria.getIcono().isBlank()) {
            existente.setIcono(categoria.getIcono().trim());
        }
        if (categoria.getColorBadge() != null && !categoria.getColorBadge().isBlank()) {
            existente.setColorBadge(categoria.getColorBadge().trim());
        }

        categoriaDAO.actualizar(existente);
    }

    @Override
    public void eliminarCategoria(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID de categoría requerido.");
        }

        Categoria c = categoriaDAO.buscarPorId(id);
        if (c == null) {
            throw new IllegalArgumentException("La categoría con ID " + id + " no existe.");
        }

        // Validación de integridad referencial: No eliminar si tiene productos asociados
        long productosAsociados = categoriaDAO.contarProductosPorCategoria(id);
        if (productosAsociados > 0) {
            throw new IllegalStateException("No se puede eliminar la categoría '" + c.getNombre() + 
                    "' porque tiene " + productosAsociados + " producto(s) asignado(s). Reasigna los productos primero.");
        }

        categoriaDAO.eliminar(id);
    }

    @Override
    public Categoria obtenerPorId(Long id) {
        return categoriaDAO.buscarPorId(id);
    }

    @Override
    public Categoria obtenerPorNombre(String nombre) {
        return categoriaDAO.buscarPorNombre(nombre);
    }

    @Override
    public Categoria obtenerPorCodigo(String codigo) {
        return categoriaDAO.buscarPorCodigo(codigo);
    }

    @Override
    public List<Categoria> obtenerTodas() {
        return categoriaDAO.listarTodas();
    }

    @Override
    public long contarProductosPorCategoria(Long categoriaId) {
        return categoriaDAO.contarProductosPorCategoria(categoriaId);
    }

    @Override
    public void inicializarCategoriasPorDefecto() {
        List<Categoria> existentes = categoriaDAO.listarTodas();
        if (existentes != null && !existentes.isEmpty()) {
            return; // Ya existen categorías en la base de datos
        }

        System.out.println(">>> [SERVICE Categoria] Tabla 'categorias' vacía. Sembrando categorías comerciales estándar...");

        Categoria[] base = new Categoria[] {
            new Categoria("CAT-001", "Frutas y Verduras", "Frutas, hortalizas y verduras frescas del campo", "🍎", "cat-frutas"),
            new Categoria("CAT-002", "Lácteos y Huevos", "Leche, quesos campesinos, yogures y huevos de granja", "🧀", "cat-lacteos"),
            new Categoria("CAT-003", "Carnes y Aves", "Cortes de res, pollo fresco y carnes seleccionadas", "🥩", "cat-carnes"),
            new Categoria("CAT-004", "Abarrotes y Granos", "Arroces, aceites, frijoles y alimentos no perecederos", "🌾", "cat-abarrotes"),
            new Categoria("CAT-005", "Panadería", "Panes tajados, pastelería y productos horneados", "🥖", "cat-panaderia"),
            new Categoria("CAT-006", "Bebidas e Infusiones", "Cafés especiales, tés, jugos e infusiones", "🧃", "cat-bebidas"),
            new Categoria("CAT-007", "Aseo y Limpieza", "Detergentes, jabones y artículos para el hogar", "🧼", "cat-aseo")
        };

        for (Categoria c : base) {
            try {
                categoriaDAO.guardar(c);
            } catch (Exception e) {
                System.err.println(">>> Error al sembrar categoría " + c.getNombre() + ": " + e.getMessage());
            }
        }
        System.out.println(">>> [SERVICE Categoria] 7 categorías iniciales creadas exitosamente con Hibernate.");
    }
}
