package com.organicautonomy.reviewapi.util.feign;

import com.organicautonomy.reviewapi.dto.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BookClientTest {
    public final Book TO_SAVE = new Book("Holes", "Louis Sachar", LocalDate.of(1998, 11, 1));
    public final Book HOLES = new Book(1, "Holes", "Louis Sachar", LocalDate.of(1998, 11, 1));
    public final Book UPDATED = new Book(1, "Holes", "Louis Sachar", LocalDate.of(1998, 11, 2));
    public final Book THE_PRINCE = new Book(2,"The Prince", "Louis Sachar", LocalDate.of(1999, 12, 1));

    @MockBean
    private BookClient client;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateBook() {
        when(client.createBook(TO_SAVE)).thenReturn(HOLES);

        Book book = client.createBook(TO_SAVE);
        assertEquals(book, HOLES);
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = new ArrayList<>();
        books.add(HOLES);
        books.add(THE_PRINCE);

        when(client.getAllBooks()).thenReturn(books);

        List<Book> fromClient = client.getAllBooks();
        assertEquals(2, fromClient.size());
    }

    @Test
    void testGetBookById() {
        when(client.getBookById(HOLES.getId())).thenReturn(HOLES);

        Book fromClient = client.getBookById(HOLES.getId());

        assertEquals(HOLES, fromClient);
    }

    @Test
    void testUpdateBook() {
        doNothing().when(client).updateBook(HOLES.getId(), UPDATED);

        Book book = new Book(1, "Holes", "Louis Sachar", LocalDate.of(1998, 11, 1));
        book.setReleaseDate(LocalDate.of(1998, 11, 2));

        client.updateBook(book.getId(), book);

        assertEquals(UPDATED, book);
    }

    @Test
    void testGetBookByTitle() {
        when(client.getBookByTitle(HOLES.getTitle())).thenReturn(HOLES);

        Book book = client.getBookByTitle(HOLES.getTitle());

        assertEquals(HOLES, book);
    }

    @Test
    void testGetBooksByReleaseDate() {
        List<Book> books = new ArrayList<>();
        books.add(THE_PRINCE);

        when(client.getBooksByReleaseDate(THE_PRINCE.getReleaseDate())).thenReturn(books);

        List<Book> fromClient = client.getBooksByReleaseDate(THE_PRINCE.getReleaseDate());

        assertEquals(1, fromClient.size());
    }

    @Test
    void testGetBooksByAuthor() {
        List<Book> books = new ArrayList<>();
        books.add(HOLES);

        when(client.getBooksByAuthor(HOLES.getAuthor())).thenReturn(books);

        List<Book> fromClient = client.getBooksByAuthor(HOLES.getAuthor());

        assertEquals(1, fromClient.size());
    }
}