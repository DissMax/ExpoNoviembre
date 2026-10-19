package com.mercado.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una categoría comercial de productos.
 * Capa de Modelo (Model).
 * 
 * Relación One-to-Many con la entidad Producto.
 */
@Entity
@Table(name = "categorias")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo; // Ej: "CAT-001"

    @Column(name = "nombre", nullable = false, unique = true, length = 80)
    private String nombre; // Ej: "Frutas y Verduras"

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "icono", length = 20)
    private String icono; // Emoji visual, ej: "🍎", "🧀", "🥩", "🌾"

    @Column(name = "color_badge", length = 40)
    private String colorBadge; // Clase CSS o color, ej: "cat-frutas", "cat-lacteos"

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    /**
     * Relación OneToMany: Una categoría contiene muchos productos.
     * transient para evitar recursión infinita en serialización JSON (GSON).
     */
    @OneToMany(mappedBy = "categoria", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private transient List<Producto> productos = new ArrayList<>();

    // Constructor por defecto requerido por JPA/Hibernate
    public Categoria() {
        this.fechaCreacion = LocalDateTime.now();
        this.icono = "📦";
        this.colorBadge = "cat-abarrotes";
    }

    // Constructor con campos principales
    public Categoria(String codigo, String nombre, String descripcion, String icono, String colorBadge) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = (icono != null && !icono.isBlank()) ? icono : "📦";
        this.colorBadge = (colorBadge != null && !colorBadge.isBlank()) ? colorBadge : "cat-abarrotes";
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getColorBadge() {
        return colorBadge;
    }

    public void setColorBadge(String colorBadge) {
        this.colorBadge = colorBadge;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", icono='" + icono + '\'' +
                '}';
    }
}
