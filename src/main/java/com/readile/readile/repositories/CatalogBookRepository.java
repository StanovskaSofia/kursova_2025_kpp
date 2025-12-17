package com.readile.readile.repositories;

import com.readile.readile.models.book.CatalogBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogBookRepository extends JpaRepository<CatalogBook, Long> {
    List<CatalogBook> findByNameIgnoreCaseContainingOrAuthorsIgnoreCaseContaining(String name, String authors);
}