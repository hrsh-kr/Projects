package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Library {
    private List<Book> Books;
    List<Member> Members;
    private Set<String> RegMobileNumbers;

    public Library() {
        Books = new ArrayList<>();
        Members = new ArrayList<>();
        RegMobileNumbers = new HashSet<>();
    }

    public void AddBook(Book Book) {
        Books.add(Book);
    }

    public void RemoveBook(int BookID) {
        Books.removeIf(Book -> Book.FetchBookID() == BookID);
    }

    public void RegisterMember(Member Member) {
        Members.add(Member);
    }

    public void RemoveMember(String MobileNumber) {
        Members.removeIf(Member -> Member.FetchMobileNumber().equals(MobileNumber));
    }

    public Member FindMember(String MobileNumber) {
        for (Member Member : Members) {
            if (Member.FetchMobileNumber().equals(MobileNumber)) {
                return Member;
            }
        }
        return null;
    }

    public Book FindBook(int BookID) {
        for (Book Book : Books) {
            if (Book.FetchBookID() == BookID) {
                return Book;
            }
        }
        return null;
    }

    public List<Book> FetchAvailableBooks() {
        List<Book> AvailableBooks = new ArrayList<>();
        for (Book Book : Books) {
            if (Book.FetchSigmaCopies() > 0) {
                AvailableBooks.add(Book);
            }
        }
        return AvailableBooks;
    }

    public boolean IfBorrowedBooks(String MobileNumber) {
        Member Member = FindMember(MobileNumber);
        if (Member != null) {
            List<Book> BorrowedBooks = Member.GetBorrowedBooks();
            return !BorrowedBooks.isEmpty();
        }
        return false;
    }

    public boolean IfMobileNumberRegistered(String MobileNumber) {
        return RegMobileNumbers.contains(MobileNumber);
    }

}
