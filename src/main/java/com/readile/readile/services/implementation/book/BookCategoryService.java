package com.readile.readile.services.implementation.book;

import com.readile.readile.models.book.CatalogBook;
import com.readile.readile.models.book.category.BookCategory;
import com.readile.readile.models.book.category.Category;
import com.readile.readile.repositories.BookCategoryRepository;
import com.readile.readile.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCategoryService implements CrudService<BookCategory> {
    @Autowired
    BookCategoryRepository bookCategoryRepository;

    @Override
    public BookCategory save(BookCategory entity) {
        return bookCategoryRepository.save(entity);
    }

    @Override
    public BookCategory update(BookCategory entity) {
        return bookCategoryRepository.save(entity);
    }

    @Override
    public void delete(BookCategory entity) {
        bookCategoryRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Use delete with BookCategory instance");
    }

    @Override
    public void deleteInBatch(List<BookCategory> entities) {
        bookCategoryRepository.deleteAllInBatch(entities);
    }

    @Override
    public BookCategory findById(Long id) {
        return null;
    }

    @Override
    public List<BookCategory> findAll() {
        return bookCategoryRepository.findAll();
    }

    public List<BookCategory> findAllByCategory(Category category) {
        return bookCategoryRepository.findAllByCategory(category);
    }

    public List<BookCategory> findAllByBook(CatalogBook book) {
        return bookCategoryRepository.findAllByBook(book);
    }
}