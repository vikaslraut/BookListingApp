package com.example.r.booklistingapp;

/**
 * Book data object model
 */
public class Book {

    private String bookTitle;
    private String bookAuthor;

    public Book(String bookTitle, String bookAuthor) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }
}
