package m.furniture.M_f.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private double price;

    private String category;

    private String description;

    private Integer stock;

    private String imageUrl;

    @JsonIgnore // Ігноруємо це поле при серіалізації JSON
    @ManyToMany(mappedBy = "products")
    private List<Cart> carts = new ArrayList<>();
}