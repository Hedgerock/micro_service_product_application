package com.hedgerock.catalogue.entity;

import com.hedgerock.catalogue.payload.UpdateProductPayload;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "catalogue", name="t_product")
@Entity
@NamedQueries(
        @NamedQuery(
                name = "Product.findAllByTitleLikeIgnoringCase",
                query = "SELECT p FROM Product p WHERE p.title ILIKE :title"
        )
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="c_title")
    @NotNull
    @Size(min = 3, max = 50)
    private String title;

    @Column(name="c_details")
    @Size(max = 1000)
    private String details;


    public UpdateProductPayload getUpdatePayload() {
        return new UpdateProductPayload(this.title, this.details);
    }

}
