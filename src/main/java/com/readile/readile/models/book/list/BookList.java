package com.readile.readile.models.book.list;

import com.readile.readile.models.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(
        name = "Book_List",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"})
)
public class BookList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(length = 64, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "bookList", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<BookListEntry> entries = new HashSet<>();

    public BookList(User user, String name) {
        this.user = user;
        this.name = name;
    }
}
