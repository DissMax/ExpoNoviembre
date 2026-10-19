package com.mercado.dao;

import com.mercado.model.Producto;
import com.mercado.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la Capa DAO utilizando Hibernate ORM.
 * Gestiona transacciones, sesiones y consultas en HQL (Hibernate Query Language).
 */
public class ProductoDAOImpl implements IProductoDAO {

    @Override
    public void guardar(Producto producto) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // Hibernate inserta el registro y genera automáticamente el ID en la entidad
            session.persist(producto);
            tx.commit();
            System.out.println(">>> [DAO] Producto persistido exitosamente con ID: " + producto.getId());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO] Error al guardar producto: " + e.getMessage());
            throw new RuntimeException("Error al guardar producto", e);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // session.merge sincroniza el objeto desasociado con la sesión y ejecuta UPDATE
            session.merge(producto);
            tx.commit();
            System.out.println(">>> [DAO] Producto actualizado: ID " + producto.getId());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO] Error al actualizar producto: " + e.getMessage());
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Producto producto = session.get(Producto.class, id);
            if (producto != null) {
                session.remove(producto);
                System.out.println(">>> [DAO] Producto eliminado: ID " + id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO] Error al eliminar producto: " + e.getMessage());
            throw new RuntimeException("Error al eliminar producto", e);
        }
    }

    @Override
    public Producto buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // session.get consulta la clave primaria utilizando la caché de primer nivel
            return session.get(Producto.class, id);
        } catch (Exception e) {
            System.err.println(">>> [DAO] Error al buscar producto por ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Producto buscarPorCodigo(String codigo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Producto p WHERE p.codigo = :codigo";
            Query<Producto> query = session.createQuery(hql, Producto.class);
            query.setParameter("codigo", codigo);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println(">>> [DAO] Error al buscar por código: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Producto> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL orientado a la clase Producto con DISTINCT para evitar duplicados en relaciones ManyToMany
            String hql = "SELECT DISTINCT p FROM Producto p ORDER BY p.id DESC";
            Query<Producto> query = session.createQuery(hql, Producto.class);
            return query.list();
        } catch (Exception e) {
            System.err.println(">>> [DAO] Error al listar productos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Producto> buscarPorNombre(String termino) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT p FROM Producto p WHERE lower(p.nombre) LIKE :termino OR lower(p.descripcion) LIKE :termino ORDER BY p.nombre ASC";
            Query<Producto> query = session.createQuery(hql, Producto.class);
            query.setParameter("termino", "%" + termino.toLowerCase().trim() + "%");
            return query.list();
        } catch (Exception e) {
            System.err.println(">>> [DAO] Error al buscar por nombre: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Producto> filtrarPorCategoria(String categoria) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT p FROM Producto p WHERE (p.categoria.nombre = :categoria OR p.categoria.codigo = :categoria) ORDER BY p.nombre ASC";
            Query<Producto> query = session.createQuery(hql, Producto.class);
            query.setParameter("categoria", categoria);
            return query.list();
        } catch (Exception e) {
            System.err.println(">>> [DAO] Error al filtrar por categoría: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Producto> filtrarAvanzado(String nombre, String categoria, Double minPrecio, Double maxPrecio, Boolean soloEnStock) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("SELECT DISTINCT p FROM Producto p WHERE 1=1 ");

            if (nombre != null && !nombre.trim().isEmpty()) {
                hql.append("AND (lower(p.nombre) LIKE :nombre OR lower(p.codigo) LIKE :nombre) ");
            }
            if (categoria != null && !categoria.trim().isEmpty() && !categoria.equalsIgnoreCase("TODAS")) {
                hql.append("AND (p.categoria.nombre = :categoria OR p.categoria.codigo = :categoria) ");
            }
            if (minPrecio != null) {
                hql.append("AND p.precio >= :minPrecio ");
            }
            if (maxPrecio != null) {
                hql.append("AND p.precio <= :maxPrecio ");
            }
            if (soloEnStock != null && soloEnStock) {
                hql.append("AND p.stock > 0 ");
            }

            hql.append("ORDER BY p.nombre ASC");

            Query<Producto> query = session.createQuery(hql.toString(), Producto.class);

            if (nombre != null && !nombre.trim().isEmpty()) {
                query.setParameter("nombre", "%" + nombre.toLowerCase().trim() + "%");
            }
            if (categoria != null && !categoria.trim().isEmpty() && !categoria.equalsIgnoreCase("TODAS")) {
                query.setParameter("categoria", categoria);
            }
            if (minPrecio != null) {
                query.setParameter("minPrecio", minPrecio);
            }
            if (maxPrecio != null) {
                query.setParameter("maxPrecio", maxPrecio);
            }

            return query.list();
        } catch (Exception e) {
            System.err.println(">>> [DAO] Error en filtro avanzado: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
