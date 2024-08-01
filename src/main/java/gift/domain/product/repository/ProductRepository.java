package gift.domain.product.repository;

import gift.domain.category.entity.Category;
import gift.domain.product.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByCategory(Category category);
    Page<Product> findAllByCategory(Pageable pageable, Category category);
    void deleteByCategory(Category category);
}