# Readme File

## Steps to Run the Project

1.Open terminal in the forlder containing "pom.xml" file
2.Run command "mvn clean package"
3.Run command "cd target"
4.Run command "java -jar untitled11-1.0-SNAPSHOT.jar"
5.Follow the on-screen instructions to use the Library Management System

xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

## Brief Explanation of the Code

The code in the Java class called `Main` represents a simple library management system in Java.

1. **Initialization**: The program starts by initializing some basic settings and displaying a welcome message for the library portal.

2. **User Selection**: The program presents a menu to the user with three options:
   - **Enter as a librarian**: Librarians have special privileges to manage the library.
   - **Enter as a member**: Registered members can borrow and return books.
   - **Exit**: To close the program.

3. **Librarian Actions**: If the user selects the librarian option, they can perform various library management tasks:
   - Register a new member: Entering the name, age, and phone number of a new library member.
   - Remove a member: Removing a library member based on their phone number.
   - Add a book: Adding books to the library, specifying title, author, and the number of copies.
   - Remove a book: Removing a book from the library based on its ID.
   - View all members with their books and fines: Displaying a list of members, their borrowed books, and any fines they need to pay.
   - View all books: Displaying a list of all available books in the library.

4. **Member Actions**: If the user selects the member option, they can perform actions related to their library account:
   - List available books: Displaying a list of books available for borrowing.
   - List my books: Showing the books currently borrowed by the member.
   - Issue a book: Borrowing a book from the library after specifying its ID.
   - Return a book: Returning a previously borrowed book to the library.
   - Pay fine: Clearing any fines the member may have accrued.

5. **Due Dates**: When a member borrows a book, the system sets a due date for returning the book. By default, it's set to 10 days from the current date.

6. **Input Validation**: Throughout the program, there are checks to ensure that user inputs are valid. For example, checking if the user enters a valid phone number, positive numbers, or valid menu choices.

7. **Exiting the Program**: Users can choose to exit the program at any time.

----------------------------------------------------------------------------------------------------------------------------------------------------------

This code in the Java class called `Book` represents books in a library.

1. **Book Properties**: The `Book` class has several properties that describe a book:
   - `BookID`: A unique identification number for each book, automatically assigned as the next available ID.
   - `Title`: The title of the book.
   - `Author`: The author of the book.
   - `SigmaCopies`: The total number of copies of the book available in the library.
   - `ReturnDate`: The due date for returning the book (if borrowed), represented as a date.
   - `isAvailable`: A flag indicating whether the book is currently available in the library.

2. **Constructor**: When a new `Book` object is created, it requires three pieces of information: the title, author, and the total number of copies. It assigns a unique ID to the book, initializes its properties, and marks it as available.

3. **Getter Methods**: The class provides methods to retrieve the book's properties:
   - `FetchBookID()`: Retrieves the book's unique ID.
   - `FetchTitle()`: Retrieves the book's title.
   - `FetchAuthor()`: Retrieves the book's author.
   - `FetchSigmaCopies()`: Retrieves the total number of copies available.

4. **Issue and Return Dates**: Although there are methods like `SetIssueDate()` and `SetReturnDate()`, they don't have any functionality implemented in this code snippet. However, they suggest that in a complete system, you might set and update these dates when a book is borrowed or returned.

5. **Availability**: The `isAvailable()` method checks whether the book is currently available in the library or not. It returns `true` if the book is available, and `false` otherwise.

6. **Set Availability**: The `SetAvailable(boolean available)` method allows you to mark a book as available or unavailable by setting the `isAvailable` property.

----------------------------------------------------------------------------------------------------------------------------------------------------------

The code in the Java class called `Library` simulates the functioning of a library.

1. **Library Properties**: The `Library` class has several properties:
   - `Books`: A list that stores information about all the books in the library.
   - `Members`: A list that keeps track of all the library members.
   - `RegMobileNumbers`: A set that stores registered mobile phone numbers of library members.

2. **Constructor**: When a new `Library` object is created, it initializes the lists for books and members, as well as the set for registered mobile numbers.

3. **Adding and Removing Books**: The library allows you to add books using the `AddBook(Book Book)` method and remove books using the `RemoveBook(int BookID)` method. The book's unique ID is used to identify and remove a specific book.

