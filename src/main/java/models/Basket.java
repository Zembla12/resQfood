package models;

import java.sql.Date;

public class Basket {
    // Attributes
    private final int basket_id;
    private String basket_status;
    private final int user_id;
    private java.sql.Date confirmation_date; // Replaced event_id with confirmation_date

    // Constructors
    public Basket(int basket_id, String basket_status, int user_id, Date confirmation_date) {
        this.basket_id = basket_id;
        this.basket_status = basket_status;
        this.user_id = user_id;
        this.confirmation_date = confirmation_date;
    }

    // Getter methods
    public int getBasketId() {
        return basket_id;
    }

    public String getBasketStatus() {
        return basket_status;
    }

    public int getUserId() {
        return user_id;
    }

    public Date getConfirmationDate() {
        return confirmation_date;
    }

    // Setter methods (if needed)
    public void setBasketStatus(String basket_status) {
        this.basket_status = basket_status;
    }

    public void setConfirmationDate(Date confirmation_date) {
        this.confirmation_date = confirmation_date;
    }

    // Additional methods (if needed)

    @Override
    public String toString() {
        return "Basket [basket_id=" + basket_id + ", basket_status=" + basket_status +
                ", user_id=" + user_id + ", confirmation_date=" + confirmation_date + "]";
    }
}
