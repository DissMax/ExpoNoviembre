package com.mercado.controller;

import com.mercado.model.Categoria;
import com.mercado.service.CategoriaServiceImpl;
import com.mercado.service.ICategoriaService;

import java.util.List;

/**
 * Capa de Control (Controller) para la entidad Categoria.
 * Recibe peticiones desde la Capa Web (MercadoHttpServer),
 * delega la lógica de negocio al servicio y formatea respuestas.
 */
public class CategoriaController {

    private final ICategoriaService service;

    public CategoriaController() {
        this.service = new CategoriaServiceImpl();
    }

    public CategoriaController(ICategoriaService service) {
        this.service = service;
    }

    public List<Categoria> listarCategorias() {
        return service.obtenerTodas();
    }

    public Categoria buscarPorId(Long id) {
        return service.obtenerPorId(id);
    }

    public Categoria buscarPorNombre(String nombre) {
        return service.obtenerPorNombre(nombre);
    }

    public String registrarCategoria(Categoria categoria) {
        try {
            service.registrarCategoria(categoria);
            return "SUCCESS: Categoría '" + categoria.getNombre() + "' (" + categoria.getCodigo() + ") registrada exitosamente.";
        } catch (IllegalArgumentException e) {
            return "ERROR: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: Fallo al persistir categoría en base de datos: " + e.getMessage();
        }
    }

    public String actualizarCategoria(Categoria categoria) {
        try {
            service.modificarCategoria(categoria);
            return "SUCCESS: Categoría '" + categoria.getNombre() + "' actualizada exitosamente.";
        } catch (IllegalArgumentException e) {
            return "ERROR: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: Fallo al actualizar categoría: " + e.getMessage();
        }
    }

    public String eliminarCategoria(Long id) {
        try {
            service.eliminarCategoria(id);
            return "SUCCESS: Categoría eliminada exitosamente de la base de datos.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "ERROR: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: Fallo al eliminar categoría: " + e.getMessage();
        }
    }

    public void inicializarCategoriasPorDefecto() {
        service.inicializarCategoriasPorDefecto();
    }
}