4. **Registering and Removing Members**: The library can register new members using the `RegisterMember(Member Member)` method and remove members using the `RemoveMember(String MobileNumber)` method. Membership is identified by the mobile phone number of the member.

5. **Finding Members and Books**: You can find a member by providing their mobile phone number using the `FindMember(String MobileNumber)` method. Similarly, you can find a book by providing its unique ID using the `FindBook(int BookID)` method.

6. **Fetching Available Books**: The `FetchAvailableBooks()` method retrieves a list of all books that are currently available for borrowing. It checks the `SigmaCopies` property of each book to determine availability.

7. **Checking for Borrowed Books**: The `IfBorrowedBooks(String MobileNumber)` method checks if a member with a given mobile number has borrowed any books. It returns `true` if the member has borrowed books and `false` otherwise.

8. **Checking Mobile Number Registration**: The `IfMobileNumberRegistered(String MobileNumber)` method checks if a mobile number is already registered with a library member. It returns `true` if the mobile number is registered and `false` otherwise.

----------------------------------------------------------------------------------------------------------------------------------------------------------

This code in the Java class called `Member` represents a library member.

1. **Member Properties**: The `Member` class has several properties:
   - `MobileNumber`: The mobile phone number of the library member, serving as their unique identifier.
   - `Name`: The name of the library member.
   - `FineAmount`: The amount of fine (late fee) the member may owe for returning books late.
   - `BorrowedBooks`: A list that keeps track of the books borrowed by the member.

2. **Constructor**: When a new `Member` object is created, it requires two pieces of information: the mobile phone number and the name of the member. It initializes the member's properties, setting the fine amount to zero and creating an empty list for borrowed books.

3. **Getter Methods**: The class provides methods to retrieve the member's properties:
   - `FetchMobileNumber()`: Retrieves the member's mobile phone number.
   - `FetchName()`: Retrieves the member's name.
   - `FetchFineAmount()`: Retrieves the amount of fine owed by the member.

4. **Setting Fine Amount**: The `SetFineAmount(double FineAmount)` method allows you to set the fine amount for the member.

5. **Tracking Borrowed Books**: The `GetBorrowedBooks()` method returns a list of books that the member has borrowed. This list keeps track of the books currently in possession by the member.

6. **Borrowing Books**: The `BorrowBook(Book Book)` method allows the member to borrow a book. It checks if the member has already borrowed the maximum allowed number of books (2) and if the book is available. If both conditions are met, it adds the book to the member's list of borrowed books and marks it as unavailable.

7. **Returning Books**: The `ReturnBook(Book Book)` method allows the member to return a book. It checks if the book is in the member's list of borrowed books and if it's currently available. If the book is eligible for return, it removes it from the list of borrowed books, marks it as available, and calculates any fines if the book is returned late. The fine is calculated based on the time elapsed since borrowing, and if it exceeds 10 seconds, a fine is imposed.

xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

## Error Handling Edge Cases

1. **Input Validation:**
   - Ensure that user inputs are validated. For example, check if the entered phone number is in the correct format or if the total copies of a book are non-negative.

2. **Member Registration:**
   - Check if a member with the same phone number already exists before registering a new member. If a member with the same phone number is found, handle the error by displaying a message to the user.

3. **Member Removal:**
   - Before removing a member, check if they have borrowed any books. If they have borrowed books, prevent the removal and notify the librarian of the issue.

4. **Book Removal:**
   - Check if the book with the specified ID exists before attempting to remove it. If the book does not exist, provide a suitable error message.

5. **Book Issuance & Return:**
   - Verify that the member does not have any outstanding fines before allowing them to borrow a book. If they have fines, prevent the book issuance and prompt them to clear their fines first.
   - Ensure that the book being returned exists in the library's collection and that the member has borrowed it. Handle cases where the book is not found or is already available.

6. **User Input Handling:**
    - Validate user input for menu choices to prevent unexpected values. Handle input errors gracefully by displaying appropriate error messages and allowing the user to retry.

---------------------------------------------------------------------End of Document----------------------------------------------------------------------