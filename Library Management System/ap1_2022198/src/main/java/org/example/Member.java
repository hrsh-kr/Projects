package org.example;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Member {
    private String MobileNumber;
    private String Name;
    private LocalTime currentTimeMillis;

    private double FineAmount;
    private List<Book> BorrowedBooks; // Add a list to track borrowed books

    public Member(String MobileNumber, String Name) {
        this.MobileNumber = MobileNumber;
        this.Name = Name;
        this.FineAmount = 0.0;
        this.BorrowedBooks = new ArrayList<>();
    }

    public String FetchMobileNumber() {
        return MobileNumber;
    }

    public String FetchName() {
        return Name;
    }


    public double FetchFineAmount() {
        return FineAmount;
    }

    public void SetFineAmount(double FineAmount) {
        this.FineAmount = FineAmount;
    }

    public List<Book> GetBorrowedBooks() {
        return BorrowedBooks;
    }

    public void BorrowBook(Book Book) {
        currentTimeMillis = LocalTime.now();
        if (BorrowedBooks.size() < 2) {
            if (Book.isAvailable()) { // Check if the book is available
                BorrowedBooks.add(Book);
                Book.SetAvailable(false); // Mark the book as not available
                System.out.println("Book " + Book.FetchTitle() + " by " + Book.FetchAuthor() + " borrowed successfully.");
            } else {
                System.out.println("The book is not available for borrowing.");
            }
        } else {
            System.out.println("You have already borrowed the maximum number of books (2).");
        }
    }

    public void ReturnBook(Book Book) {

        if (BorrowedBooks.contains(Book)) {
            BorrowedBooks.remove(Book);
            Book.SetAvailable(true); // Mark the book as available
            System.out.println("Book " + Book.FetchTitle() + " by " + Book.FetchAuthor() + " returned successfully.");

            // Calculate fine if the book is returned late
            LocalTime ReturnDateMillis = LocalTime.now();
            Duration Period = Duration.between(currentTimeMillis, ReturnDateMillis);
            long Delta = Period.getSeconds();

            if (Delta > 10) {
                double FineAmount = (double) (Delta - 10) * 3.0;
                this.FineAmount += FineAmount;
                int days = (int) (FineAmount / 3);
                System.out.println("Due Fine Amount: Rs" + FineAmount + " for a delay of " + days + " days" + ". Pay your dues to issue a book.");
            }
        } else {
            System.out.println("Invalid book or book is already available.");
        }
    }

}
