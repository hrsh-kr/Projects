package org.example;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static class LibManageSys {
        public static void main(String[] args) {

            Scanner scanner = new Scanner(System.in);
            Library Library = new Library();
            System.out.println("Library Portal Initialized...");

            int UserInput = 0;

            while (UserInput != 3) {
                System.out.println("-----------------------------------");
                System.out.println("1. Enter as a librarian");
                System.out.println("2. Enter as a member");
                System.out.println("3. Exit");
                System.out.println("-----------------------------------");

                try {
                    UserInput = scanner.nextInt();

                    if (UserInput < 1 || UserInput > 3) {
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                    } else {
                        scanner.nextLine();

                        switch (UserInput) {
                            case 1 -> {
                                // Librarian Menu
                                while (true) {
                                    System.out.println("-----------------------------------");
                                    System.out.println("1. Register a member");
                                    System.out.println("2. Remove a member");
                                    System.out.println("3. Add a book");
                                    System.out.println("4. Remove a book");
                                    System.out.println("5. View all members along with their books and fines to be paid");
                                    System.out.println("6. View all books");
                                    System.out.println("7. Back");
                                    System.out.println("-----------------------------------");
                                    int LibrarianInput = 0; // Initialize invalid value

                                    try {
                                        LibrarianInput = scanner.nextInt();

                                        if (LibrarianInput < 1 || LibrarianInput > 7) {
                                            System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                                        } else {
                                            scanner.nextLine();

                                            switch (LibrarianInput) {
                                                case 1 -> {
                                                    // Implement member registration logic here
                                                    String MemberName = ""; // Initialize to an empty string

                                                    while (MemberName.isEmpty()) {
                                                        System.out.print("Enter member name: ");
                                                        MemberName = scanner.nextLine();

                                                        if (MemberName.isEmpty()) {
                                                            System.out.println("Name cannot be empty. Please enter a valid name.");
                                                        }
                                                    }

                                                    int Age = 0;
                                                    while (true) {
                                                        System.out.print("Enter member age: ");
                                                        String AgeInput = scanner.nextLine();
                                                        try {
                                                            Age = Integer.parseInt(AgeInput);

                                                            if (Age > 0) {
                                                                break;
                                                            } else {
                                                                System.out.println("Age must be a positive integer.");
                                                            }
                                                        } catch (NumberFormatException e) {
                                                            System.out.println("Invalid input. Please enter a valid positive integer age.");
                                                        }
                                                    }
                                                    while (true) {
                                                        System.out.print("Enter member phone number: ");
                                                        String MemberMobileNumber = scanner.nextLine();
                                                        String MobileNumberPattern = "\\d{10}";
                                                        if (!MemberMobileNumber.matches(MobileNumberPattern)) {
                                                            System.out.println("Invalid phone number format. Please enter a 10-digit phone number.");
                                                        } else if (Library.IfMobileNumberRegistered(MemberMobileNumber)) {
                                                            System.out.println("A member with this phone number already exists.");
                                                        } else {
                                                            // Check if a member with the same phone number is already registered
                                                            boolean MemberExists = Library.Members.stream().anyMatch(Member -> Member.FetchMobileNumber().equals(MemberMobileNumber));

                                                            if (MemberExists) {
                                                                System.out.println("A member with this phone number already exists.");
                                                            } else {
                                                                Library.RegisterMember(new Member(MemberMobileNumber, MemberName));
                                                                System.out.println("Member " + MemberName + " with phone number " + MemberMobileNumber + " added successfully!");
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }

                                                case 2 -> {

                                                    // Implement member removal logic here
                                                    while (true) {
                                                        System.out.print("Enter member phone number to remove: ");
                                                        String MemberMobileNumberToRemove = scanner.nextLine();
                                                        String MobileNumberPattern = "\\d{10}";

                                                        if (!MemberMobileNumberToRemove.matches(MobileNumberPattern)) {
                                                            System.out.println("Invalid phone number format. Please enter a 10-digit phone number.");
                                                        } else {
                                                            // Check if a member with the provided phone number exists
                                                            Member MemberToRemove = Library.FindMember(MemberMobileNumberToRemove);

                                                            if (MemberToRemove == null) {
                                                                System.out.println("Member with this phone number does not exist.");
                                                            } else if (Library.IfBorrowedBooks(MemberMobileNumberToRemove)) {
                                                                System.out.println("Cannot remove the member. They have borrowed books.");
                                                                break;
                                                            } else {
                                                                Library.RemoveMember(MemberMobileNumberToRemove);
                                                                System.out.println("Member ID: " + MemberMobileNumberToRemove + " removed successfully!");
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                case 3 -> {
                                                    // Implement book addition logic here
                                                    System.out.print("Enter title: ");
                                                    String Title = scanner.nextLine();
                                                    System.out.print("Enter author: ");
                                                    String Author = scanner.nextLine();
                                                    int SigmaCopies = 0; // Initialize totalCopies to 0

                                                    while (true) {
                                                        System.out.print("Enter total copies: ");
                                                        String SigmaCopiesInput = scanner.nextLine();

                                                        try {
                                                            SigmaCopies = Integer.parseInt(SigmaCopiesInput);

                                                            if (SigmaCopies > 0) {
                                                                break;
                                                            } else {
                                                                System.out.println("Total copies must be a positive integer greater than zero.");
                                                            }
                                                        } catch (NumberFormatException e) {
                                                            System.out.println("Invalid input. Please enter a valid positive integer for total copies.");
                                                        }
                                                    }

                                                    for (int i = 0; i < SigmaCopies; i++) {
                                                        // Create a new book and set the issue date to null initially
                                                        Book NewBook = new Book(Title, Author, 1);
                                                        NewBook.SetIssueDate();

                                                        Library.AddBook(NewBook);
                                                    }
                                                    System.out.println(SigmaCopies + "x " + "Copies of " + Title + " by " + Author + " added successfully!");
                                                }
                                                case 4 -> {
                                                    // Implement book removal logic here
                                                    System.out.print("Enter book ID to remove: ");
                                                    int BookIDToRemove = scanner.nextInt();
                                                    Book BookToRemove = Library.FindBook(BookIDToRemove);

                                                    if (BookToRemove != null) {
                                                        // Check if the book is currently borrowed by any member
                                                        boolean IfBorrowed = Library.Members.stream().anyMatch(Member -> Member.GetBorrowedBooks().contains(BookToRemove));

                                                        if (IfBorrowed) {
                                                            System.out.println("Cannot remove the book. It is currently borrowed by a member.");
                                                        } else {
                                                            Library.RemoveBook(BookIDToRemove);
                                                            System.out.println("Book ID " + BookIDToRemove + " removed successfully!");
                                                        }
                                                    } else {
                                                        System.out.println("Book with ID " + BookIDToRemove + " not found. No book removed.");
                                                    }
                                                }
                                                case 5 -> {
                                                    // Implement the view all members with books and fines logic here
                                                    System.out.println("All Members with Books and Fines:");
                                                    if (Library.Members.isEmpty()) {
                                                        System.out.println("No members added.");
                                                    } else {
                                                        for (Member Member : Library.Members) {
                                                            System.out.println("Name: " + Member.FetchName() + ", Phone: " + Member.FetchMobileNumber());

                                                            List<Book> BorrowedBooks = Member.GetBorrowedBooks();
                                                            if (BorrowedBooks.isEmpty()) {
                                                                System.out.println("  Borrowed Books: None");
                                                            } else {
                                                                System.out.println("  Borrowed Books:");
                                                                for (Book Book : BorrowedBooks) {
                                                                    System.out.println("    Book ID: " + Book.FetchBookID() + ", Title: " + Book.FetchTitle() + ", Author: " + Book.FetchAuthor());
                                                                }
                                                            }

                                                            double Fines = Member.FetchFineAmount();
                                                            System.out.println("  Fines to be Paid: Rs" + Fines);
                                                        }
                                                    }
                                                }
                                                case 6 -> {
                                                    // Implement the view books logic here
                                                    System.out.println("Available Books:");
                                                    List<Book> AllBooks = Library.FetchAvailableBooks();
                                                    if (AllBooks.isEmpty()) {
                                                        System.out.println("No books added.");
                                                    } else {
                                                        for (Book Book : AllBooks) {
                                                            System.out.println(Book.FetchBookID() + ": " + Book.FetchTitle() + " by " + Book.FetchAuthor());
                                                        }
                                                    }
                                                }
                                                case 7 -> {
                                                    // Back to the main menu
                                                }
                                                default ->
                                                        System.out.println("Invalid choice. Please enter a valid option.");
                                            }

                                            if (LibrarianInput == 7) {
                                                break; // Exit the librarian menu and go back to the main menu
                                            }
                                        }
                                    } catch (java.util.InputMismatchException e) {
                                        System.out.println("Invalid input. Please enter a valid integer.");
                                        scanner.nextLine();
                                    }
                                }
                            } // Exit the librarian login loop

                            case 2 -> {
                                // Member Menu
                                System.out.print("Name: ");
                                String MemberName = scanner.nextLine();
                                System.out.print("Phone No: ");
                                String MemberMobileNumber = scanner.nextLine();

                                // Implement member authentication and menu here
                                // Use memberPhoneNumber to authenticate the member
                                Member LoggedInMember = Library.FindMember(MemberMobileNumber);

                                if (LoggedInMember != null && LoggedInMember.FetchName().equals(MemberName)) {
                                    System.out.println("Welcome, " + LoggedInMember.FetchName() + ". Member ID : " + LoggedInMember.FetchMobileNumber());
                                    // Member's menu options
                                    while (true) {
                                        System.out.println("---------------------------------");
                                        System.out.println("1. List Available Books");
                                        System.out.println("2. List My Books");
                                        System.out.println("3. Issue book");
                                        System.out.println("4. Return book");
                                        System.out.println("5. Pay Fine");
                                        System.out.println("6. Back");
                                        System.out.println("---------------------------------");

                                        int MemberInput = 0; // Initialize with an invalid value

                                        try {
                                            MemberInput = scanner.nextInt();

                                            if (MemberInput < 1 || MemberInput > 6) {
                                                System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                                            } else {
                                                scanner.nextLine();

                                                switch (MemberInput) {
                                                    case 1 -> {
                                                        // Implement the list available books logic here
                                                        System.out.println("Available Books:");
                                                        List<Book> AllBooks = Library.FetchAvailableBooks();
                                                        for (Book Book : AllBooks) {
                                                            System.out.println(Book.FetchBookID() + ": " + Book.FetchTitle() + " by " + Book.FetchAuthor());
                                                        }
                                                    }
                                                    case 2 -> {
                                                        // Implement the list member's books logic here
                                                        List<Book> BorrowedBooks = LoggedInMember.GetBorrowedBooks();
                                                        if (BorrowedBooks.isEmpty()) {
                                                            System.out.println("You haven't borrowed any books yet.");
                                                        } else {
                                                            System.out.println("Your Borrowed Books:");
                                                            for (Book Book : BorrowedBooks) {
                                                                System.out.println("Book ID: " + Book.FetchBookID() + ", Title: " + Book.FetchTitle() + ", Author: " + Book.FetchAuthor());
                                                            }
                                                        }
                                                    }
                                                    case 3 -> {
                                                        // Implement the issue book logic here
                                                        Member IssuingMember = Library.FindMember(MemberMobileNumber);
                                                        if (IssuingMember != null) {
                                                            List<Book> AvailableBooks = Library.FetchAvailableBooks();
                                                            if (AvailableBooks.isEmpty()) {
                                                                System.out.println("No available books.");
                                                            } else {
                                                                System.out.println("Available Books:");
                                                                for (Book Book : AvailableBooks) {
                                                                    System.out.println(Book.FetchBookID() + ": " + Book.FetchTitle() + " by " + Book.FetchAuthor());
                                                                }
                                                                System.out.print("Enter book ID to issue: ");
                                                                int BookIDToIssue = scanner.nextInt();
                                                                Book IssuingBook = Library.FindBook(BookIDToIssue);
                                                                if (IssuingBook != null) {
                                                                    if (IssuingMember.FetchFineAmount() == 0.0) {
                                                                        IssuingBook.SetReturnDate(CalcDueDate()); // Set the due date when issuing the book
                                                                        IssuingMember.BorrowBook(IssuingBook); // Call BorrowBook method
                                                                        IssuingBook.SigmaCopies -= 1;
                                                                    } else {
                                                                        System.out.println("Clear your penalty to issue a book.");
                                                                    }
                                                                } else {
                                                                    System.out.println("Book not found.");
                                                                }
                                                            }
                                                        }
                                                    }
                                                    case 4 -> {
                                                        // Implement the book return logic here
                                                        Member ReturningMember = Library.FindMember(MemberMobileNumber);
                                                        if (ReturningMember != null) {

                                                            // Track borrowed books
                                                            List<Book> BorrowedBooks = ReturningMember.GetBorrowedBooks();

                                                            if (BorrowedBooks.isEmpty()) {
                                                                System.out.println("No borrowed books.");
                                                            } else {
                                                                System.out.println("Borrowed Books:");
                                                                for (Book Book : BorrowedBooks) {
                                                                    System.out.println(Book.FetchBookID() + ": " + Book.FetchTitle() + " by " + Book.FetchAuthor());
                                                                }
                                                                System.out.print("Enter book ID to return: ");
                                                                int BookIDToReturn = scanner.nextInt();
                                                                Book ReturningBook = Library.FindBook(BookIDToReturn);
                                                                if (ReturningBook != null) {
                                                                    // Update borrowed books
                                                                    ReturningMember.ReturnBook(ReturningBook);
                                                                    ReturningBook.SigmaCopies += 1;
                                                                } else {
                                                                    System.out.println("Book not found.");
                                                                }
                                                            }
                                                        }
                                                    }
                                                    case 5 -> {
                                                        // Implement the fine payment logic here
                                                        double Fines = LoggedInMember.FetchFineAmount();
                                                        System.out.println(Fines);

                                                        if (Fines > 0.0) {
                                                            LoggedInMember.SetFineAmount(0.0);
                                                            System.out.println("You had a total fine of Rs." + Fines + ". It has been paid successfully!");
                                                            System.out.println("Remaining fines: Rs" + LoggedInMember.FetchFineAmount());
                                                        } else {
                                                            System.out.println("You don't have any fines to pay.");
                                                        }
                                                    }
                                                    case 6 -> {
                                                        // Back to the main menu
                                                    }
                                                    default ->
                                                            System.out.println("Invalid choice. Please enter a valid option.");
                                                }

                                                if (MemberInput == 6) {
                                                    break; // Exit the member menu and go back to the main menu
                                                }
                                            }
                                        } catch (java.util.InputMismatchException e) {
                                            System.out.println("Invalid input. Please enter a number between 1 and 6.");
                                            scanner.nextLine();
                                        }
                                    }
                                } else {
                                    System.out.println("Member with the provided information doesn't exist.");
                                }


                            } // Exit the member login loop

                            case 3 -> {
                                System.out.println("Exiting Library Management System.");
                                System.out.println("Thanks for visiting!");
                                System.exit(0); // Exit the program
                            }
                        }
                    }
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Invalid input. Please enter an integer.");
                    scanner.nextLine();
                }
            }
        }

        private static Date CalcDueDate() {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 10); // Due date is 10 days from the current date
            return cal.getTime();
        }
    }
}