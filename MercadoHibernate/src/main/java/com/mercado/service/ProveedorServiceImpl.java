package com.mercado.service;

import com.mercado.dao.IProveedorDAO;
import com.mercado.dao.ProveedorDAOImpl;
import com.mercado.model.Proveedor;

import java.util.List;

/**
 * Implementación de la Capa de Servicio para la entidad Proveedor.
 * Valida reglas de negocio, integridad de datos y gestiona operaciones con la Capa DAO.
 */
public class ProveedorServiceImpl implements IProveedorService {

    private final IProveedorDAO proveedorDAO;

    public ProveedorServiceImpl(IProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    public ProveedorServiceImpl() {
        this(new ProveedorDAOImpl());
    }

    @Override
    public String registrarProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            return "ERROR: El objeto proveedor no puede ser nulo.";
        }

        if (proveedor.getNit() == null || proveedor.getNit().trim().isEmpty()) {
            return "ERROR: El NIT del proveedor es obligatorio.";
        }

        String nitTrim = proveedor.getNit().trim();
        proveedor.setNit(nitTrim);

        if (proveedorDAO.buscarPorNit(nitTrim) != null) {
            return "ERROR: Ya existe un proveedor registrado con el NIT '" + nitTrim + "'.";
        }

        if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().isEmpty()) {
            return "ERROR: La razón social o nombre comercial es obligatoria.";
        }

        if (proveedor.getRazonSocial().trim().length() < 3) {
            return "ERROR: La razón social debe tener al menos 3 caracteres.";
        }

        proveedor.setRazonSocial(proveedor.getRazonSocial().trim());

        if (proveedor.getCiudad() == null || proveedor.getCiudad().trim().isEmpty()) {
            proveedor.setCiudad("Sogamoso");
        } else {
            proveedor.setCiudad(proveedor.getCiudad().trim());
        }

        if (proveedor.getTelefono() != null) {
            proveedor.setTelefono(proveedor.getTelefono().trim());
        }

        if (proveedor.getContacto() != null) {
            proveedor.setContacto(proveedor.getContacto().trim());
        }

        if (proveedor.getEmail() != null) {
            proveedor.setEmail(proveedor.getEmail().trim().toLowerCase());
        }

        try {
            proveedorDAO.guardar(proveedor);
            return "SUCCESS: Proveedor '" + proveedor.getRazonSocial() + "' registrado exitosamente.";
        } catch (Exception e) {
            return "ERROR al guardar proveedor: " + e.getMessage();
        }
    }

    @Override
    public String actualizarProveedor(Proveedor proveedor) {
        if (proveedor == null || proveedor.getId() == null) {
            return "ERROR: El ID del proveedor es obligatorio para actualizar.";
        }

        Proveedor existente = proveedorDAO.buscarPorId(proveedor.getId());
        if (existente == null) {
            return "ERROR: No se encontró el proveedor con ID " + proveedor.getId();
        }

        if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().length() < 3) {
            return "ERROR: La razón social debe tener al menos 3 caracteres.";
        }

        String nitNuevo = proveedor.getNit() != null ? proveedor.getNit().trim() : existente.getNit();
        if (!nitNuevo.equalsIgnoreCase(existente.getNit())) {
            Proveedor duplicado = proveedorDAO.buscarPorNit(nitNuevo);
            if (duplicado != null && !duplicado.getId().equals(proveedor.getId())) {
                return "ERROR: El NIT '" + nitNuevo + "' ya está registrado en otro proveedor.";
            }
        }

        existente.setNit(nitNuevo);
        existente.setRazonSocial(proveedor.getRazonSocial().trim());
        existente.setContacto(proveedor.getContacto() != null ? proveedor.getContacto().trim() : "");
        existente.setTelefono(proveedor.getTelefono() != null ? proveedor.getTelefono().trim() : "");
        existente.setCiudad(proveedor.getCiudad() != null && !proveedor.getCiudad().isBlank() ? proveedor.getCiudad().trim() : "Sogamoso");
        existente.setEmail(proveedor.getEmail() != null ? proveedor.getEmail().trim().toLowerCase() : "");

        try {
            proveedorDAO.actualizar(existente);
            return "SUCCESS: Proveedor '" + existente.getRazonSocial() + "' actualizado exitosamente.";
        } catch (Exception e) {
            return "ERROR al actualizar proveedor: " + e.getMessage();
        }
    }

    @Override
    public String eliminarProveedor(Long id) {
        if (id == null) {
            return "ERROR: El ID del proveedor es obligatorio.";
        }

        Proveedor existente = proveedorDAO.buscarPorId(id);
        if (existente == null) {
            return "ERROR: No existe el proveedor con ID " + id;
        }

        long productosAsociados = proveedorDAO.contarProductosPorProveedor(id);
        if (productosAsociados > 0) {
            return "ERROR: No se puede eliminar el proveedor '" + existente.getRazonSocial() + 
                   "' porque suministra actualmente " + productosAsociados + " producto(s). Desvincúlelo primero.";
        }

        try {
            proveedorDAO.eliminar(id);
            return "SUCCESS: Proveedor '" + existente.getRazonSocial() + "' eliminado de la base de datos.";
        } catch (Exception e) {
            return "ERROR al eliminar proveedor: " + e.getMessage();
        }
    }

    @Override
    public Proveedor buscarPorId(Long id) {
        if (id == null) return null;
        return proveedorDAO.buscarPorId(id);
    }

    @Override
    public Proveedor buscarPorNit(String nit) {
        if (nit == null || nit.trim().isEmpty()) return null;
        return proveedorDAO.buscarPorNit(nit.trim());
    }

    @Override
    public List<Proveedor> listarProveedores() {
        return proveedorDAO.listarTodos();
    }

    @Override
    public void inicializarProveedoresPorDefecto() {
        List<Proveedor> existentes = proveedorDAO.listarTodos();
        if (existentes.isEmpty()) {
            System.out.println(">>> [SERVICE-PROVEEDOR] Inicializando proveedores base de mercado...");
            proveedorDAO.guardar(new Proveedor("900.182.341-1", "AgroBoyacá S.A.S.", "Ing. Fernando Castro", "312 450 8921", "Sogamoso", "ventas@agroboyaca.com"));
            proveedorDAO.guardar(new Proveedor("900.293.812-4", "Lácteos del Altiplano Ltda.", "Dra. Marcela Reyes", "310 894 1234", "Duitama", "pedidos@lacteosaltiplano.co"));
            proveedorDAO.guardar(new Proveedor("900.410.552-8", "Carnes y Cortes San Martín", "Héctor Fabio Suárez", "315 721 3490", "Sogamoso", "comercial@carnessanmartin.com"));
            proveedorDAO.guardar(new Proveedor("900.589.601-3", "Abarrotes y Granos Sugamuxi", "Gloria Inés Patiño", "314 380 9102", "Tunja", "ventas@granosugamuxi.com"));
            proveedorDAO.guardar(new Proveedor("900.702.119-9", "Panificadora Central Boyacá", "Ricardo Morales", "318 642 7855", "Sogamoso", "panaderia@centralboyaca.com"));
            proveedorDAO.guardar(new Proveedor("900.834.770-2", "Distribuidora BioAseo Express", "Laura Camila Silva", "320 901 4422", "Bogotá", "contacto@bioaseoexpress.co"));
            System.out.println(">>> [SERVICE-PROVEEDOR] 6 proveedores base creados exitosamente.");
        }
    }
}
