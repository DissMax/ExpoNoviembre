package com.mercado.web;

import com.google.gson.*;
import com.mercado.controller.CategoriaController;
import com.mercado.controller.ProductoController;
import com.mercado.controller.ProveedorController;
import com.mercado.model.Categoria;
import com.mercado.model.FichaNutricional;
import com.mercado.model.Producto;
import com.mercado.model.Proveedor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;

/**
 * Servidor Web HTTP embebido y ligero (basado en com.sun.net.httpserver).
 * Expone la API REST JSON conectada a los controladores de Producto, Categoria y Proveedor
 * y sirve la interfaz Single Page Application (SPA).
 */
public class MercadoHttpServer {

    private static final int DEFAULT_PORT = 8080;
    private static final Deque<String> LOGS = new ConcurrentLinkedDeque<>();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                if (json == null || json.isJsonNull()) return null;
                String val = json.getAsString().trim();
                if (val.isEmpty()) return null;
                try {
                    return LocalDateTime.parse(val);
                } catch (Exception e) {
                    try {
                        return LocalDateTime.parse(val.replace(" ", "T"));
                    } catch (Exception e2) {
                        return LocalDateTime.now();
                    }
                }
            })
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
                if (json == null || json.isJsonNull()) return null;
                String val = json.getAsString().trim();
                if (val.isEmpty()) return null;
                return LocalDate.parse(val);
            })
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    private final ProductoController controller;
    private final CategoriaController categoriaController;
    private final ProveedorController proveedorController;
    private HttpServer server;
    private int activePort;

    public MercadoHttpServer(ProductoController controller, CategoriaController categoriaController, ProveedorController proveedorController) {
        this.controller = controller;
        this.categoriaController = categoriaController;
        this.proveedorController = proveedorController;
    }

    public MercadoHttpServer(ProductoController controller, CategoriaController categoriaController) {
        this(controller, categoriaController, new ProveedorController());
    }

    public MercadoHttpServer(ProductoController controller) {
        this(controller, new CategoriaController(), new ProveedorController());
    }

    public static void log(String tag, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String entry = "[" + timestamp + "] [" + tag + "] " + message;
        LOGS.addLast(entry);
        while (LOGS.size() > 150) {
            LOGS.pollFirst();
        }
        System.out.println(">>> " + entry);
    }

    public int start() throws IOException {
        int port = DEFAULT_PORT;
        HttpServer s = null;
        for (int p = DEFAULT_PORT; p <= DEFAULT_PORT + 10; p++) {
            try {
                s = HttpServer.create(new InetSocketAddress(p), 0);
                port = p;
                break;
            } catch (IOException e) {
                // Puerto ocupado, probar el siguiente
            }
        }

        if (s == null) {
            throw new IOException("No se pudo enlazar ningún puerto entre " + DEFAULT_PORT + " y " + (DEFAULT_PORT + 10));
        }

        this.server = s;
        this.activePort = port;

        // Inicializar categorías base si la tabla está vacía
        try {
            categoriaController.inicializarCategoriasPorDefecto();
        } catch (Exception e) {
            log("INIT", "Aviso al inicializar categorías: " + e.getMessage());
        }

        // Inicializar proveedores base si la tabla está vacía
        try {
            proveedorController.inicializarProveedoresPorDefecto();
        } catch (Exception e) {
            log("INIT", "Aviso al inicializar proveedores: " + e.getMessage());
        }

        // Contextos / Rutas HTTP
        server.createContext("/", new StaticHtmlHandler());
        server.createContext("/proveedores", new StaticHtmlHandler());
        server.createContext("/proveedores.html", new StaticHtmlHandler());
        server.createContext("/api/productos", new ProductosHandler());
        server.createContext("/api/productos/eliminar", new EliminarHandler());
        server.createContext("/api/categorias", new CategoriasHandler());
        server.createContext("/api/categorias/eliminar", new EliminarCategoriaHandler());
        server.createContext("/api/proveedores", new ProveedoresHandler());
        server.createContext("/api/proveedores/eliminar", new EliminarProveedorHandler());
        server.createContext("/api/estadisticas", new EstadisticasHandler());
        server.createContext("/api/logs", new LogsHandler());
        server.createContext("/api/logs/clear", new ClearLogsHandler());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        log("WEB-SERVER", "Servidor Web HTTP iniciado exitosamente en http://localhost:" + activePort);
        return activePort;
    }

    public void openBrowser() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:" + activePort));
            } else {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", "http://localhost:" + activePort});
            }
        } catch (Exception e) {
            log("WEB-SERVER", "Abre manualmente tu navegador en: http://localhost:" + activePort);
        }
    }

    // Handler para servir las páginas HTML (Productos / Proveedores)
    private static class StaticHtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String path = exchange.getRequestURI().getPath();
                String resource = (path.equals("/proveedores") || path.equals("/proveedores.html"))
                        ? "/web/proveedores.html"
                        : "/web/index.html";

                String html = cargarHtml(resource);
                byte[] bytes = html.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception e) {
                log("ERROR", "Error al servir HTML: " + e.getMessage());
                byte[] err = ("<h1>Error interno al cargar la aplicación: " + e.getMessage() + "</h1>").getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, err.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(err);
                }
            }
        }

        private static String cargarHtml(String resourcePath) throws IOException {
            try (InputStream is = MercadoHttpServer.class.getResourceAsStream(resourcePath)) {
                if (is != null) {
                    return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
            return "<html><body><h1>Error: No se encontró el recurso " + resourcePath + "</h1></body></html>";
        }
    }

    // Handler para GET /api/productos y POST /api/productos
    private class ProductosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(method)) {
                    URI uri = exchange.getRequestURI();
                    Map<String, String> params = parseQueryParams(uri.getQuery());
                    String cat = params.get("categoria");
                    String q = params.get("q");

                    List<Producto> lista;
                    if (cat != null && !cat.isBlank() && !cat.equalsIgnoreCase("TODAS")) {
                        lista = controller.filtrarPorCategoria(cat);
                        log("HIBERNATE", "HQL ejecutado: Filtrar por categoría '" + cat + "' -> " + lista.size() + " productos encontrados.");
                    } else if (q != null && !q.isBlank()) {
                        lista = controller.buscarPorNombre(q);
                        log("HIBERNATE", "HQL ejecutado: Búsqueda por término '" + q + "' -> " + lista.size() + " coincidencias.");
                    } else {
                        lista = controller.listarProductos();
                        log("HIBERNATE", "HQL ejecutado: Obtener catálogo completo -> " + lista.size() + " productos.");
                    }

                    sendJsonResponse(exchange, 200, lista);

                } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        JsonObject json = GSON.fromJson(body, JsonObject.class);
                        Producto p = GSON.fromJson(body, Producto.class);

                        // Resolución de la entidad Categoria para la relación @ManyToOne
                        Categoria catAsignada = null;
                        if (json.has("categoriaId") && !json.get("categoriaId").isJsonNull()) {
                            try {
                                Long catId = json.get("categoriaId").getAsLong();
                                catAsignada = categoriaController.buscarPorId(catId);
                            } catch (Exception ignored) {}
                        }

                        if (catAsignada == null && json.has("categoria")) {
                            if (json.get("categoria").isJsonObject()) {
                                JsonObject catObj = json.getAsJsonObject("categoria");
                                if (catObj.has("id")) {
                                    catAsignada = categoriaController.buscarPorId(catObj.get("id").getAsLong());
                                }
                            } else if (json.get("categoria").isJsonPrimitive()) {
                                String catNombre = json.get("categoria").getAsString();
                                catAsignada = categoriaController.buscarPorNombre(catNombre);
                            }
                        }

                        if (catAsignada != null) {
                            p.setCategoria(catAsignada);
                        }

                        // Resolución de la entidad FichaNutricional para la relación @OneToOne
                        FichaNutricional fn = null;
                        if (json.has("fichaNutricional") && json.get("fichaNutricional").isJsonObject()) {
                            fn = GSON.fromJson(json.getAsJsonObject("fichaNutricional"), FichaNutricional.class);
                        } else if (json.has("calorias") && !json.get("calorias").isJsonNull()) {
                            fn = new FichaNutricional();
                            try { fn.setCalorias(json.get("calorias").getAsInt()); } catch (Exception ignored) {}
                            if (json.has("proteinas") && !json.get("proteinas").isJsonNull()) try { fn.setProteinas(json.get("proteinas").getAsDouble()); } catch (Exception ignored) {}
                            if (json.has("grasasTotales") && !json.get("grasasTotales").isJsonNull()) try { fn.setGrasasTotales(json.get("grasasTotales").getAsDouble()); } catch (Exception ignored) {}
                            if (json.has("azucares") && !json.get("azucares").isJsonNull()) try { fn.setAzucares(json.get("azucares").getAsDouble()); } catch (Exception ignored) {}
                            if (json.has("sodio") && !json.get("sodio").isJsonNull()) try { fn.setSodio(json.get("sodio").getAsDouble()); } catch (Exception ignored) {}
                            if (json.has("alergenos") && !json.get("alergenos").isJsonNull()) fn.setAlergenos(json.get("alergenos").getAsString());
                            if (json.has("registroInvima") && !json.get("registroInvima").isJsonNull()) fn.setRegistroInvima(json.get("registroInvima").getAsString());
                        }

                        if (fn != null) {
                            p.setFichaNutricional(fn);
                        }

                        // Resolución de la lista de Proveedores para la relación @ManyToMany
                        List<Proveedor> listaProvs = new ArrayList<>();
                        if (json.has("proveedoresIds") && json.get("proveedoresIds").isJsonArray()) {
                            for (JsonElement elem : json.getAsJsonArray("proveedoresIds")) {
                                try {
                                    Long provId = elem.getAsLong();
                                    Proveedor pr = proveedorController.buscarPorId(provId);
                                    if (pr != null) listaProvs.add(pr);
                                } catch (Exception ignored) {}
                            }
                            p.setProveedores(listaProvs);
                        } else if (json.has("proveedores") && json.get("proveedores").isJsonArray()) {
                            for (JsonElement elem : json.getAsJsonArray("proveedores")) {
                                try {
                                    if (elem.isJsonObject() && elem.getAsJsonObject().has("id")) {
                                        Long provId = elem.getAsJsonObject().get("id").getAsLong();
                                        Proveedor pr = proveedorController.buscarPorId(provId);
                                        if (pr != null) listaProvs.add(pr);
                                    } else if (elem.isJsonPrimitive()) {
                                        Long provId = elem.getAsLong();
                                        Proveedor pr = proveedorController.buscarPorId(provId);
                                        if (pr != null) listaProvs.add(pr);
                                    }
                                } catch (Exception ignored) {}
                            }
                            p.setProveedores(listaProvs);
                        }

                        String resp;
                        boolean esActualizacion = (p.getId() != null && p.getId() > 0);

                        if (esActualizacion) {
                            // Preservar ID de la ficha nutricional existente si es actualización
                            if (p.getFichaNutricional() != null && p.getFichaNutricional().getId() == null) {
                                Producto prodExistente = controller.buscarPorId(p.getId());
                                if (prodExistente != null && prodExistente.getFichaNutricional() != null) {
                                    p.getFichaNutricional().setId(prodExistente.getFichaNutricional().getId());
                                }
                            }
                            log("CONTROLLER", "Solicitud de actualización recibida para ID " + p.getId() + " - " + p.getNombre());
                            resp = controller.actualizarProducto(p);
                        } else {
                            log("CONTROLLER", "Solicitud de registro recibida para código: " + p.getCodigo() + " - " + p.getNombre());
                            resp = controller.registrarProducto(p);
                        }

                        boolean ok = resp.startsWith("SUCCESS");

                        Map<String, Object> map = new HashMap<>();
                        map.put("success", ok);
                        map.put("message", resp);

                        if (ok) {
                            log("DAO", esActualizacion 
                                    ? "Producto actualizado exitosamente en MySQL (session.merge): " + p.getCodigo()
                                    : "Producto persistido exitosamente en MySQL (session.persist): " + p.getCodigo());
                        } else {
                            log("SERVICE", "Validación rechazada: " + resp);
                        }

                        sendJsonResponse(exchange, ok ? 200 : 400, map);
                    } catch (Exception e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("success", false);
                        error.put("message", "Error al procesar: " + e.getMessage());
                        log("ERROR", "Fallo al procesar petición POST: " + e.getMessage());
                        sendJsonResponse(exchange, 400, error);
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                log("ERROR", "Fallo en ProductosHandler: " + e.getMessage());
                e.printStackTrace();
                sendJsonResponse(exchange, 500, Map.of("error", e.getMessage()));
            }
        }
    }

    // Handler para eliminar producto
    private class EliminarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
                String idStr = params.get("id");

                if (idStr == null || idStr.isBlank()) {
                    sendJsonResponse(exchange, 400, Map.of("success", false, "message", "ID requerido"));
                    return;
                }

                Long id = Long.parseLong(idStr);
                log("CONTROLLER", "Solicitud para eliminar producto ID: " + id);
                String resp = controller.eliminarProducto(id);
                boolean ok = resp.startsWith("SUCCESS");

                log("DAO", "Respuesta eliminación: " + resp);
                sendJsonResponse(exchange, ok ? 200 : 400, Map.of("success", ok, "message", resp));
            } catch (Exception e) {
                log("ERROR", "Fallo en EliminarHandler: " + e.getMessage());
                sendJsonResponse(exchange, 400, Map.of("success", false, "message", e.getMessage()));
            }
        }
    }

    // Handler para GET y POST /api/categorias (CRUD de Categorías)
    private class CategoriasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(method)) {
                    List<Categoria> lista = categoriaController.listarCategorias();
                    log("HIBERNATE", "HQL ejecutado: Listar categorías comerciales -> " + lista.size() + " registradas.");
                    sendJsonResponse(exchange, 200, lista);

                } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Categoria c = GSON.fromJson(body, Categoria.class);
                        String resp;
                        boolean esActualizacion = (c.getId() != null && c.getId() > 0);

                        if (esActualizacion) {
                            log("CONTROLLER", "Solicitud de actualización para categoría ID " + c.getId() + " - " + c.getNombre());
                            resp = categoriaController.actualizarCategoria(c);
                        } else {
                            log("CONTROLLER", "Solicitud de creación para nueva categoría: " + c.getNombre());
                            resp = categoriaController.registrarCategoria(c);
                        }

                        boolean ok = resp.startsWith("SUCCESS");
                        Map<String, Object> map = new HashMap<>();
                        map.put("success", ok);
                        map.put("message", resp);
                        map.put("categoria", c);

                        if (ok) {
                            log("DAO Categoria", esActualizacion
                                    ? "Categoría actualizada exitosamente con Hibernate (session.merge): " + c.getNombre()
                                    : "Categoría persistida exitosamente con Hibernate (session.persist): " + c.getNombre());
                        }

                        sendJsonResponse(exchange, ok ? 200 : 400, map);
                    } catch (Exception e) {
                        log("ERROR", "Fallo al procesar categoría: " + e.getMessage());
                        sendJsonResponse(exchange, 400, Map.of("success", false, "message", "Error: " + e.getMessage()));
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                log("ERROR", "Fallo en CategoriasHandler: " + e.getMessage());
                sendJsonResponse(exchange, 500, Map.of("error", e.getMessage()));
            }
        }
    }

    // Handler para eliminar categoría
    private class EliminarCategoriaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
                String idStr = params.get("id");

                if (idStr == null || idStr.isBlank()) {
                    sendJsonResponse(exchange, 400, Map.of("success", false, "message", "ID de categoría requerido"));
                    return;
                }

                Long id = Long.parseLong(idStr);
                log("CONTROLLER", "Solicitud para eliminar categoría ID: " + id);
                String resp = categoriaController.eliminarCategoria(id);
                boolean ok = resp.startsWith("SUCCESS");

                log("DAO Categoria", "Resultado eliminación categoría: " + resp);
                sendJsonResponse(exchange, ok ? 200 : 400, Map.of("success", ok, "message", resp));
            } catch (Exception e) {
                log("ERROR", "Fallo en EliminarCategoriaHandler: " + e.getMessage());
                sendJsonResponse(exchange, 400, Map.of("success", false, "message", e.getMessage()));
            }
        }
    }

    // Handler para listar y registrar/actualizar proveedores (@ManyToMany)
    private class ProveedoresHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(method)) {
                    List<Proveedor> lista = proveedorController.listarProveedores();
                    log("HIBERNATE", "HQL ejecutado: Listar proveedores comerciales -> " + lista.size() + " proveedores.");
                    sendJsonResponse(exchange, 200, lista);

                } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Proveedor pr = GSON.fromJson(body, Proveedor.class);
                        String resp;
                        boolean esActualizacion = (pr.getId() != null && pr.getId() > 0);

                        if (esActualizacion) {
                            log("CONTROLLER", "Solicitud de actualización para proveedor ID " + pr.getId() + " - " + pr.getRazonSocial());
                            resp = proveedorController.actualizarProveedor(pr);
                        } else {
                            log("CONTROLLER", "Solicitud de creación para nuevo proveedor: " + pr.getRazonSocial());
                            resp = proveedorController.registrarProveedor(pr);
                        }

                        boolean ok = resp.startsWith("SUCCESS");
                        Map<String, Object> map = new HashMap<>();
                        map.put("success", ok);
                        map.put("message", resp);
                        map.put("proveedor", pr);

                        if (ok) {
                            log("DAO Proveedor", esActualizacion
                                    ? "Proveedor actualizado exitosamente con Hibernate (session.merge): " + pr.getRazonSocial()
                                    : "Proveedor persistido exitosamente con Hibernate (session.persist): " + pr.getRazonSocial());
                        }

                        sendJsonResponse(exchange, ok ? 200 : 400, map);
                    } catch (Exception e) {
                        log("ERROR", "Fallo al procesar proveedor: " + e.getMessage());
                        sendJsonResponse(exchange, 400, Map.of("success", false, "message", "Error: " + e.getMessage()));
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                log("ERROR", "Fallo en ProveedoresHandler: " + e.getMessage());
                sendJsonResponse(exchange, 500, Map.of("error", e.getMessage()));
            }
        }
    }

    // Handler para eliminar proveedor
    private class EliminarProveedorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod()) && !"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
                String idStr = params.get("id");

                if (idStr == null || idStr.isBlank()) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    if (!body.isBlank()) {
                        try {
                            JsonObject json = GSON.fromJson(body, JsonObject.class);
                            if (json.has("id")) idStr = json.get("id").getAsString();
                        } catch (Exception ignored) {}
                    }
                }

                if (idStr == null || idStr.isBlank()) {
                    sendJsonResponse(exchange, 400, Map.of("success", false, "message", "ID de proveedor requerido"));
                    return;
                }

                Long id = Long.parseLong(idStr);
                log("CONTROLLER", "Solicitud para eliminar proveedor ID: " + id);
                String resp = proveedorController.eliminarProveedor(id);
                boolean ok = resp.startsWith("SUCCESS");

                log("DAO Proveedor", "Resultado eliminación proveedor: " + resp);
                sendJsonResponse(exchange, ok ? 200 : 400, Map.of("success", ok, "message", resp));
            } catch (Exception e) {
                log("ERROR", "Fallo en EliminarProveedorHandler: " + e.getMessage());
                sendJsonResponse(exchange, 400, Map.of("success", false, "message", e.getMessage()));
            }
        }
    }

    // Handler para estadísticas
    private class EstadisticasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Map<String, Object> stats = controller.obtenerEstadisticas();
                sendJsonResponse(exchange, 200, stats);
            } catch (Exception e) {
                log("ERROR", "Fallo en EstadisticasHandler: " + e.getMessage());
                sendJsonResponse(exchange, 500, Map.of("error", e.getMessage()));
            }
        }
    }

    // Handler para consultar logs
    private static class LogsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<String> list = new ArrayList<>(LOGS);
            sendJsonResponse(exchange, 200, list);
        }
    }

    // Handler para limpiar logs
    private static class ClearLogsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGS.clear();
            log("SISTEMA", "Consola de eventos reiniciada por el usuario.");
            sendJsonResponse(exchange, 200, Map.of("success", true));
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int status, Object data) throws IOException {
        String json = GSON.toJson(data);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isBlank()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length > 0) {
                String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String v = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
                map.put(k, v);
            }
        }
        return map;
    }
}
