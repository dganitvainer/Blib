CREATE DATABASE library_system;
USE library_system;

-- Table: Users
CREATE TABLE Users (
    UserID INT PRIMARY KEY,  -- Primary key remains UserID, but no AUTO_INCREMENT
    FullName VARCHAR(255) NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Email VARCHAR(255),
    Phone VARCHAR(20),
    UserType ENUM('Subscriber', 'Librarian') NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Address VARCHAR(255)
);

-- Table: Subscribers
CREATE TABLE Subscribers (
    SubscriberID INT PRIMARY KEY,
    Status ENUM('ACTIVE', 'FROZEN') DEFAULT 'ACTIVE',
    FOREIGN KEY (SubscriberID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- Table: Books
CREATE TABLE Books (
    BookID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    Author VARCHAR(255),
    Subject VARCHAR(255),
    Description TEXT,
    TotalCopies INT NOT NULL,
    CopiesAvailable INT NOT NULL,
    ShelfLocation VARCHAR(100)
);

-- Table: Loans
CREATE TABLE Loans (
    LoanID INT AUTO_INCREMENT PRIMARY KEY,
    SubscriberID INT NOT NULL,
    BookID INT NOT NULL,
    LoanDate DATE NOT NULL,
    ReturnDate DATE NOT NULL,
    ActualReturnDate DATE,
    FOREIGN KEY (SubscriberID) REFERENCES Subscribers(SubscriberID) ON DELETE CASCADE,
    FOREIGN KEY (BookID) REFERENCES Books(BookID) ON DELETE CASCADE
);

-- Table: Reservations
CREATE TABLE Reservations (
    ReservationID INT AUTO_INCREMENT PRIMARY KEY,
    SubscriberID INT NOT NULL,
    BookID INT NOT NULL,
    ReservationDate DATE NOT NULL,
    ExpirationDate DATE NULL,
    Status ENUM('PENDING', 'FULFILLED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (SubscriberID) REFERENCES Subscribers(SubscriberID) ON DELETE CASCADE,
    FOREIGN KEY (BookID) REFERENCES Books(BookID) ON DELETE CASCADE
);

-- Table: Notifications
CREATE TABLE Notifications (
    NotificationID INT AUTO_INCREMENT PRIMARY KEY,
    SubscriberID INT NOT NULL,
    Message TEXT NOT NULL,
    NotificationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    Type ENUM(
        'REMINDER', 
        'RESERVATION_READY', 
        'LATE_RETURN', 
        'EXTENSION_APPROVED', 
        'EXTENSION_DECLINED', 
        'CANCELLED_RESERVATION', 
        'RESERVATION_EXPIRED',
        'OTHER'
    ) NOT NULL,
    FOREIGN KEY (SubscriberID) REFERENCES Subscribers(SubscriberID) ON DELETE CASCADE 
);


-- Table: ActivityLog
CREATE TABLE ActivityLog (
    ActivityID INT AUTO_INCREMENT PRIMARY KEY,
    SubscriberID INT NOT NULL,
    ActivityType ENUM('LOAN', 'RESERVATION', 'NOTIFICATION', 'OTHER', 'RETURN', 'EXTENSION') NOT NULL,
    ActivityDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    BookID INT,
    Message TEXT,
    FOREIGN KEY (SubscriberID) REFERENCES Subscribers(SubscriberID) ON DELETE CASCADE,
    FOREIGN KEY (BookID) REFERENCES Books(BookID) ON DELETE CASCADE
);

-- Table: SubscriberStatusHistory
CREATE TABLE SubscriberStatusHistory (
    HistoryID INT AUTO_INCREMENT PRIMARY KEY,
    SubscriberID INT NOT NULL,
    Status ENUM('ACTIVE', 'FROZEN') NOT NULL,
    ChangeDate DATE NOT NULL,
    Reason TEXT,
    FOREIGN KEY (SubscriberID) REFERENCES Subscribers(SubscriberID) ON DELETE CASCADE
);

-- Insert data into tables

-- Users
INSERT INTO Users (UserID, FullName, PasswordHash, Email, Phone, UserType, Address) VALUES
(3157, 'Harel Aronovich', 'Harel123', 'HarelAr@example.com', '0547892255', 'Subscriber', '123 Main St'),
(3154, 'Ora Smith', 'Ora56', 'Ora@example.com', '0555555555', 'Librarian', '456 Elm St'),
(3155, 'Nir gluck', 'Nir211', 'Niros@example.com', '0555229517', 'Librarian', '456 Elm St'),
(0000, 'System Managment', 'system_manager', 'System@manager.com', '0555229517', 'Subscriber', 'braude'),
(1111, 'Anat Dahan', 'anat1111', 'anatdhn@braude.ac.il', '0571576659', 'Subscriber', 'braude'),
(2222, 'Tiran hesawi', 'Tiran2222', 'hesawi@braude.ac.il ', '0571523459', 'Subscriber', 'braude'),
(3333, 'Ilya  zeldner', 'ilya3333', ' zeldner26@gmail.com  ', '0571523459', 'Librarian', 'braude'),
(4444, 'katerina  Kornblatt', 'Tiran2222', 'katerina@braude.ac.il ', '0571523459', 'Librarian', 'braude');


-- Subscribers
INSERT INTO Subscribers (SubscriberID, Status) VALUES
(3157, 'ACTIVE'),
(3155, 'ACTIVE'),
(0000, 'ACTIVE'),
(1111, 'ACTIVE'),
(2222, 'FROZEN'),
(3333, 'ACTIVE'),
(4444, 'ACTIVE');

-- Books
INSERT INTO Books (Title, Author, Subject, Description, TotalCopies, CopiesAvailable, ShelfLocation) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', 'Literature', 'A classic novel set in the 1920s', 10, 8, 'A1-01'),
('1984', 'George Orwell', 'Dystopian', 'A dystopian social science fiction novel', 5, 3, 'B2-02'),
('To Kill a Mockingbird', 'Harper Lee', 'Literature', 'A novel about racial injustice', 7, 7, 'C3-03');

-- Loans
INSERT INTO Loans (SubscriberID, BookID, LoanDate, ReturnDate, ActualReturnDate) VALUES
(3157, 1, '2025-01-01', '2025-01-15', NULL),
(3157, 2, '2025-01-10', '2025-01-24', '2025-01-20');

-- Reservations
INSERT INTO Reservations (SubscriberID, BookID, ReservationDate, ExpirationDate, Status) VALUES
(3157, 3, '2025-01-05', '2025-01-12', 'FULFILLED'),
(3157, 2, '2025-01-15', '2025-01-22', 'PENDING');

-- Notifications
INSERT INTO Notifications (SubscriberID, Message, Type) VALUES
(3157, 'Your reserved book "To Kill a Mockingbird" is ready for pickup.', 'RESERVATION_READY'),
(3157, 'Your loaned book "The Great Gatsby" is due soon.', 'REMINDER');

-- ActivityLog
INSERT INTO ActivityLog (SubscriberID, ActivityType, BookID, Message) VALUES
(3157, 'LOAN', 1, 'Loaned "The Great Gatsby".'),
(3157, 'RESERVATION', 3, 'Reserved "To Kill a Mockingbird".');

-- SubscriberStatusHistory
INSERT INTO SubscriberStatusHistory (SubscriberID, Status, ChangeDate, Reason) VALUES
(3157, 'FROZEN', '2025-01-18', 'Overdue books not returned on time.'),
(3157, 'ACTIVE', '2025-01-20', 'Overdue books returned and fine paid.');

ALTER TABLE ActivityLog 
ADD LibrarianID INT,
ADD FOREIGN KEY (LibrarianID) REFERENCES Users(UserID);

UPDATE ActivityLog
SET LibrarianID = (
    SELECT MIN(UserID) 
    FROM Users 
    WHERE UserType = 'Librarian'
) 
WHERE LibrarianID IS NULL;

INSERT INTO Books (Title, Author, Subject, Description, TotalCopies, CopiesAvailable, ShelfLocation) VALUES
('Pride and Prejudice', 'Jane Austen', 'Classic Literature', 'A classic romantic novel', 5, 5, 'A2-01'),
('Brave New World', 'Aldous Huxley', 'Dystopian Fiction', 'A futuristic dystopian novel', 3, 3, 'B1-02'),
('The Catcher in the Rye', 'J.D. Salinger', 'Coming of Age', 'A novel about teenage angst and alienation', 5, 5, 'C3-01'),  
('Lord of the Flies', 'William Golding', 'Allegory', 'An allegorical novel about a group of boys stranded on an island', 4, 4, 'C3-02'),
('Animal Farm', 'George Orwell', 'Satire', 'A satirical novella about a group of farm animals', 7, 7, 'D4-01'),
('Fahrenheit 451', 'Ray Bradbury', 'Dystopian Fiction', 'A dystopian novel about a future society where books are banned', 6, 6, 'D4-02'),
('The Grapes of Wrath', 'John Steinbeck', 'Great Depression', 'A novel about a familys journey during the Great Depression', 3, 3, 'E5-01'),  
('Of Mice and Men', 'John Steinbeck', 'Novella', 'A novella about two migrant workers during the Great Depression', 5, 5, 'E5-02'),
('One Hundred Years of Solitude', 'Gabriel Garcia Marquez', 'Magical Realism', 'A multi-generational story of the Buendia family', 3, 3, 'F6-02'),
('Catch-22', 'Joseph Heller', 'Satire', 'A satirical war novel set during World War II', 5, 5, 'G7-01'),
('Slaughterhouse-Five', 'Kurt Vonnegut', 'Science Fiction', 'A satirical novel with time travel and aliens', 6, 6, 'G7-02'),
('The Handmaids Tale', 'Margaret Atwood', 'Dystopian Fiction', 'A dystopian novel set in a totalitarian society', 5, 5, 'H8-02'),
('The Hitchhikers Guide to the Galaxy', 'Douglas Adams', 'Science Fiction', 'A comedic science fiction series', 7, 7, 'I9-01'),
('Dune', 'Frank Herbert', 'Science Fiction', 'A science fiction novel set in a desert world', 4, 4, 'I9-02'),
('The Lord of the Rings', 'J.R.R. Tolkien', 'Fantasy', 'An epic high fantasy novel', 6, 6, 'J10-01'),
('The Hobbit', 'J.R.R. Tolkien', 'Fantasy', 'A fantasy novel about a hobbits adventure', 5, 5, 'J10-02'),
('The Chronicles of Narnia', 'C.S. Lewis', 'Fantasy', 'A series of fantasy novels set in the land of Narnia', 7, 7, 'K11-01'),
('The Lion, the Witch and the Wardrobe', 'C.S. Lewis', 'Fantasy', 'The first book in The Chronicles of Narnia series', 4, 4, 'K11-02'),
('The Divine Comedy', 'Dante Alighieri', 'Epic Poetry', 'An epic poem divided into three parts: Inferno, Purgatorio, and Paradiso', 3, 3, 'L12-01'),
('The Canterbury Tales', 'Geoffrey Chaucer', 'Poetry', 'A collection of 24 stories told by a group of pilgrims', 5, 5, 'L12-02'),
("Gulliver's Travels", 'Jonathan Swift', 'Satire', 'A satirical novel about the adventures of Lemuel Gulliver', 4, 4, 'M13-01'),
('Robinson Crusoe', 'Daniel Defoe', 'Adventure', 'A novel about a castaway who spends 28 years on a remote island', 6, 6, 'M13-02'),
('Frankenstein', 'Mary Shelley', 'Gothic Fiction', 'A Gothic novel about a scientist who creates a sapient creature', 5, 5, 'N14-01'),
('Dracula', 'Bram Stoker', 'Gothic Fiction', 'A Gothic horror novel about Count Dracula', 3, 3, 'N14-02'),
('The Picture of Dorian Gray', 'Oscar Wilde', 'Gothic Fiction', 'A philosophical novel about a man who sells his soul for eternal youth', 4, 4, 'O15-01'),
('The Strange Case of Dr Jekyll and Mr Hyde', 'Robert Louis Stevenson', 'Gothic Fiction', 'A novella about a lawyer investigating the connection between his old friend Dr. Jekyll and the evil Mr. Hyde', 5, 5, 'O15-02'),
('Moby-Dick', 'Herman Melville', 'Adventure', "A novel about Captain Ahab's obsessive quest for a giant whale", 6, 6, 'P16-01'),
('The Adventures of Huckleberry Finn', 'Mark Twain', 'Adventure', "A novel about a boy and a runaway slave's journey down the Mississippi River", 4, 4, 'P16-02'),
('The Adventures of Tom Sawyer', 'Mark Twain', 'Adventure', 'A novel about a young boy growing up along the Mississippi River', 3, 3, 'Q17-01'),
('The Call of the Wild', 'Jack London', 'Adventure', "A novel about a dog's journey from domestication to the wild", 5, 5, 'Q17-02'),
('The Jungle Book', 'Rudyard Kipling', "Children's Literature", "A collection of stories about the adventures of Mowgli", 4, 4, 'R18-01'),
('The Wind in the Willows', 'Kenneth Grahame', "Children's Literature", "A novel about the adventures of four anthropomorphized animals", 6, 6, 'R18-02'),
('The Tale of Peter Rabbit', 'Beatrix Potter', "Children's Literature", 'A story about a mischievous rabbit and his adventures in a vegetable garden', 5, 5, 'S19-01'),
('The Cat in the Hat', 'Dr. Seuss', "Children's Literature", "A story about a tall anthropomorphic cat and his adventures", 7, 7, 'S19-02'),
('Anne of Green Gables', 'Lucy Maud Montgomery', "Children's Literature", "A novel about the adventures of Anne Shirley, an 11-year-old orphan girl", 3, 3, 'T20-01'),
('Little Women', 'Louisa May Alcott', 'Coming of Age', 'A novel about the March sisters and their passage from childhood to womanhood', 4, 4, 'T20-02'),
('The Secret Garden', 'Frances Hodgson Burnett', "Children's Literature", "A novel about a young girl who discovers a secret garden", 5, 5, 'U21-01'),
('A Little Princess', 'Frances Hodgson Burnett', "Children's Literature", 'A novel about a young girl who faces adversity at a boarding school', 6, 6, 'U21-02'),
('The Wonderful Wizard of Oz', 'L. Frank Baum', 'Fantasy', 'A novel about a girl named Dorothy and her adventures in the Land of Oz', 4, 4, 'V22-01'),
("Alice's Adventures in Wonderland", 'Lewis Carroll', 'Fantasy', 'A novel about a girl named Alice and her surreal adventures', 3, 3, 'V22-02'),
('Peter Pan', 'J.M. Barrie', 'Fantasy', 'A play and novel about a mischievous boy who can fly and never grows up', 5, 5, 'W23-01'),
('The Story of Doctor Dolittle', 'Hugh Lofting', 'Fantasy', 'A novel about a doctor who can talk to animals', 6, 6, 'W23-02'),
('Treasure Island', 'Robert Louis Stevenson', 'Adventure', 'A novel about buccaneers and buried gold', 4, 4, 'X24-01'),
('The Three Musketeers', 'Alexandre Dumas', 'Historical Fiction', "A novel about the adventures of d'Artagnan and his musketeer friends", 5, 5, 'X24-02'),
('The Count of Monte Cristo', 'Alexandre Dumas', 'Adventure', "A novel about a man's journey of revenge after being wrongfully imprisoned", 6, 6, 'Y25-01'),
('The Scarlet Pimpernel', 'Baroness Orczy', 'Historical Fiction', 'A novel about an English aristocrat who rescues French nobles during the Reign of Terror', 3, 3, 'Y25-02'),
('The Hunchback of Notre-Dame', 'Victor Hugo', 'Historical Fiction', 'A novel about Quasimodo, the bell-ringer of Notre-Dame Cathedral', 4, 4, 'Z26-01'),
('Les Misérables', 'Victor Hugo', 'Historical Fiction', 'A novel about ex-convict Jean Valjean and his journey for redemption', 5, 5, 'Z26-02'),
('War and Peace', 'Leo Tolstoy', 'Historical Fiction', 'An epic novel about Russian society during the Napoleonic Era', 3, 3, 'AA27-01'),
('Anna Karenina', 'Leo Tolstoy', 'Realist Fiction', 'A novel about the tragedy of a married woman and her affair with Count Vronsky', 4, 4, 'AA27-02'),
('Crime and Punishment', 'Fyodor Dostoevsky', 'Psychological Fiction', 'A novel about the mental anguish and moral dilemmas of Rodion Raskolnikov', 5, 5, 'AB28-01'),
('The Brothers Karamazov', 'Fyodor Dostoevsky', 'Philosophical Fiction', 'A novel about the philosophical and moral debates of the Karamazov brothers', 6, 6, 'AB28-02'),
('The Idiot', 'Fyodor Dostoevsky', 'Philosophical Fiction', 'A novel about Prince Myshkin, a kind-hearted but naive man', 3, 3, 'AC29-01'),
('Notes from Underground', 'Fyodor Dostoevsky', 'Existentialist Fiction', 'A novella about the psychology of a man who has withdrawn from society', 4, 4, 'AC29-02'),
('Madame Bovary', 'Gustave Flaubert', 'Realist Fiction', "A novel about a woman's misery in her marriage and love affairs", 5, 5, 'AD30-01'),
('The Red and the Black', 'Stendhal', 'Psychological Fiction', 'A novel about the attempts of a young man to rise above his social status', 6, 6, 'AD30-02'),
('The Charterhouse of Parma', 'Stendhal', 'Psychological Fiction', 'A novel about the life of Fabrizio del Dongo and his love affairs', 4, 4, 'AE31-01'),
('The Sorrows of Young Werther', 'Johann Wolfgang von Goethe', 'Epistolary Fiction', 'A novel about the sorrows of a young man in love', 3, 3, 'AE31-02'),
('Faust', 'Johann Wolfgang von Goethe', 'Tragedy', 'A play about a man who makes a pact with the devil', 5, 5, 'AF32-01'),
('The Metamorphosis', 'Franz Kafka', 'Absurdist Fiction', 'A novella about a man who wakes up to find himself transformed into a giant insect', 6, 6, 'AF32-02'),
('The Trial', 'Franz Kafka', 'Absurdist Fiction', 'A novel about a man arrested and prosecuted by a remote, inaccessible authority', 4, 4, 'AG33-01'),
('The Castle', 'Franz Kafka', 'Absurdist Fiction', 'A novel about a man who struggles to gain access to the mysterious authorities of a castle', 3, 3, 'AG33-02'),
('The Stranger', 'Albert Camus', 'Existentialist Fiction', 'A novel about an indifferent French Algerian who kills an Arab man', 5, 5, 'AH34-01'),
('The Plague', 'Albert Camus', 'Existentialist Fiction', 'A novel about a plague that ravages the French Algerian city of Oran', 6, 6, 'AH34-02');


INSERT INTO Users (UserID, FullName, PasswordHash, Email, Phone, UserType, Address) VALUES
(3171, 'Sarah Johnson', 'Sarahpass1', 'sarah.johnson@example.com', '1234567890', 'Subscriber', '789 Oak Ave'),
(3172, 'David Lee', 'Davidpass2', 'david.lee@example.com', '9876543210', 'Subscriber', '321 Maple Rd'),
(3173, 'Emily Davis', 'Emilypass3', 'emily.davis@example.com', '4561237890', 'Subscriber', '654 Pine St'),
(3174, 'Michael Brown', 'Michaelpass4', 'michael.brown@example.com', '7894561230', 'Subscriber', '987 Cedar Ln'),
(3175, 'Jessica Wilson', 'Jessicapass5', 'jessica.wilson@example.com', '3216549870', 'Subscriber', '741 Walnut Ave'),
(3176, 'Christopher Taylor', 'Chrispass6', 'chris.taylor@example.com', '9638527410', 'Subscriber', '852 Birch Rd'),
(3177, 'Amanda Anderson', 'Amandapass7', 'amanda.anderson@example.com', '1472583690', 'Subscriber', '963 Spruce St'),
(3178, 'Matthew Thomas', 'Matthewpass8', 'matthew.thomas@example.com', '8529637410', 'Subscriber', '159 Oak Ln'),
(3179, 'Olivia Martinez', 'Oliviapass9', 'olivia.martinez@example.com', '3698521470', 'Subscriber', '753 Maple Ave'),
(3180, 'Daniel Robinson', 'Danielpass10', 'daniel.robinson@example.com', '2147483647', 'Subscriber', '951 Pine Rd'),
(3181, 'Sophia Clark', 'Sophiapass11', 'sophia.clark@example.com', '3692581470', 'Subscriber', '357 Cedar St'),
(3182, 'Andrew Hall', 'Andrewpass12', 'andrew.hall@example.com', '1593574682', 'Subscriber', '246 Walnut Ln'),
(3183, 'Avery Wright', 'Averypass13', 'avery.wright@example.com', '7539514682', 'Subscriber', '864 Birch Ave'),
(3184, 'Evelyn Lopez', 'Evelynpass14', 'evelyn.lopez@example.com', '9517538246', 'Subscriber', '579 Spruce Rd'),
(3185, 'William Gonzalez', 'Williampass15', 'william.gonzalez@example.com', '3578642190', 'Subscriber', '135 Oak St'),
(3186, 'Emma Lewis', 'Emmapass16', 'emma.lewis@library.com', '7531598520', 'Librarian', '246 Maple Ave'),
(3187, 'James Young', 'Jamespass17', 'james.young@library.com', '9517532468', 'Librarian', '864 Pine Rd'),
(3188, 'Mia King', 'Miapass18', 'mia.king@library.com', '3578641290', 'Librarian', '579 Cedar Ln');
INSERT INTO Subscribers (SubscriberID, Status) VALUES
(3171, 'ACTIVE'), (3172, 'ACTIVE'), (3173, 'ACTIVE'), (3174, 'ACTIVE'), (3175, 'ACTIVE'),
(3176, 'ACTIVE'), (3177, 'FROZEN'), (3178, 'ACTIVE'), (3179, 'ACTIVE'), (3180, 'ACTIVE'),
(3181, 'ACTIVE'), (3182, 'ACTIVE'), (3183, 'ACTIVE'), (3184, 'ACTIVE'), (3185, 'ACTIVE');


SET SQL_SAFE_UPDATES = 0;

-- Set up variables for dates
SET @current_date = CURRENT_DATE();
SET @start_date = DATE_SUB(@current_date, INTERVAL 30 DAY);

-- Insert 48 on-time returns
INSERT INTO Loans (SubscriberID, BookID, LoanDate, ReturnDate, ActualReturnDate)
SELECT 
    CASE MOD(nums.n, 15) 
        WHEN 0 THEN 3171 
        WHEN 1 THEN 3172
        WHEN 2 THEN 3173
        WHEN 3 THEN 3174
        WHEN 4 THEN 3175
        WHEN 5 THEN 3176
        WHEN 6 THEN 3177
        WHEN 7 THEN 3178
        WHEN 8 THEN 3179
        WHEN 9 THEN 3180
        WHEN 10 THEN 3181
        WHEN 11 THEN 3182
        WHEN 12 THEN 3183
        WHEN 13 THEN 3184
        ELSE 3185
    END as SubscriberID,
    MOD(nums.n, 50) + 1 as BookID,
    DATE_SUB(@current_date, INTERVAL (28 - MOD(nums.n, 14)) DAY) as LoanDate,
    DATE_SUB(@current_date, INTERVAL (14 - MOD(nums.n, 7)) DAY) as ReturnDate,
    DATE_SUB(@current_date, INTERVAL (14 - MOD(nums.n, 7)) DAY) as ActualReturnDate
FROM (
    SELECT a.N + b.N * 10 as n
    FROM (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) b
    LIMIT 48
) nums;

-- Insert 6 returns that were 1-7 days late
INSERT INTO Loans (SubscriberID, BookID, LoanDate, ReturnDate, ActualReturnDate)
SELECT 
    CASE MOD(nums.n, 6)
        WHEN 0 THEN 3171
        WHEN 1 THEN 3173
        WHEN 2 THEN 3175
        WHEN 3 THEN 3177
        WHEN 4 THEN 3179
        ELSE 3181
    END as SubscriberID,
    MOD(nums.n + 50, 50) + 1 as BookID,
    DATE_SUB(@current_date, INTERVAL 21 DAY) as LoanDate,
    DATE_SUB(@current_date, INTERVAL 7 DAY) as ReturnDate,
    DATE_SUB(@current_date, INTERVAL (6 - nums.n) DAY) as ActualReturnDate
FROM (
    SELECT n FROM (SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) nums
) nums;

-- Insert 3 returns that were 8-10 days late
INSERT INTO Loans (SubscriberID, BookID, LoanDate, ReturnDate, ActualReturnDate)
VALUES 
    (3172, 21, DATE_SUB(@current_date, INTERVAL 25 DAY), DATE_SUB(@current_date, INTERVAL 11 DAY), DATE_SUB(@current_date, INTERVAL 3 DAY)),
    (3174, 22, DATE_SUB(@current_date, INTERVAL 25 DAY), DATE_SUB(@current_date, INTERVAL 11 DAY), DATE_SUB(@current_date, INTERVAL 2 DAY)),
    (3176, 23, DATE_SUB(@current_date, INTERVAL 25 DAY), DATE_SUB(@current_date, INTERVAL 11 DAY), DATE_SUB(@current_date, INTERVAL 1 DAY));

-- Insert 1 return that was 23 days late
INSERT INTO Loans (SubscriberID, BookID, LoanDate, ReturnDate, ActualReturnDate)
VALUES 
    (3178, 24, DATE_SUB(@current_date, INTERVAL 40 DAY), DATE_SUB(@current_date, INTERVAL 26 DAY), DATE_SUB(@current_date, INTERVAL 3 DAY));

-- Update subscriber status to FROZEN only for returns more than 7 days late
UPDATE Subscribers 
SET Status = 'FROZEN' 
WHERE SubscriberID IN (
    SELECT DISTINCT l.SubscriberID 
    FROM Loans l 
    WHERE DATEDIFF(l.ActualReturnDate, l.ReturnDate) > 7
);

-- Add status history records for frozen subscribers
INSERT INTO SubscriberStatusHistory (SubscriberID, Status, ChangeDate, Reason)
SELECT DISTINCT
    l.SubscriberID,
    'FROZEN',
    l.ActualReturnDate,
    CONCAT('Late return by ', DATEDIFF(l.ActualReturnDate, l.ReturnDate), ' days')
FROM Loans l
WHERE DATEDIFF(l.ActualReturnDate, l.ReturnDate) > 7;

-- Add activity logs for all loans
INSERT INTO ActivityLog (SubscriberID, BookID, ActivityType, ActivityDate, Message, LibrarianID)
SELECT 
    l.SubscriberID,
    l.BookID,
    'RETURN',
    l.ActualReturnDate,
    CONCAT(
        'Book returned ', 
        CASE 
            WHEN DATEDIFF(l.ActualReturnDate, l.ReturnDate) <= 0 THEN 'on time'
            ELSE CONCAT(DATEDIFF(l.ActualReturnDate, l.ReturnDate), ' days late')
        END
    ),
    3186
FROM Loans l
WHERE l.ActualReturnDate IS NOT NULL;

SET SQL_SAFE_UPDATES = 1;

ALTER TABLE ActivityLog 
MODIFY COLUMN ActivityType ENUM('LOAN', 'RESERVATION', 'NOTIFICATION', 'OTHER', 'RETURN', 'EXTENSION');


ALTER TABLE ActivityLog  MODIFY COLUMN ActivityType ENUM('LOAN', 'RESERVATION', 'NOTIFICATION', 'OTHER', 'RETURN', 'EXTENSION');

UPDATE Books 
SET CopiesAvailable = 0 
WHERE BookID IN (1, 2);
INSERT INTO Loans (SubscriberID, BookID, LoanDate, ReturnDate, ActualReturnDate) VALUES
(3171, 3, '2025-01-30', '2025-01-31', NULL),
(3172, 4, '2025-01-30', '2025-02-05', NULL),
(3173, 5, '2025-01-30', '2025-02-03', NULL),
(3174, 6, '2025-01-20', '2025-01-27', NULL),
(3175, 7, '2025-01-15', '2025-01-25', NULL);
UPDATE Subscribers 
SET Status = 'FROZEN' 
WHERE SubscriberID = 3173;
INSERT INTO ActivityLog (SubscriberID, BookID, ActivityType, Message, LibrarianID) VALUES
(3171, 3, 'OTHER', 'Loaned "To Kill a Mockingbird".', 3154),
(3172, 4, 'OTHER', 'Loaned "Pride and Prejudice".', 3154),
(3173, 5, 'OTHER', 'Loaned "The Catcher in the Rye".', 3154),
(3174, 6, 'OTHER', 'Loaned "Fahrenheit 451".', 3154),
(3175, 7, 'OTHER', 'Loaned "The Hobbit".', 3154);
INSERT INTO SubscriberStatusHistory (SubscriberID, Status, ChangeDate, Reason) VALUES
(3173, 'FROZEN', '2025-01-30', 'Member status changed to frozen');

SET SQL_SAFE_UPDATES =0;


INSERT INTO Reservations (SubscriberID, BookID, ReservationDate, Status) VALUES
(3171, 8, '2025-01-30', 'PENDING'),
(3172, 9, '2025-01-30', 'PENDING');
SET SQL_SAFE_UPDATES = 1;
