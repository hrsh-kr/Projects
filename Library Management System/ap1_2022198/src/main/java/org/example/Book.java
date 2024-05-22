package org.example;

import java.util.Date;

public class Book {
    private static int NextBookID = 1;
    private int BookID;
    private String Title;
    private String Author;
    int SigmaCopies;
    private Date ReturnDate;
    private boolean isAvailable;

    public Book(String Title, String Author, int SigmaCopies) {
        this.BookID = NextBookID++; // Assign the next available book ID
        this.Title = Title;
        this.Author = Author;
        this.SigmaCopies = SigmaCopies;
        this.ReturnDate = null;
        this.isAvailable = true;
    }

    public int FetchBookID() {
        return BookID;
    }

    public String FetchTitle() {
        return Title;
    }

    public String FetchAuthor() {
        return Author;
    }

    public int FetchSigmaCopies() {
        return SigmaCopies;
    }

    public void SetIssueDate() {
    }

    public void SetReturnDate(Date returnDate) {
        this.ReturnDate = returnDate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void SetAvailable(boolean available) {
        isAvailable = available;
    }
}
