package services;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public interface IService<T> {
    public void ajouter(T t);
    public void modifier(T t);
    public void supprimer(int id);
    public T getOneById(int id);
    public List<T> getAll();
}