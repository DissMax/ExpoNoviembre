package com.mercado.service;

import com.mercado.model.Proveedor;
import java.util.List;

/**
 * Interfaz de la Capa de Lógica de Negocio (Service) para Proveedores.
 */
public interface IProveedorService {

    String registrarProveedor(Proveedor proveedor);

    String actualizarProveedor(Proveedor proveedor);

    String eliminarProveedor(Long id);

    Proveedor buscarPorId(Long id);

    Proveedor buscarPorNit(String nit);

    List<Proveedor> listarProveedores();

    void inicializarProveedoresPorDefecto();
}
