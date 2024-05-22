import mysql.connector
from datetime import date
import time
import re


def Exec_Query(query, mode=0):
    cnx = mysql.connector.connect(
        user="root", password="123456", database="DBMS_Project"
    )
    cursor = cnx.cursor()
    try:
        cursor.execute(query)
    except:
        print("Invalid query")
        return
    ret = cursor.fetchall()
    cnx.commit()
    cursor.close()
    if mode == 0:
        if ret == []:
            return
        else:
            for i in ret:
                for j in i:
                    j = str(j)
                    print(j, end="")
                    print((25 - len(j)) * " ", end="|")
                print()
    else:
        return ret


def PrintLine():
    print(
        "---------------------------------------------------------------------------------------------------------------------------------------------"
    )
    print()


def MainMenu():
    print("\n")
    print("Welcome to the Mysql Query Tool\n")
    print(
        "Please select an option from the menu:\n"
        + "1. List all available products\n"
        + "2. User Signup/Login\n"
        + "3. Exit\n"
    )
    PrintLine()
    choice = int(input("Enter your choice: "))
    if choice == 1:
        print(
            "Category ID              |Category Name            |Product Id               |Product Names            |Product Count            |"
        )
        PrintLine()
        Exec_Query(
            "SELECT c.category_id, c.fname AS category_name, p.product_id, p.name as product_name, p.quantity AS product_count FROM Category c LEFT JOIN Product p ON c.category_id = p.category_id GROUP BY c.category_id, c.fname, p.product_id;"
        )
        PrintLine()
        MainMenu()
    elif choice == 2:
        User_Menu_1()
    elif choice == 3:
        exit()
    else:
        print("Invalid choice")
        MainMenu()

def validate_name(name):
    return name.isalpha() and len(name) >= 3

def validate_phone(phone):
    return len(phone) == 10 and phone.isdigit()

def validate_email(email):
    return re.match(r"[^@]+@[^@]+\.[^@]+", email)

def User_Menu_1():
    PrintLine()
    print("1. Signup\n" + "2. Login\n" + "3. Back\n")
    PrintLine()
    x = int(input("Enter your choice: "))
    if x == 1:
        first_name = input("Enter your first name: ")
        if first_name == "":
            print("First name cannot be empty")
            User_Menu_1()
            return 0
        if not validate_name(first_name):
            print("First name should contain only alphabets and should be at least 3 characters")
            User_Menu_1()
            return 0
        
        last_name = input("Enter your last name: ")
        if last_name == "":
            print("Last name cannot be empty")
            User_Menu_1()
            return 0
        if not validate_name(last_name):
            print("Last name should contain only alphabets and should be at least 3 characters")
            User_Menu_1()
            return 0
        
        phone = input("Enter your phone number: ")
        ret = Exec_Query("select phone from user where phone = '" + phone + "';", 1) 
        if not validate_phone(phone):
            print("Invalid phone number format. Please enter 10 digit numbers only.")
            User_Menu_1()
            return 0
        elif ret != []:
            print("Phone number already exists")
            User_Menu_1()
            return 0
        
        email = input("Enter your email: ")
        if not validate_email(email):
            print("Invalid email format. Please enter a valid email address.")
            User_Menu_1()
            return 0
        address = input("Enter your address: ")
        if address == "":
            print("Address cannot be empty")
            User_Menu_1()
            return 0
        wallet = int(input("Enter your wallet balance: "))
        if wallet < 0:
            print("Wallet balance cannot be negative")
            User_Menu_1()
            return 0
        password = input("Enter your password: ")
        if len(password) < 8 or len(password) > 24:
            print("Password must be between 8 and 24 characters\n")
            print("Please Start Again\n")
            User_Menu_1()
            return 0
        for i in password:
            if i.isalpha() == False and i.isdigit() == False:
                print("Password must contain only alphabets and digits")
                User_Menu_1()
                return 0
        Exec_Query(
            "INSERT INTO User (first_name, last_name, phone, email, address, wallet, password) values ("
            + "'"
            + first_name
            + "', '"
            + last_name
            + "', '"
            + phone
            + "', '"
            + email
            + "', '"
            + address
            + "', "
            + str(wallet)
            + ", '"
            + password
            + "');"
        )
        print("Signup successful")
        print("Your user ID is: ", end=" ")
        ret = Exec_Query("select user_id from user where phone = '" + phone + "';", 1)
        print(ret[0][0])
        time.sleep(2)
        User_Menu_1()

    elif x == 2:
        user_id = int(input("Enter your user id: "))
        password = input("Enter your password: ")
        ret = Exec_Query(
            "select user_id, password from user where user_id = " + str(user_id) + ";",
            1,
        )
        if ret == []:
            print("Invalid User ID")
            User_Menu_1()
            return 0
        if ret[0][1] == password:
            print("Login successful")
            User_Menu_2(user_id)
        else:
            print("Invalid password")
            User_Menu_1()
            return 0
    elif x == 3:
        MainMenu()
    else:
        print("Invalid choice")
        User_Menu_1()
        return 0


