package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.ProductDTO;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.Product;
import vn.vissoft.dashboard.repo.ProductRepo;
import vn.vissoft.dashboard.services.ProductService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;
    private BaseMapper<Product, ProductDTO> mapper = new BaseMapper<Product, ProductDTO>(Product.class, ProductDTO.class);

    @Override
    public List<Product> findAll() {
        return productRepo.findAll();
    }

    @Override
    public List<Product> getActiveProduct() {
        return productRepo.findActiveProduct();
    }


}
