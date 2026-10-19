package com.mercado.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un producto del inventario del mercado.
 * Capa de Modelo (Model).
 * 
 * Relaciones ORM:
 * - Many-to-One con la entidad Categoria
 * - One-to-One con la entidad FichaNutricional
 * - Many-to-Many con la entidad Proveedor
 */
@Entity
@Table(name = "productos")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    /**
     * Relación Many-to-One con la entidad Categoria.
     * Clave foránea categoria_id en la tabla productos.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = true)
    private Categoria categoria;

    /**
     * Relación One-to-One con la entidad FichaNutricional.
     * Clave foránea ficha_id en la tabla productos (con restricción unique).
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ficha_id", referencedColumnName = "id", unique = true)
    private FichaNutricional fichaNutricional;

    /**
     * Relación Many-to-Many con la entidad Proveedor.
     * Utiliza la tabla intermedia productos_proveedores.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "productos_proveedores",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "proveedor_id")
    )
    private List<Proveedor> proveedores = new ArrayList<>();

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida; // Ej: "Kilo", "Litro", "Unidad", "Bolsa"

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    // Constructor por defecto obligatorio para JPA/Hibernate
    public Producto() {
        this.fechaIngreso = LocalDateTime.now();
    }

    // Constructor con entidad Categoria
    public Producto(String codigo, String nombre, Categoria categoria, Double precio, Integer stock, String unidadMedida, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.descripcion = descripcion;
        this.fechaIngreso = LocalDateTime.now();
    }

    // Constructor básico
    public Producto(String codigo, String nombre, Double precio, Integer stock, String unidadMedida, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.descripcion = descripcion;
        this.fechaIngreso = LocalDateTime.now();
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Retorna el nombre de la categoría para conveniencia y serialización.
     */
    public String getCategoriaNombre() {
        return categoria != null ? categoria.getNombre() : "Sin Categoría";
    }

    /**
     * Retorna el icono/emoji de la categoría.
     */
    public String getCategoriaIcono() {
        return (categoria != null && categoria.getIcono() != null) ? categoria.getIcono() : "📦";
    }

    /**
     * Retorna la clase de color badge de la categoría.
     */
    public String getCategoriaColor() {
        return (categoria != null && categoria.getColorBadge() != null) ? categoria.getColorBadge() : "cat-abarrotes";
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public FichaNutricional getFichaNutricional() {
        return fichaNutricional;
    }

    public void setFichaNutricional(FichaNutricional fichaNutricional) {
        this.fichaNutricional = fichaNutricional;
    }

    public List<Proveedor> getProveedores() {
        return proveedores;
    }

    public void setProveedores(List<Proveedor> proveedores) {
        this.proveedores = proveedores;
    }

    public void addProveedor(Proveedor proveedor) {
        if (proveedor != null && !this.proveedores.contains(proveedor)) {
            this.proveedores.add(proveedor);
        }
    }

    public void removeProveedor(Proveedor proveedor) {
        if (proveedor != null) {
            this.proveedores.remove(proveedor);
        }
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "null") +
                ", ficha=" + (fichaNutricional != null ? fichaNutricional.getCalorias() + "kcal" : "sin ficha") +
                ", proveedores=" + (proveedores != null ? proveedores.size() : 0) +
                ", precio=" + precio +
                ", stock=" + stock +
                ", unidadMedida='" + unidadMedida + '\'' +
                '}';
    }
}
