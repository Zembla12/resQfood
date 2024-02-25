package models;


public class Basket {
    // Attributes
    private int basket_id;
    private String basket_status;
    private int user_id;
    private int event_id;

    // Constructors
    public Basket(int basket_id, String basket_status, int user_id, int event_id) {
        this.basket_id = basket_id;
        this.basket_status = basket_status;
        this.user_id = user_id;
        this.event_id = event_id;
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

    public int getEventId() {
        return event_id;
    }

    // Setter methods (if needed)
    public void setBasketStatus(String basket_status) {
        this.basket_status = basket_status;
    }

    // Additional methods (if needed)

    @Override
    public String toString() {
        return "Basket [basket_id=" + basket_id + ", basket_status=" + basket_status +
                ", user_id=" + user_id + ", event_id=" + event_id + "]";
    }
}
