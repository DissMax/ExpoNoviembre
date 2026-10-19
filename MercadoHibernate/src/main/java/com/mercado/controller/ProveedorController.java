package com.mercado.controller;

import com.mercado.model.Proveedor;
import com.mercado.service.IProveedorService;
import com.mercado.service.ProveedorServiceImpl;

import java.util.List;

/**
 * Controlador para la gestión de Proveedores (@ManyToMany con Producto).
 * Capa de Controlador (Controller).
 */
public class ProveedorController {

    private final IProveedorService service;

    public ProveedorController(IProveedorService service) {
        this.service = service;
    }

    public ProveedorController() {
        this(new ProveedorServiceImpl());
    }

    public String registrarProveedor(Proveedor proveedor) {
        return service.registrarProveedor(proveedor);
    }

    public String actualizarProveedor(Proveedor proveedor) {
        return service.actualizarProveedor(proveedor);
    }

    public String eliminarProveedor(Long id) {
        return service.eliminarProveedor(id);
    }

    public Proveedor buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    public Proveedor buscarPorNit(String nit) {
        return service.buscarPorNit(nit);
    }

    public List<Proveedor> listarProveedores() {
        return service.listarProveedores();
    }

    public void inicializarProveedoresPorDefecto() {
        service.inicializarProveedoresPorDefecto();
    }
}
