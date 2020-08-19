package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.ProductDTO;
import vn.vissoft.dashboard.model.Product;

import java.util.List;

public interface ProductService {

    public List<Product> findAll();

    public List<Product> getActiveProduct();

}
