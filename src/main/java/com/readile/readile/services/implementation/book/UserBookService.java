package com.readile.readile.services.implementation.book;

import com.readile.readile.models.book.CatalogBook;
import com.readile.readile.models.book.UserBook;
import com.readile.readile.models.user.User;
import com.readile.readile.repositories.UserBookRepository;
import com.readile.readile.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBookService implements CrudService<UserBook> {
    @Autowired
    UserBookRepository userBookRepository;

    @Override
    public UserBook save(UserBook entity) {
        return userBookRepository.save(entity);
    }

    @Override
    public UserBook update(UserBook entity) {
        return userBookRepository.save(entity);
    }

    @Override
    public void delete(UserBook entity) {
        userBookRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        userBookRepository.deleteById(id);
    }

    @Override
    public void deleteInBatch(List<UserBook> entities) {
        userBookRepository.deleteAllInBatch(entities);
    }

    @Override
    public UserBook findById(Long id) {
        return userBookRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserBook> findAll() {
        return userBookRepository.findAll();
    }

    public List<UserBook> findAllByUser(User user) {
        return userBookRepository.findAllByUser(user);
    }

    public UserBook findByUserAndBook(User user, CatalogBook book) {
        return userBookRepository.findByUserAndBook(user, book);
    }

    public UserBook addCatalogBookToUser(User user, CatalogBook book) {
        UserBook existing = userBookRepository.findByUserAndBook(user, book);
        if (existing != null) {
            return existing;
        }
        UserBook userBook = new UserBook(user, book, 0, null, null,
                com.readile.readile.models.book.Rating.ONE_STAR,
                com.readile.readile.models.book.Status.TO_READ);
        return userBookRepository.save(userBook);
    }
}