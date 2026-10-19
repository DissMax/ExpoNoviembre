package com.mercado.dao;

import com.mercado.model.Proveedor;
import com.mercado.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la Capa DAO para Proveedores utilizando Hibernate ORM.
 * Gestiona transacciones, sesiones y consultas en HQL (Hibernate Query Language).
 */
public class ProveedorDAOImpl implements IProveedorDAO {

    @Override
    public void guardar(Proveedor proveedor) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(proveedor);
            tx.commit();
            System.out.println(">>> [DAO-PROVEEDOR] Proveedor persistido exitosamente con ID: " + proveedor.getId());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO-PROVEEDOR] Error al guardar proveedor: " + e.getMessage());
            throw new RuntimeException("Error al guardar proveedor", e);
        }
    }

    @Override
    public void actualizar(Proveedor proveedor) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(proveedor);
            tx.commit();
            System.out.println(">>> [DAO-PROVEEDOR] Proveedor actualizado: ID " + proveedor.getId());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO-PROVEEDOR] Error al actualizar proveedor: " + e.getMessage());
            throw new RuntimeException("Error al actualizar proveedor", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Proveedor proveedor = session.get(Proveedor.class, id);
            if (proveedor != null) {
                session.remove(proveedor);
                System.out.println(">>> [DAO-PROVEEDOR] Proveedor eliminado: ID " + id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO-PROVEEDOR] Error al eliminar proveedor: " + e.getMessage());
            throw new RuntimeException("Error al eliminar proveedor", e);
        }
    }

    @Override
    public Proveedor buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Proveedor.class, id);
        } catch (Exception e) {
            System.err.println(">>> [DAO-PROVEEDOR] Error al buscar proveedor por ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Proveedor buscarPorNit(String nit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Proveedor pr WHERE pr.nit = :nit";
            Query<Proveedor> query = session.createQuery(hql, Proveedor.class);
            query.setParameter("nit", nit);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println(">>> [DAO-PROVEEDOR] Error al buscar por NIT: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Proveedor> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Proveedor pr ORDER BY pr.razonSocial ASC";
            Query<Proveedor> query = session.createQuery(hql, Proveedor.class);
            return query.list();
        } catch (Exception e) {
            System.err.println(">>> [DAO-PROVEEDOR] Error al listar proveedores: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public long contarProductosPorProveedor(Long proveedorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(p) FROM Producto p JOIN p.proveedores pr WHERE pr.id = :proveedorId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("proveedorId", proveedorId);
            Long count = query.uniqueResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            System.err.println(">>> [DAO-PROVEEDOR] Error al contar productos del proveedor: " + e.getMessage());
            return 0L;
        }
    }
}
