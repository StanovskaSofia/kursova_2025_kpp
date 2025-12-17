package com.readile.readile.services.implementation.book;

import com.readile.readile.models.book.CatalogBook;
import com.readile.readile.repositories.CatalogBookRepository;
import com.readile.readile.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogBookService implements CrudService<CatalogBook> {
    @Autowired
    CatalogBookRepository catalogBookRepository;

    @Override
    public CatalogBook save(CatalogBook entity) {
        return catalogBookRepository.save(entity);
    }

    @Override
    public CatalogBook update(CatalogBook entity) {
        return catalogBookRepository.save(entity);
    }

    @Override
    public void delete(CatalogBook entity) {
        catalogBookRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        catalogBookRepository.deleteById(id);
    }

    @Override
    public void deleteInBatch(List<CatalogBook> entities) {
        catalogBookRepository.deleteAllInBatch(entities);
    }

    @Override
    public CatalogBook findById(Long id) {
        return catalogBookRepository.findById(id).orElse(null);
    }

    @Override
    public List<CatalogBook> findAll() {
        return catalogBookRepository.findAll();
    }

    public List<CatalogBook> search(String query) {
        return catalogBookRepository.findByNameIgnoreCaseContainingOrAuthorsIgnoreCaseContaining(query, query);
    }
}