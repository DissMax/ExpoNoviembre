package com.mercado.dao;

import com.mercado.model.Categoria;
import com.mercado.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la Capa de Acceso a Datos (DAO) para la entidad Categoria.
 * Utiliza Hibernate SessionFactory para gestionar transacciones y consultas HQL.
 */
public class CategoriaDAOImpl implements ICategoriaDAO {

    @Override
    public void guardar(Categoria categoria) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(categoria);
            tx.commit();
            System.out.println(">>> [DAO Categoria] session.persist() ejecutado exitosamente para: " + categoria.getNombre());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO Categoria] Error al guardar categoría: " + e.getMessage());
            throw new RuntimeException("Error en persistencia Hibernate: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Categoria categoria) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(categoria);
            tx.commit();
            System.out.println(">>> [DAO Categoria] session.merge() ejecutado exitosamente para: " + categoria.getNombre());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO Categoria] Error al actualizar categoría: " + e.getMessage());
            throw new RuntimeException("Error en actualización Hibernate: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Categoria c = session.get(Categoria.class, id);
            if (c != null) {
                session.remove(c);
                System.out.println(">>> [DAO Categoria] session.remove() ejecutado exitosamente para ID: " + id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println(">>> [DAO Categoria] Error al eliminar categoría ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Error al eliminar categoría con Hibernate: " + e.getMessage(), e);
        }
    }

    @Override
    public Categoria buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Categoria.class, id);
        } catch (Exception e) {
            System.err.println(">>> [DAO Categoria] Error al buscar por ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Categoria buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Categoria c WHERE lower(trim(c.nombre)) = lower(trim(:nombre))";
            Query<Categoria> query = session.createQuery(hql, Categoria.class);
            query.setParameter("nombre", nombre.trim());
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println(">>> [DAO Categoria] Error al buscar por nombre: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Categoria buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Categoria c WHERE c.codigo = :codigo";
            Query<Categoria> query = session.createQuery(hql, Categoria.class);
            query.setParameter("codigo", codigo.trim());
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println(">>> [DAO Categoria] Error al buscar por código: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Categoria> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Categoria c ORDER BY c.nombre ASC";
            Query<Categoria> query = session.createQuery(hql, Categoria.class);
            return query.list();
        } catch (Exception e) {
            System.err.println(">>> [DAO Categoria] Error al listar categorías: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public long contarProductosPorCategoria(Long categoriaId) {
        if (categoriaId == null) return 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(p) FROM Producto p WHERE p.categoria.id = :catId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("catId", categoriaId);
            Long count = query.uniqueResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            System.err.println(">>> [DAO Categoria] Error al contar productos por categoría: " + e.getMessage());
            return 0;
        }
    }
}