def User_Menu_2(userId):
    print(
        "\nWelcome to the user menu\n"
        + "Please select an option from the menu below\n"
        + "1. View available products\n"
        + "2. View previous transactions\n"
        + "3. View account balance\n"
        + "4. Buy Product\n"
        + "5. Put product up for sale\n"
        + "6. Upgrade customer status\n"
        + "7. View account details\n"
        + "8. Update account details\n"
        + "9. Add money to wallet\n"
        + "10. Logout\n"
    )
    PrintLine()
    choice = int(input("Enter your choice: "))
    PrintLine()
    if choice == 1:
        print("     Product ID     |       Product Names      |        Price       |")
        PrintLine()
        Exec_Query(
            "SELECT p.product_id, p.name AS product_name, p.price FROM Product p WHERE p.quantity > 1 ORDER BY p.price ASC;"
        )
    elif choice == 2:
        print(
            "     Payment_id     |    Payment_date    |       Amount       |     Buy/Sell       |        Product Id"
        )
        PrintLine()
        Exec_Query(
            "SELECT Payment_ID, Payment_Date, amount, sellbuy, product_id FROM Payments WHERE user_id ="
            + str(userId)
            + " ORDER BY Payment_Date DESC;"
        )
    elif choice == 3:
        print("      Balance       |")
        PrintLine()
        Exec_Query("select wallet from User where user_id = " + str(userId) + ";")
        

    elif choice == 4:

        user_id = userId
        Exec_Query(
            "SELECT p.product_id, p.name AS product_name, p.price FROM Product p WHERE p.quantity > 1 ORDER BY p.price ASC;"
        )
        print("\n")
        product_id = int(input("Enter the product id you want to buy: "))

        # Check if the product exists and is available
        product_query = "SELECT name, price, quantity FROM Product WHERE product_id = %s AND quantity > 0"
        product_data = Exec_Query(product_query % product_id, mode=1)

        if not product_data:
            print("Invalid product ID or product is out of stock.")
        else:
            product_name, price, quantity = product_data[0]
            
            # Check if the user has enough balance
            wallet_query = "SELECT wallet FROM User WHERE user_id = %s"
            user_wallet = Exec_Query(wallet_query % user_id, mode=1)

            if not user_wallet:
                print("Invalid user ID.")
            else:
                user_wallet = user_wallet[0][0]
                if user_wallet < price:
                    print("Not enough balance in your wallet.")
                else:
                    # Deduct the price from the user's wallet
                    new_wallet_balance = user_wallet - price
                    update_wallet_query = "UPDATE User SET wallet = %s WHERE user_id = %s"
                    Exec_Query(update_wallet_query % (new_wallet_balance, user_id))
                    
                    # Add transaction to the Payments table
                    # Add transaction to the Payments table
                    payment_query = f"INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id) VALUES ('{date.today()}', {price}, 0, {user_id}, {product_id})"
                    Exec_Query(payment_query, mode=0)

                    
                    # Decrease product quantity
                    update_product_query = "UPDATE Product SET quantity = quantity - 1 WHERE product_id = %s"
                    Exec_Query(update_product_query % product_id, mode=0)
                    
                    print("Transaction successful. You bought:", product_name, "for Rs ", price, "from your wallet.")

    elif choice == 5:
        name = input("Enter the name of the product: ")
        price = int(input("Enter the price of the product: "))
        description = input("Enter the description of the product: ")
        quantity = int(input("Enter the quantity of the product: "))
        print("Choose a category for the product: ")
        Exec_Query("SELECT fname, category_id FROM category;")
        category_id = int(input("Enter the category id: "))
        Exec_Query(
            "INSERT INTO Product (name, price, description, quantity, seller_id, category_id) values ('"
            + name
            + "', '"
            + str(price)
            + "', '"
            + description
            + "', "
            + str(quantity)
            + ", "
            + str(userId)
            + ", "
            + str(category_id)
            + ");"
        )
        print("Product added successfully")
    elif choice == 6:
        ret = Exec_Query(
            "select wallet, prime_user from user where user_id =" + str(userId) + ";", 1
        )
        if ret[0][1] == 1:
            print("You are already a prime user")
        elif ret[0][0] < 1000:
            print("You do not have enough balance to upgrade to prime user")
        else:
            Exec_Query(
                "update user set prime_user = 1 where user_id = " + str(userId) + ";"
            )
            Exec_Query(
                "update user set wallet = wallet - 1000 where user_id = "
                + str(userId)
                + ";"
            )
            print("You are now a prime user")
    elif choice == 7:
        print(
            "UserID | First_Name | Last_Name | Phone | Email | Address | Prime_User | Wallet |"
        )
        PrintLine()
        Exec_Query(
            "select user_id, first_name, last_name, phone, email, address, prime_user, wallet from user where user_id ="
            + str(userId)
            + ";"
        )
    elif choice == 8:
        first_name = input("Enter your first name: ")
        last_name = input("Enter your last name: ")
        phone = int(input("Enter your phone number: "))
        email = input("Enter your email: ")
        address = input("Enter your address: ")
        password = input("Enter new password: ")
        wallet = input("Enter your wallet balance: ")
        Exec_Query(
            "update user set first_name = '"
            + first_name
            + "', last_name = '"
            + last_name
            + "', phone = '"
            + str(phone)
            + "', email = '"
            + email
            + "', address = '"
            + address
            + "', password = '"
            + password
            + "', wallet = '"
            + wallet
            + "' where user_id = "
            + str(userId)
            + ";"
        )
    elif choice == 9:
        amnt = int(input("Enter the amount to be added: "))
        if amnt > 0:
            Exec_Query(
                "UPDATE User SET wallet = wallet + "
                + str(amnt)
                + " WHERE user_id = "
                + str(userId)
                + ";"
            )
        else:
            print("Error: Amount cannot be 0 or negative.")

    elif choice == 10:
        MainMenu()
    else:
        print("Invalid choice")
        PrintLine()
    time.sleep(1)
    User_Menu_2(userId)


def main():
    MainMenu()


if __name__ == "__main__":
    main()
