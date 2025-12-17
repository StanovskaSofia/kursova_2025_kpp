package com.readile.readile.repositories;

import com.readile.readile.models.book.CatalogBook;
import com.readile.readile.models.book.UserBook;
import com.readile.readile.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    List<UserBook> findAllByUser(User user);

    UserBook findByUserAndBook(User user, CatalogBook book);
}