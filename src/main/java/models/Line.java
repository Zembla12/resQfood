package models;

import java.sql.Date;

public class Line {
    // Attributes
    private int line_id;
    private int line_quantity;
    private int basket_id;
    private int product_id;
    private int user_id;
    private java.sql.Date line_date;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Constructors
    public Line(int line_id, int line_quantity, int basket_id, int product_id, int user_id, Date line_date) {
        this.line_id = line_id;
        this.line_quantity = line_quantity;
        this.basket_id = basket_id;
        this.product_id = product_id;
        this.user_id = user_id;
        this.line_date = line_date;
    }

    public Line() {}

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

    public int getUserId() {
        return user_id;
    }

    public Date getLineDate() {
        return line_date;
    }

    public void setLineId(int line_id) {
        this.line_id = line_id;
    }

    // Setter methods (if needed)
    public void setLineQuantity(int line_quantity) {
        this.line_quantity = line_quantity;
    }

    // Additional methods (if needed)

    @Override
    public String toString() {
        return "Line [line_id=" + line_id + ", line_quantity=" + line_quantity +
                ", basket_id=" + basket_id + ", product_id=" + product_id +
                ", user_id=" + user_id + ", line_date=" + line_date + ", name=" + name + "]";
    }
}
