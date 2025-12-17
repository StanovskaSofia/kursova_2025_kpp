package com.readile.readile.models.book;

import com.readile.readile.models.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "User_Book")
public class UserBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private CatalogBook book;

    @Column(nullable = false)
    private int currentPage;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "userBook", cascade = CascadeType.ALL)
    @ToString.Exclude
    Set<Highlight> highlights = new HashSet<>();

    public UserBook(User user, CatalogBook book, int currentPage, Date startDate, Date endDate, Rating rating, Status status) {
        this.user = user;
        this.book = book;
        this.currentPage = currentPage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rating = rating;
        this.status = status;
    }
}
