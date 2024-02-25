package models;

import services.ProductService;

public class Line {
    // Attributes
    private int line_id;
    private int line_quantity;
    private int basket_id;
    private int product_id;

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    // Constructors
    public Line(int line_id, int line_quantity, int basket_id, int product_id) {
        this.line_id = line_id;
        this.line_quantity = line_quantity;
        this.basket_id = basket_id;
        this.product_id = product_id;
    }

    public Line(){};
    // Getter methods
    public int getLineId() {
        return line_id;
    }

    public int getLineQuantity() {
        return line_quantity;
    }

    public int getBasketId() {
        return basket_id;
    }

    public int getProductId() {
        return product_id;
    }

    public void setLineId(int line_id) {
        this.line_id= line_id;
    }

    // Setter methods (if needed)
    public void setLineQuantity(int line_quantity) {
        this.line_quantity = line_quantity;
    }

    // Additional methods (if needed)

    @Override
    public String toString() {
        return "Line [line_id=" + line_id + ", line_quantity=" + line_quantity +
                ", basket_id=" + basket_id + ", product_id=" + product_id + name+"]";
    }




}
