DROP DATABASE if EXISTS DBMS_Project;
create DATABASE DBMS_Project;
use DBMS_Project;

CREATE TABLE User(
-- (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user)
    user_id int NOT NULL AUTO_INCREMENT,
    first_name varchar(25) NOT NULL,
    last_name varchar(25) NOT NULL,
    phone long NOT NULL,
    email varchar(50) NOT NULL,
    address varchar(250) NOT NULL,
    password varchar(25) NOT NULL,
    wallet int NOT NULL,
    prime_user boolean NOT NULL DEFAULT FALSE,
    PRIMARY KEY (user_id)
);

CREATE TABLE Category(
-- (category_id, fname, description)
    category_id int NOT NULL AUTO_INCREMENT ,
    fname varchar(20) NOT NULL, 
    description varchar(250) NOT NULL,
    PRIMARY KEY (category_id)
);

CREATE TABLE Product(
-- (product_id, name, price, rating, description,  quantity, avl_status, seller_id, category_id, user_id)
    product_id int NOT NULL AUTO_INCREMENT,
    name varchar(25) NOT NULL,
    price int NOT NULL CHECK (price >= 0),
    rating float CHECK(rating>=0),
    description varchar(250),
    quantity int NOT NULL CHECK (quantity >= 0), 
    avl_status boolean NOT NULL DEFAULT TRUE,
    seller_id varchar(25) NOT NULL,
    category_id int NOT NULL,
    user_id int NULL,
    PRIMARY KEY (product_id),
    FOREIGN KEY (category_id) REFERENCES Category(category_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Payments(
-- (Payment_ID, Payment_Date, amount, sellbuy, user_id)
    Payment_ID int NOT NULL AUTO_INCREMENT,
    Payment_Date DATE NOT NULL,
    amount int NOT NULL,
    sellbuy int NOT NULL,
    user_id int NOT NULL,
    product_id int NOT NULL,
    PRIMARY KEY (Payment_ID),
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE
);

CREATE TABLE Sell_Buy_Mapping(
    SB_id int NOT NULL,
    SB_action varchar(20) NOT NULL
);
INSERT INTO Sell_Buy_Mapping (SB_id, SB_action) VALUES (0, 'Buy');
INSERT INTO Sell_Buy_Mapping (SB_id, SB_action) VALUES (1, 'Sell');


-- User Table Data
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (1, 'Jethalal', 'Gada', '9876543210', 'gada@electronics.com', 'Gokuldham Society 1', 'golibetamastinhi', 1000000, true);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (2, 'Tarak', 'Mehta', '1234567890', 'taarak@writers.com', 'Gokuldham Society 2', 'bapujirocks', 5000, false);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (3, 'Babita', 'Iyer', '8765432109', 'babita@scientists.com', 'Gokuldham Society 3', 'myganesha', 7000, false);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (4, 'Daya', 'Gada', '9876123450', 'daya@housewives.com', 'Gokuldham Society 1', 'jethajirock', 3000, false);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (5, 'Amit', 'Bhide', '6543210987', 'amit@teachers.com', 'Gokuldham Society 5', 'gokuldham123', 4500, false);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (6, 'Roshan', 'Sodhi', '8901234567', 'roshan@businessmen.com', 'Gokuldham Society 6', 'punjabirocks', 80000, false);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (7, 'Popatlal', 'Pandey', '2345678901', 'popatlal@journalists.com', 'Gokuldham Society 7', 'foreversingle', 6000, false);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (8, 'Dr. Hansraj', 'Hathi', '7654321098', 'hathi@doctors.com', 'Gokuldham Society 8', 'sodhimeetswati', 5500, true);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (9, 'Abdul', 'Miyan', '5432109876', 'abdul@shopkeepers.com', 'Gokuldham Society 10', 'dahihandi', 3500, false);
INSERT INTO User (user_id, first_name, last_name, phone, email, address, password, wallet, prime_user) VALUES (10, 'Champak', 'Chacha', '8765432100', 'champak@publishers.com', 'Gokuldham Society 1', 'comicsarelife', 4000, false);

-- Category Table Data
INSERT INTO Category (category_id, fname, description) VALUES (1, 'Phones', 'Latest and greatest piece of technology in your hands');
INSERT INTO Category (category_id, fname, description) VALUES (2, 'Clothes', 'Fashionable attire for every occasion');
INSERT INTO Category (category_id, fname, description) VALUES (3, 'Wearables', 'Smart and stylish accessories to enhance your lifestyle');
INSERT INTO Category (category_id, fname, description) VALUES (4, 'Sound & Audio', 'Immerse yourself in a high-quality listening experience');
INSERT INTO Category (category_id, fname, description) VALUES (5, 'Home Decor', 'Enhance the aesthetics of your living space');
INSERT INTO Category (category_id, fname, description) VALUES (6, 'Electronics', 'Everyday gadgets to simplify your life');
INSERT INTO Category (category_id, fname, description) VALUES (7, 'Sports', 'Gear up for your favorite physical activities');
INSERT INTO Category (category_id, fname, description) VALUES (8, 'Books', 'Expand your knowledge and imagination');
INSERT INTO Category (category_id, fname, description) VALUES (9, 'Toys', 'For the kid inside you');
INSERT INTO Category (category_id, fname, description) VALUES (10, 'Food', 'Delicious treats to satisfy your cravings');

-- Product Table Data
INSERT INTO Product (product_id, name, price, rating, description,  quantity, avl_status, seller_id, category_id, user_id) VALUES (1, 'iPhone 13', '49999', '4', 'Your new superpower', '2', true, 46, '1',null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (2, 'Samsung Galaxy S23', '59999', '4.5', 'Next-level innovation', '5', true, 45, '1', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (3, 'Denim Jacket', '2999', '4.7', 'Stylish and comfortable', '10', true, 75, '2', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (4, 'Cartoon T-Shirt', '499', '3.7', 'Fun and vibrant designs', '1', true, 75, '2', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (5, 'Apple Watch Series 6', '29999', null, 'Your health companion', '3', true, 46, '3', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (6, 'Samsung Watch 5', '12999', '4.6', 'Track your fitness goals', '6', true, 45, '3', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (7, 'Airpods Pro', '18999', '4.8', 'Immersive sound experience', '5', true, 46, '4', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (8, 'LED Diwali Lights', '1499', '4.7', 'Light up the atmosphere', '15', true, 19, '5', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (9, 'Dell XPS 13', '89999', '4.9', 'Power and portability', '3', true, 37, '6', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (10, 'Nike Air Zoom Pegasus 38', '8999', '4.8', 'Ultimate running shoes', '9', true, 75, '7', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (11, 'Atomic Habits', '799', '4.8', 'Transform your life', '10', true, 88, '8', null);
INSERT INTO Product (product_id, name, price, rating, description, quantity, avl_status, seller_id, category_id, user_id) VALUES (12, 'LEGO Classic Brick Box', '1999', '4.7', 'Unleash your creativity', '20', true, 88, '9', null);
INSERT INTO Product (product_id, name, price, rating, description,  quantity, avl_status, seller_id, category_id, user_id) VALUES (13, 'Melody Pack of 100', '100', '5', 'Melody khao khud jan jao', '4', true, 22, '10',null);

-- Payments Table Data
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (1, '2024-01-14', '59999', 0, 2,2);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (2, '2024-01-14', '1499', 0, 6,8);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (3, '2024-01-19', '799', 0, 2,11);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (4, '2024-01-20', '100', 1, 9,13);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (5, '2024-01-22', '799', 0, 5,11);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (6, '2024-01-26', '8999', 0, 3,10);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (7, '2024-01-26', '49999', 0, 1,1);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (8, '2024-01-26', '89999', 1, 1,9);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (9, '2024-02-01', '499', 0, 4,4);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (10, '2024-01-06', '100', 0, 8,13);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (11, '2024-01-10', '12999', 0, 5,6);
INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id, product_id) VALUES (12, '2024-01-10', '1999', 0, 7,12);

-- SELECT 
--     c.category_id,
--     c.fname AS category_name,
--     p.product_id,
--     p.name as product_name,
--     p.quantity AS product_count
-- FROM 
--     Category c
-- LEFT JOIN 
--     Product p ON c.category_id = p.category_id
-- GROUP BY 
--     c.category_id, c.fname, p.product_id;

DELIMITER //
CREATE TRIGGER update_wallet_after_payment
AFTER INSERT ON Payments
FOR EACH ROW
BEGIN
    DECLARE action_amount INT;
    SELECT CASE WHEN NEW.sellbuy = 0 THEN -NEW.amount ELSE NEW.amount END INTO action_amount;
    UPDATE User SET wallet = wallet + action_amount WHERE user_id = NEW.user_id;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER prevent_negative_balance
BEFORE INSERT ON Payments
FOR EACH ROW
BEGIN
    DECLARE current_balance INT;
    DECLARE new_balance INT;
    SELECT wallet INTO current_balance FROM User WHERE user_id = NEW.user_id;
    IF NEW.sellbuy = 0 THEN
        SET new_balance = current_balance - NEW.amount;
    ELSE
        SET new_balance = current_balance + NEW.amount;
    END IF;
    IF new_balance < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Preventing negative balance in wallet';
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER handle_price_change
BEFORE INSERT ON Payments
FOR EACH ROW
BEGIN
    DECLARE product_price INT;
    SELECT price INTO product_price FROM Product WHERE product_id = NEW.product_id;
    -- Checking if the payment amount matches the product price
    IF product_price != NEW.amount THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Amount mismatch, please try again';
    END IF;
END;
//
DELIMITER ;

DELIMITER //

CREATE TRIGGER decrement_product_quantity
AFTER INSERT ON Payments
FOR EACH ROW
BEGIN
    DECLARE current_quantity INT;
    DECLARE current_availability BOOLEAN;
    
    -- Get current quantity and availability status
    SELECT quantity, avl_status INTO current_quantity, current_availability
    FROM Product
    WHERE product_id = NEW.product_id;
    
    -- Decrement quantity
    UPDATE Product
    SET quantity = quantity - 1
    WHERE product_id = NEW.product_id;
    
    -- Update availability status if quantity reaches 0
    IF current_quantity = 1 THEN
        UPDATE Product
        SET avl_status = FALSE
        WHERE product_id = NEW.product_id;
    END IF;
END;
//
DELIMITER ;


DELIMITER //
CREATE TRIGGER check_product_availability
BEFORE INSERT ON Payments
FOR EACH ROW
BEGIN
    DECLARE product_quantity INT;
    SELECT quantity INTO product_quantity FROM Product WHERE product_id = NEW.product_id;
    IF product_quantity <= 0 THEN
        SIGNAL SQLSTATE '46000' SET MESSAGE_TEXT = 'Product is not available';
    END IF;
END;
//
DELIMITER ;



-- Conflicting Transaction 3 Simulate out of stock
-- START TRANSACTION;
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id)VALUES ('2024-04-20', 49999, 0, 1, 1);
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id)VALUES ('2024-04-20', 49999, 0, 1, 1);
-- select * from product;
-- COMMIT;
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id)VALUES ('2024-04-20', 49999, 0, 1, 1);


-- Conflicting Transaction 2 Simulate price change
-- START TRANSACTION;
-- UPDATE Product
-- SET price = 50000
-- WHERE product_id = 1;
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id)
-- VALUES ('2024-04-20', 49999, 0, 1, 1);
-- COMMIT;

-- Conflicting Transaction 1 Simulate negative wallet balance
-- START TRANSACTION;
-- SELECT wallet INTO @current_balance FROM User WHERE user_id = 4;
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id) VALUES ('2024-04-20', 49999, 0, 4, 1);
-- COMMIT;

-- select * from Product;
-- trigger testing query
-- INSERT INTO Payments (Payment_ID, Payment_Date, amount, sellbuy, user_id) VALUES (13, '2024-01-20', '3700', 1, 9);
-- select * from User;

-- Non Conflicting Transaction 1 different users buy item and update their wallet balnce
-- START TRANSACTION;
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id) 
-- VALUES ('2024-04-20', 18999, 0, 1, 7);
-- UPDATE User SET wallet = wallet + 5000 WHERE user_id = 5;
-- COMMIT;

-- Non Conflicting Transaction 2 Seller price change and user buys some other product
-- START TRANSACTION;
-- UPDATE Product
-- SET price = 55000
-- WHERE product_id = 1;
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id)
-- VALUES ('2024-04-20', 100, 0, 1, 13);
-- COMMIT;

-- Non Conflicting Transaction 3 user buys product before Seller price change
-- START TRANSACTION;
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id)
-- VALUES ('2024-04-20', 49999, 0, 1, 1);
-- UPDATE Product
-- SET avl_status = false
-- WHERE product_id = 1;
-- COMMIT;

-- Non Conflicting Transaction 4 user lists a product to sell, someone buys it and seller is credited
-- INSERT INTO Product (name, price, description,  quantity, avl_status, seller_id, category_id, user_id) 
-- VALUES ('Pixel 6a', '24999', 'Best of Google', '3', true, 19, '1',1);
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id) 
-- VALUES ('2024-04-20', 24999, 0, 6, 14);
-- INSERT INTO Payments (Payment_Date, amount, sellbuy, user_id, product_id) 
-- VALUES ('2024-04-20', 24999, 1, 1, 14);


-- Query 1:This query returns number of products in every category
-- SELECT c.category_id, c.fname AS category_name, COUNT(p.product_id) AS product_count
-- FROM Category c
-- LEFT JOIN Product p ON c.category_id = p.category_id
-- GROUP BY c.category_id, c.fname;

-- SELECT c.category_id, c.fname AS category_name, 
--        COUNT(p.product_id) AS product_count, 
--        GROUP_CONCAT(p.name) AS product_names
-- FROM Category c
-- LEFT JOIN Product p ON c.category_id = p.category_id
-- WHERE p.quantity > 0
-- GROUP BY c.category_id, c.fname;

-- Query 2:Update the status of a product to out of stock and sets quantity to 0
-- UPDATE Product
-- SET avl_status = false, quantity = 0
-- WHERE product_id = 2;
-- select * from product; 

-- Query 3:Creates a filter sorted by rating
-- SELECT product_id, name as 'pname', price, fname as 'cname', rating
-- FROM Product, Category
-- WHERE avl_status = true
-- AND Product.category_id = 2
-- AND Category.category_id = 2
-- ORDER BY rating DESC;

-- Query 4 Creates a filter in category 1 where price is in the range of 45000 and 50000 
-- SELECT product_id, name , price, fname as 'category'
-- FROM Product, Category
-- WHERE avl_status = true
-- AND Product.category_id = 1
-- AND Category.category_id = 1
-- AND price BETWEEN 45000 AND 60000;

-- Query 5:Creates a filter in category 1 by rating
-- SELECT * 
-- FROM Product 
-- WHERE category_id=1 and rating > 4;

-- Query 6: Shows the most valuable customers to target by most spend
-- SELECT u.user_id, u.first_name, u.last_name, SUM(p.amount) AS total_payments
-- FROM User u
-- LEFT JOIN Payments p ON u.user_id = p.user_id
-- GROUP BY u.user_id, u.first_name, u.last_name
-- ORDER BY total_payments DESC;

-- Query 7: Provide Prime benefits to valuable customers by most spend
-- UPDATE User u
-- JOIN (
--     SELECT p.user_id, u.first_name, u.last_name, SUM(p.amount) AS total_spend
--     FROM Payments p
--     INNER JOIN User u ON p.user_id = u.user_id
--     GROUP BY p.user_id, u.first_name, u.last_name
-- ) AS spend_summary ON u.user_id = spend_summary.user_id
-- SET u.prime_user = true
-- WHERE spend_summary.total_spend > 10000;



-- Query 8 updating price and price can't be lesser than 0 and if out of stock sets quantity to 1
-- UPDATE Product
-- SET avl_status = true,
--     quantity = CASE WHEN quantity = 0 THEN 1 ELSE quantity END,
--     price = 54999
-- WHERE product_id = 1;
-- select * from product;

-- Query 9 Show idle users to focus advertising offers on by no transactions
-- SELECT u.user_id, u.first_name, u.last_name 
-- FROM User u 
-- LEFT JOIN Payments p ON u.user_id = p.user_id 
-- WHERE p.Payment_ID IS NULL;

-- Query 10 Store transaction analytics 
-- SELECT 
--     SUM(CASE WHEN sellbuy = 0 THEN amount ELSE 0 END) AS total_purchases,
--     SUM(CASE WHEN sellbuy = 1 THEN amount ELSE 0 END) AS total_sales
-- FROM Payments;

-- Query 11 Products to keep stocked up
-- SELECT * FROM Product WHERE quantity < 10
-- ORDER by quantity;

-- Query 12 Basic Price sort query
-- SELECT * FROM Product 
-- Where category_id = 2
-- ORDER BY price DESC;