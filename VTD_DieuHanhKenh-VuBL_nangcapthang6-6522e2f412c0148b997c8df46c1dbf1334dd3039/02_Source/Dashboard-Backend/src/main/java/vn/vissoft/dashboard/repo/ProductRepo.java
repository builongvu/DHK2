package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>, ProductRepoCustom {

}

