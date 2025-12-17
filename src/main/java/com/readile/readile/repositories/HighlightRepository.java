package com.readile.readile.repositories;

import com.readile.readile.models.book.Highlight;
import com.readile.readile.models.book.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long> {
    List<Highlight> findByUserBook(UserBook userBook);
}