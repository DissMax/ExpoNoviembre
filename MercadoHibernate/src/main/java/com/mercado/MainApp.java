package com.mercado;

import com.mercado.controller.ProductoController;
import com.mercado.model.Producto;
import com.mercado.util.HibernateUtil;
import com.mercado.web.MercadoHttpServer;

import java.util.List;

/**
 * Clase Principal Ejecutable (Main).
 * Inicia el Servidor Web integrado y abre la Interfaz Gráfica del Mercado en el navegador.
 */
public class MainApp {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   SISTEMA DE GESTIÓN DE MERCADO - ORM HIBERNATE POR CAPAS (WEB)");
        System.out.println("   Tecnólogo ADSO - SENA CIMM");
        System.out.println("==================================================================\n");

        ProductoController controller = new ProductoController();

        try {
            // Inicializar sesión de Hibernate y verificar conexión
            MercadoHttpServer.log("HIBERNATE", "Verificando conexión con MySQL 8 (localhost:3306/mercado_db)...");
            List<Producto> catalogo = controller.listarProductos();
            MercadoHttpServer.log("HIBERNATE", "Conexión exitosa. Catálogo cargado: " + catalogo.size() + " productos.");

            // Iniciar Servidor Web HTTP Integrado
            MercadoHttpServer webServer = new MercadoHttpServer(controller);
            int port = webServer.start();

            System.out.println("\n------------------------------------------------------------------");
            System.out.println("  >>> APLICACIÓN WEB INICIADA CON ÉXITO <<<");
            System.out.println("  Abre en tu navegador la siguiente URL:");
            System.out.println("  ===> http://localhost:" + port);
            System.out.println("------------------------------------------------------------------");

            // Abrir navegador automáticamente
            webServer.openBrowser();

            System.out.println("\n[INFO] El servidor web se encuentra activo.");
            System.out.println("[INFO] Puedes registrar, buscar, filtrar, actualizar y eliminar productos");
            System.out.println("[INFO] desde la interfaz web en tiempo real.");
            System.out.println("[INFO] Presiona Ctrl + C en esta consola para detener el servidor.\n");

            // Mantener el servidor activo
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Error durante la ejecución del sistema: " + e.getMessage());
            e.printStackTrace();
            HibernateUtil.shutdown();
        }
    }
}
