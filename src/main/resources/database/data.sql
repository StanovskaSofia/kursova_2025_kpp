USE Readile;

INSERT INTO Category (name, category_image, user_id) VALUES
                                                         ('Fiction', '/assets/placeholders/categories/cat1.png', NULL),
                                                         ('Non-Fiction', '/assets/placeholders/categories/cat2.png', NULL),
                                                         ('Science', '/assets/placeholders/categories/cat3.png', NULL),
                                                         ('History', '/assets/placeholders/categories/cat4.png', NULL),
                                                         ('Fantasy', '/assets/placeholders/categories/cat5.png', NULL);

INSERT INTO Book_Catalog (name, cover_id, length, authors, description) VALUES
                                                                            ('The First Dawn', '/assets/placeholders/books/book1.png', 320, 'A. Harper', 'A tale of hope and discovery as a small town greets a mysterious sunrise.'),
                                                                            ('Echoes of Tomorrow', '/assets/placeholders/books/book2.png', 280, 'L. Nguyen', 'Time travelers race to prevent a paradox that could unravel reality.'),
                                                                            ('Silent Springs', '/assets/placeholders/books/book3.png', 350, 'M. Patel', 'A botanist uncovers a hidden world of whispering forests and ancient secrets.'),
                                                                            ('Starlit Voyage', '/assets/placeholders/books/book4.png', 410, 'R. Kim', 'An interstellar crew charts unknown galaxies in search of a new home.'),
                                                                            ('Clockwork Hearts', '/assets/placeholders/books/book5.png', 290, 'E. Rossi', 'In a steampunk city, two inventors risk everything for love and invention.'),
                                                                            ('Shadows in the Library', '/assets/placeholders/books/book6.png', 260, 'J. Morales', 'A librarian follows cryptic clues to stop a string of literary crimes.'),
                                                                            ('Beneath the Waves', '/assets/placeholders/books/book7.png', 330, 'S. Ibrahim', 'A marine biologist dives into myths of an underwater civilization.'),
                                                                            ('Mountain of Glass', '/assets/placeholders/books/book8.png', 300, 'T. O''Brien', 'Explorers ascend a crystal peak that reflects their deepest fears.'),
                                                                            ('City of Threads', '/assets/placeholders/books/book9.png', 370, 'C. Zhao', 'A weaver navigates political intrigue in a metropolis built on silk and secrets.'),
                                                                            ('Garden of Sparks', '/assets/placeholders/books/book10.png', 240, 'D. Sinclair', 'Young inventors transform their town with brilliant ideas and bright lights.');

INSERT INTO Book_Category (category_id, book_id) VALUES
                                                     (1, 1),
                                                     (1, 5),
                                                     (2, 6),
                                                     (3, 7),
                                                     (3, 4),
                                                     (4, 8),
                                                     (5, 10),
                                                     (5, 4),
                                                     (2, 2),
                                                     (1, 3);