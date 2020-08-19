package vn.vissoft.dashboard.model;

public class GropServiceFind {
    Long productName;
    Long code;
    Long name;
    Long productId;

    public GropServiceFind(Long productName, Long code, Long name, Long productId) {
        productName = productName;
        code = code;
        name = name;
        productId = productId;
    }

    public Long getProductName() {
        return productName;
    }

    public void setProductName(Long productName) {
        this.productName = productName;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
