package com.hedgerock.catalogue.repository;

import com.hedgerock.catalogue.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends CrudRepository<Product, Long> {
    //native query
    //@Query(name="nativeQuery", value="SELECT * FROM catalogue.t_product WHERE c_title ILIKE :title", nativeQuery=true)
    //From Entity
    //@Query(name="Product.findAllByTitleLikeIgnoringCase")
    @Query(name="This is name", value = "SELECT p FROM Product p WHERE p.title ILIKE :title")
    Iterable<Product> findAllByTitleLikeIgnoreCase(@Param("title") String title);
}
