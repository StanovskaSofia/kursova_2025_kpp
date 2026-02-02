package com.readile.readile.services.implementation.book;

import com.readile.readile.models.book.UserBook;
import com.readile.readile.models.book.list.BookList;
import com.readile.readile.models.book.list.BookListEntry;
import com.readile.readile.repositories.BookListEntryRepository;
import com.readile.readile.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookListEntryService implements CrudService<BookListEntry> {
    @Autowired
    BookListEntryRepository bookListEntryRepository;

    @Override
    public BookListEntry save(BookListEntry entity) {
        return bookListEntryRepository.save(entity);
    }

    @Override
    public BookListEntry update(BookListEntry entity) {
        return bookListEntryRepository.save(entity);
    }

    @Override
    public void delete(BookListEntry entity) {
        bookListEntryRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        bookListEntryRepository.deleteById(id);
    }

    @Override
    public void deleteInBatch(List<BookListEntry> entities) {
        bookListEntryRepository.deleteAllInBatch(entities);
    }

    @Override
    public BookListEntry findById(Long id) {
        return bookListEntryRepository.findById(id).orElse(null);
    }

    @Override
    public List<BookListEntry> findAll() {
        return bookListEntryRepository.findAll();
    }

    public List<BookListEntry> findByBookList(BookList bookList) {
        return bookListEntryRepository.findByBookList(bookList);
    }

    public List<BookListEntry> findByUserBook(UserBook userBook) {
        return bookListEntryRepository.findByUserBook(userBook);
    }

    public BookListEntry findByBookListAndUserBook(BookList bookList, UserBook userBook) {
        return bookListEntryRepository.findByBookListAndUserBook(bookList, userBook);
    }
}
