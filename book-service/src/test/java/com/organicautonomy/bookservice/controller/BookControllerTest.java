package com.organicautonomy.bookservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.organicautonomy.bookservice.dao.BookRepository;
import com.organicautonomy.bookservice.dto.Book;
import com.organicautonomy.bookservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
class BookControllerTest {
    public final Book TO_SAVE = new Book("Holes", "Louis Sachar", LocalDate.of(1998, 11, 1));
    public final Book HOLES = new Book(1, "Holes", "Louis Sachar", LocalDate.of(1998, 11, 1));
    public final Book THE_PRINCE = new Book(2,"The Prince", "Louis Sachar", LocalDate.of(1999, 12, 1));
    public final Book INVALID = new Book();

    @MockBean
    private BookRepository repository;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetAllBooks() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(HOLES);
        books.add(THE_PRINCE);

        when(repository.findAll()).thenReturn(books);

        String outputJson = mapper.writeValueAsString(books);

        this.mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(outputJson))
                .andDo(print());
    }

    @Test
    void testCreateBook() throws Exception {
        String inputJson = mapper.writeValueAsString(TO_SAVE);
        String outputJson = mapper.writeValueAsString(HOLES);

        when(repository.save(TO_SAVE)).thenReturn(HOLES);

        this.mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(outputJson))
                .andDo(print());
    }

    @Test
    void testCreateBookWithInvalidFormat() throws Exception {
        String inputJson = mapper.writeValueAsString(INVALID);

        this.mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andDo(print());
    }

    @Test
    void testGetBookById() throws Exception {
        String outputJson = mapper.writeValueAsString(THE_PRINCE);

        when(repository.findById(THE_PRINCE.getId())).thenReturn(Optional.of(THE_PRINCE));

        this.mockMvc.perform(get("/books/{bookId}", THE_PRINCE.getId()))
                .andExpect(content().json(outputJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testGetBookByIdWithInvalidId() throws Exception {
        when(repository.findById(3)).thenReturn(null);

        this.mockMvc.perform(get("/books/{bookId}", THE_PRINCE.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("There is no book associated with the id provided.",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

    @Test
    void testUpdateBook() throws Exception {
        String inputJson = mapper.writeValueAsString(HOLES);
        when(repository.findById(HOLES.getId())).thenReturn(Optional.of(HOLES));

        this.mockMvc.perform(put("/books/{bookId}", HOLES.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    void testUpdateBookWithInvalidId() throws Exception {
        String inputJson = mapper.writeValueAsString(HOLES);
        when(repository.findById(HOLES.getId())).thenReturn(Optional.empty());

        this.mockMvc.perform(put("/books/{bookId}", HOLES.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("There is no book associated with the id provided.",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

    @Test
    void testUpdateBookWithInvalidPathId() throws Exception {
        String inputJson = mapper.writeValueAsString(HOLES);
        when(repository.findById(HOLES.getId())).thenReturn(Optional.empty());

        this.mockMvc.perform(put("/books/{bookId}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("The book id in the path must match book object id.",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

    @Test
    void testDeleteBook() throws Exception {
        when(repository.findById(HOLES.getId())).thenReturn(Optional.of(HOLES));
        doNothing().when(repository).delete(HOLES);

        this.mockMvc.perform(delete("/books/{bookId}", HOLES.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    void testDeleteBookWithInvalidId() throws Exception {
        when(repository.findById(HOLES.getId())).thenReturn(Optional.empty());

        this.mockMvc.perform(delete("/books/{bookId}", HOLES.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("There is no book associated with the id provided.",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

    @Test
    void testGetBookByTitle() throws Exception {
        String outputJson = mapper.writeValueAsString(THE_PRINCE);

        when(repository.findBookByTitle(THE_PRINCE.getTitle())).thenReturn(THE_PRINCE);

        this.mockMvc.perform(get("/books/title/{title}", THE_PRINCE.getTitle()))
                .andExpect(content().json(outputJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testGetBookByTitleWithInvalidTitle() throws Exception {
        when(repository.findBookByTitle("INVALID")).thenReturn(null);

        this.mockMvc.perform(get("/books/title/{title}", "INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("There is no book associated with the title provided.",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

    @Test
    void testGetBooksByReleaseDate() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(HOLES);

        when(repository.findBooksByReleaseDate(HOLES.getReleaseDate())).thenReturn(books);

        String outputJson = mapper.writeValueAsString(books);

        this.mockMvc.perform(get("/books/date/{releaseDate}", "1998-11-01"))
                .andExpect(status().isOk())
                .andExpect(content().json(outputJson))
                .andDo(print());
    }

    @Test
    void testGetBookByTitleWithInvalidReleaseDate() throws Exception {
        List<Book> books = new ArrayList<>();
        when(repository.findBooksByReleaseDate(LocalDate.of(2012, 1, 1))).thenReturn(books);

        this.mockMvc.perform(get("/books/date/{releaseDate}", "2012-01-01"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("There are no books associated with the release date provided.",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

    @Test
    void testGetBooksByAuthor() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(THE_PRINCE);

        when(repository.findBooksByAuthor(THE_PRINCE.getAuthor())).thenReturn(books);

        String outputJson = mapper.writeValueAsString(books);

        this.mockMvc.perform(get("/books/authors/{author}", THE_PRINCE.getAuthor()))
                .andExpect(status().isOk())
                .andExpect(content().json(outputJson))
                .andDo(print());
    }

    @Test
    void testGetBookByTitleWithInvalidAuthor() throws Exception {
        List<Book> books = new ArrayList<>();
        when(repository.findBooksByAuthor("INVALID")).thenReturn(books);

        this.mockMvc.perform(get("/books/authors/{author}", "INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("There are no books associated with the author provided.",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }
}