package com.readile.readile.services.implementation.book;

import com.readile.readile.models.book.list.BookList;
import com.readile.readile.models.user.User;
import com.readile.readile.repositories.BookListRepository;
import com.readile.readile.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookListService implements CrudService<BookList> {
    @Autowired
    BookListRepository bookListRepository;

    @Override
    public BookList save(BookList entity) {
        return bookListRepository.save(entity);
    }

    @Override
    public BookList update(BookList entity) {
        return bookListRepository.save(entity);
    }

    @Override
    public void delete(BookList entity) {
        bookListRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        bookListRepository.deleteById(id);
    }

    @Override
    public void deleteInBatch(List<BookList> entities) {
        bookListRepository.deleteAllInBatch(entities);
    }

    @Override
    public BookList findById(Long id) {
        return bookListRepository.findById(id).orElse(null);
    }

    @Override
    public List<BookList> findAll() {
        return bookListRepository.findAll();
    }

    public List<BookList> findByUser(User user) {
        return bookListRepository.findByUser(user);
    }

    public BookList findByUserAndName(User user, String name) {
        return bookListRepository.findByUserAndName(user, name);
    }
}
