package com.readile.readile.repositories;

import com.readile.readile.models.book.list.BookList;
import com.readile.readile.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookListRepository extends JpaRepository<BookList, Long> {
    List<BookList> findByUser(User user);

    BookList findByUserAndName(User user, String name);
}
