# 🛒 MercadoHibernateApp - Proyecto Java ORM con Hibernate por Capas

Proyecto CRUD de productos de un mercado desarrollado bajo **Arquitectura por Capas tradicional de NetBeans / Java**, implementando el ORM **Hibernate 6** y especificación **Jakarta Persistence (JPA)**.

---

## 📁 Estructura del Proyecto por Capas

```
MercadoHibernateApp/
├── pom.xml                                   # Configuración de dependencias Maven (Hibernate, MySQL, H2)
├── nbproject/                                # Configuración de proyecto para Apache NetBeans
│   └── project.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── mercado/
        │           ├── model/                # 1. CAPA MODELO (Entidades JPA)
        │           │   ├── Producto.java     # @Entity, @Table, @Id, @Column, @GeneratedValue
        │           │   └── Categoria.java    # Enum de categorías comerciales
        │           ├── dao/                  # 2. CAPA DAO (Acceso a Datos con Hibernate)
        │           │   ├── IProductoDAO.java # Interfaz de operaciones CRUD y consultas HQL
        │           │   └── ProductoDAOImpl.java # Implementación con Session, Transaction y HQL
        │           ├── service/              # 3. CAPA SERVICIO (Lógica de Negocio)
        │           │   ├── IProductoService.java
        │           │   └── ProductoServiceImpl.java # Validaciones de negocio, precios y stock
        │           ├── controller/           # 4. CAPA CONTROLADOR (Orquestación)
        │           │   └── ProductoController.java # Manejo de respuestas y flujo de datos
        │           ├── util/                 # 5. CAPA UTILIDAD
        │           │   └── HibernateUtil.java # SessionFactory Singleton
        │           └── MainApp.java          # Clase ejecutable de prueba
        └── resources/
            ├── hibernate.cfg.xml             # Configuración de dialecto, show_sql y hbm2ddl
            └── database.sql                  # Script SQL de respaldo y datos iniciales
```

---

## ⚙️ Cómo Abrir y Ejecutar en Apache NetBeans

1. Abre **Apache NetBeans** (versión 12 o superior).
2. Ve al menú superior: **File -> Open Project...**
3. Selecciona la carpeta `MercadoHibernateApp`. NetBeans la reconocerá automáticamente como un proyecto Maven.
4. Espera que descargue las dependencias (`hibernate-core`, `mysql-connector-j`, `h2`).
5. Abre `com.mercado.MainApp.java`, haz clic derecho y selecciona **Run File (Shift + F6)**.

---

## 🗄️ Base de Datos

En `src/main/resources/hibernate.cfg.xml`:
* Por defecto se conecta a MySQL en `jdbc:mysql://localhost:3306/mercado_db`.
* La propiedad `<property name="hibernate.hbm2ddl.auto">update</property>` crea la tabla automáticamente sin necesidad de scripts manuales.
* Si no tienes MySQL instalado, en el mismo archivo viene comentada la configuración para **H2 In-Memory**, la cual corre 100% en memoria RAM sin necesidad de instalar ningún servidor de base de datos.
