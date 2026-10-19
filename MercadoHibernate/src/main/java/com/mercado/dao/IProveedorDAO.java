package com.mercado.dao;

import com.mercado.model.Proveedor;
import java.util.List;

/**
 * Interfaz de la Capa DAO para la entidad Proveedor.
 * Define las operaciones CRUD y consultas especializadas en Hibernate ORM.
 */
public interface IProveedorDAO {

    void guardar(Proveedor proveedor);

    void actualizar(Proveedor proveedor);

    void eliminar(Long id);

    Proveedor buscarPorId(Long id);

    Proveedor buscarPorNit(String nit);

    List<Proveedor> listarTodos();

    long contarProductosPorProveedor(Long proveedorId);
}
