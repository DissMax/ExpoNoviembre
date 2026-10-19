package com.mercado.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa a un Proveedor comercial del supermercado.
 * Relación Muchos a Muchos (@ManyToMany) con la entidad Producto.
 * Capa de Modelo (Model).
 */
@Entity
@Table(name = "proveedores")
public class Proveedor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nit", nullable = false, unique = true, length = 30)
    private String nit; // Ej: "900.123.456-7"

    @Column(name = "razon_social", nullable = false, length = 120)
    private String razonSocial; // Ej: "AgroBoyacá Distribuciones S.A.S."

    @Column(name = "contacto", length = 80)
    private String contacto; // Ej: "Ing. Carlos Mendoza"

    @Column(name = "telefono", length = 30)
    private String telefono; // Ej: "311 555 8920"

    @Column(name = "ciudad", length = 60)
    private String ciudad; // Ej: "Sogamoso", "Duitama", "Tunja", "Bogotá"

    @Column(name = "email", length = 100)
    private String email;

    /**
     * Relación inversa ManyToMany con Producto.
     * transient para evitar recursión infinita en serialización JSON (GSON).
     */
    @ManyToMany(mappedBy = "proveedores", fetch = FetchType.LAZY)
    private transient List<Producto> productos = new ArrayList<>();

    // Constructor por defecto obligatorio para JPA
    public Proveedor() {
    }

    public Proveedor(String nit, String razonSocial, String contacto, String telefono, String ciudad, String email) {
        this.nit = nit;
        this.razonSocial = razonSocial;
        this.contacto = contacto;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.email = email;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nit='" + nit + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
