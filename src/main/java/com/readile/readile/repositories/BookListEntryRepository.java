package com.readile.readile.repositories;

import com.readile.readile.models.book.UserBook;
import com.readile.readile.models.book.list.BookList;
import com.readile.readile.models.book.list.BookListEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookListEntryRepository extends JpaRepository<BookListEntry, Long> {
    List<BookListEntry> findByBookList(BookList bookList);

    List<BookListEntry> findByUserBook(UserBook userBook);

    BookListEntry findByBookListAndUserBook(BookList bookList, UserBook userBook);
}
