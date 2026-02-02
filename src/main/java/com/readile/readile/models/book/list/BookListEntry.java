package com.readile.readile.models.book.list;

import com.readile.readile.models.book.UserBook;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(
        name = "Book_List_Entry",
        uniqueConstraints = @UniqueConstraint(columnNames = {"book_list_id", "user_book_id"})
)
public class BookListEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "book_list_id", nullable = false)
    private BookList bookList;

    @ManyToOne
    @JoinColumn(name = "user_book_id", nullable = false)
    private UserBook userBook;

    public BookListEntry(BookList bookList, UserBook userBook) {
        this.bookList = bookList;
        this.userBook = userBook;
    }
}
