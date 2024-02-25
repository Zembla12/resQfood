package test;

import models.Line;
import models.Product;
import services.LineService;
import services.ProductService;
import utils.MyDataBase;

import java.sql.Date;
import java.time.LocalDate;

public class main {

    public static void main(String[] args) {
        System.out.println(MyDataBase.getInstance());

       /* ProductService productService= new ProductService();
        LocalDate localDate = LocalDate.of(2024, 1, 13);
        Date sqlDate = Date.valueOf(localDate);
        System.out.println("SQL Date: " + sqlDate);

        Product P1 = new Product("thon",5,sqlDate);
        productService.ajouter(P1);
        System.out.println(productService.getOneById(2));
        productService.modifier(new Product(1,"brik",10,sqlDate));
        productService.supprimer(1);
        System.out.println(productService.getAll());*/
        /*
        LineService lineService = new LineService();
        Line L1 = new Line(1,2,3,4);
        lineService.ajouter(L1);*/



    }
}
