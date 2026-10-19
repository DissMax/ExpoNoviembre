package com.mercado.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entidad JPA que representa la Ficha Nutricional y Registro Sanitario de un producto.
 * Relación Uno a Uno (@OneToOne) con la entidad Producto.
 * Capa de Modelo (Model).
 */
@Entity
@Table(name = "fichas_nutricionales")
public class FichaNutricional implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "calorias", nullable = false)
    private Integer calorias; // Kilocalorías (kcal) por porción

    @Column(name = "proteinas")
    private Double proteinas; // Gramos (g)

    @Column(name = "grasas_totales")
    private Double grasasTotales; // Gramos (g)

    @Column(name = "azucares")
    private Double azucares; // Gramos (g)

    @Column(name = "sodio")
    private Double sodio; // Miligramos (mg)

    @Column(name = "alergenos", length = 150)
    private String alergenos; // Ej: "Contiene lactosa", "Sin alérgenos", "Gluten"

    @Column(name = "registro_invima", length = 50, unique = true)
    private String registroInvima; // Ej: "RSA-001248-2024"

    /**
     * Relación inversa OneToOne con Producto.
     * transient para evitar recursión infinita en serialización JSON (GSON).
     */
    @OneToOne(mappedBy = "fichaNutricional")
    private transient Producto producto;

    // Constructor por defecto requerido por JPA/Hibernate
    public FichaNutricional() {
        this.calorias = 0;
        this.proteinas = 0.0;
        this.grasasTotales = 0.0;
        this.azucares = 0.0;
        this.sodio = 0.0;
        this.alergenos = "Ninguno";
        this.registroInvima = "N/A";
    }

    public FichaNutricional(Integer calorias, Double proteinas, Double grasasTotales, Double azucares, Double sodio, String alergenos, String registroInvima) {
        this.calorias = calorias != null ? calorias : 0;
        this.proteinas = proteinas != null ? proteinas : 0.0;
        this.grasasTotales = grasasTotales != null ? grasasTotales : 0.0;
        this.azucares = azucares != null ? azucares : 0.0;
        this.sodio = sodio != null ? sodio : 0.0;
        this.alergenos = (alergenos != null && !alergenos.isBlank()) ? alergenos : "Ninguno";
        this.registroInvima = (registroInvima != null && !registroInvima.isBlank()) ? registroInvima : "N/A";
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCalorias() {
        return calorias;
    }

    public void setCalorias(Integer calorias) {
        this.calorias = calorias;
    }

    public Double getProteinas() {
        return proteinas;
    }

    public void setProteinas(Double proteinas) {
        this.proteinas = proteinas;
    }

    public Double getGrasasTotales() {
        return grasasTotales;
    }

    public void setGrasasTotales(Double grasasTotales) {
        this.grasasTotales = grasasTotales;
    }

    public Double getAzucares() {
        return azucares;
    }

    public void setAzucares(Double azucares) {
        this.azucares = azucares;
    }

    public Double getSodio() {
        return sodio;
    }

    public void setSodio(Double sodio) {
        this.sodio = sodio;
    }

    public String getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(String alergenos) {
        this.alergenos = alergenos;
    }

    public String getRegistroInvima() {
        return registroInvima;
    }

    public void setRegistroInvima(String registroInvima) {
        this.registroInvima = registroInvima;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "FichaNutricional{" +
                "id=" + id +
                ", calorias=" + calorias +
                ", proteinas=" + proteinas + "g" +
                ", grasas=" + grasasTotales + "g" +
                ", azucares=" + azucares + "g" +
                ", invima='" + registroInvima + '\'' +
                '}';
    }
}
