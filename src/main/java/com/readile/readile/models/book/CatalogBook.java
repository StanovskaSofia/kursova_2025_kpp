package com.readile.readile.models.book;

import com.readile.readile.models.book.category.BookCategory;
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
@Table(name = "Book_Catalog")
public class CatalogBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "cover_id", nullable = false, length = 1024)
    private String coverId;

    @Column(nullable = false)
    private int length;

    @Column(length = 1024, nullable = false)
    private String authors;

    @Column(length = 1024, nullable = false)
    private String description;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @ToString.Exclude
    Set<BookCategory> bookCategories = new HashSet<>();

    public CatalogBook(String name, String coverId, int length, String authors, String description) {
        this.name = name;
        this.coverId = coverId;
        this.length = length;
        this.authors = authors;
        this.description = description;
    }
}