package com.mercado.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Clase Utilitaria para la gestión del SessionFactory de Hibernate (Patrón
 * Singleton).
 * Capa de Utilidades (Util).
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        // Inicializa el registro de servicios estándar leyendo hibernate.cfg.xml
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
            System.out.println(">>> [HibernateUtil] SessionFactory inicializado exitosamente.");
        } catch (Exception ex) {
            StandardServiceRegistryBuilder.destroy(registry);
            System.err.println(">>> [HibernateUtil] Error crítico al inicializar SessionFactory: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Retorna la instancia única de SessionFactory.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Cierra los pools de conexión y libera memoria al detener la aplicación.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println(">>> [HibernateUtil] SessionFactory cerrado correctamente.");
        }
    }
}
