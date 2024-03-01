package models;
import java.sql.Timestamp;
import java.util.Date;

public class Product {
    private int productId;
    private String productName;
    private int quantity;
    private java.sql.Date  expirationDate;
    private Timestamp modifiedAt;
    private int version;


    // Constructors
    public Product() {
    }

    public Product(int productId, String productName, int quantity, java.sql.Date  expirationDate) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;

    }
    public Product(String productName, int quantity, java.sql.Date  expirationDate) {
        this.productName = productName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }
    // Getter and Setter methods
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public java.sql.Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(java.sql.Date  expirationDate) {
        this.expirationDate = expirationDate;
    }


    public Timestamp getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Timestamp modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Product{" +
                // Existing fields...
                ", modifiedAt=" + modifiedAt +
                ", version=" + version +
                '}';
    }


}
