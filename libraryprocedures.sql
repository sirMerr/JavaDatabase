/**
* We want to write a function which computes the number
* of days a book is past it's due date. It should take
* as input an isbn. It should return an integer value,
* either 0 if the book is not overdue or the number of days
*  it is overdue otherwise.
*/
DROP FUNCTION DaysOverdue(isbn int) IF EXISTS;

DELIMITER $$

CREATE FUNCTION DaysOverdue(isbn int) RETURNS INT;
    DETERMINISTIC
  BEGIN
    DECLARE date_now DATE;
    DECLARE due DATE;
    DECLARE difference INT;

    SELECT due_date INTO due , CURDATE() INTO date_now FROM book_loan
    WHERE book_loan.isbn = isbn;

    SET difference = DATEDIFF(date_now,due);

    IF (difference < 0) THEN
      SET difference = 0;
    END IF;

    RETURN (difference);
  END $$

DELIMITER ;

/**
* This procedure should create a new entry in the book_loan table,
* with the due date set at two weeks from the current date, and the
* returned field set to 0 (false) since the book is just being loaned out.
*/
DROP PROCEDURE LoanBook(IN isbn int, IN patron_id int) IF EXISTS;

DELIMITER //

CREATE PROCEDURE LoanBook(IN isbn int, IN patron_id int)
BEGIN
  INSERT INTO book_loan (book, patron_id, due_date, returned) VALUES (isbn, DATE_ADD(now(),INTERVAL 2 WEEK), 0);
END //
DELIMITER ;

/**
* Procedure to renew a book. It should take as input an isbn, and update the
* due date to be two weeks from the current date. It should also assess fees,
* adding 5 cents per day overdue (if any) to the Patron’s fees field.
*/
DROP PROCEDURE RenewBook(IN isbn int) IF EXISTS;

DELIMITER //

CREATE PROCEDURE RenewBook(IN isbn int)
BEGIN
  IF EXIST(SELECT book FROM book_loan WHERE book = isbn) THEN
    BEGIN
      DECLARE patron_fees DOUBLE;
      DECLARE p_id INTEGER;

      SELECT patron_id INTO p_id FROM book_loan
        WHERE book = isbn;

      SELECT fees FROM patron INTO patron_fees
        WHERE patron_id = p_id;

        SET patron_fees += DaysOverDue() * 0.05;

        UPDATE book_loan
          SET due_date = DATE_ADD(now(),INTERVAL 2 WEEK)
        WHERE book = isbn;

        UPDATE patron
          SET fees = patron_fees
          WHERE patron_id = p_id;
      END
  ELSE
    BEGIN
      SIGNAL SQLSTATE '4500'
        SET MESSAGE_TEXT = "That book is not on loan";
    END;
  END IF;
END //

DELIMITER ;

/**
* Procedure to renew all of a patron’s book. It should take as input a
* patron_id, and for every book that patron has on loan, it should update the
* due date to be two weeks from the current date. It should also assess fees,
* adding 5 cents per day overdue (if any) to the Patron’s fees field.
*/
DROP PROCEDURE BallinProcedure(IN p_id int) IF EXISTS;

DELIMITER //

CREATE PROCEDURE BallinProcedure (IN p_id INT)

  BEGIN
    DECLARE isbn_num INTEGER DEFAULT 0;

    DECLARE radical_cursor CURSOR FOR
    SELECT isbn FROM book_loan WHERE patron_id = p_id;

    OPEN radical_cursor;

    cool_loop: LOOP
        FETCH radical_cursor INTO isbn_num;

        CALL RENEWBOOKS(isbn_num);

        IF no_more_rows THEN
            LEAVE cool_loop;
        END IF;
    END LOOP cool_loop;
    CLOSE radical_cursor;
  END //

DELIMITER ;

/**
* This procedure takes as input a patron_id and an amount of change being used
* to pay fees. It takes in an int value in cents as payment, removes that
* amount from the Patrons fees, and outputs the change leftover after the
* transaction.
*/
DROP PROCEDURE Payment(INOUT change int, IN p_id int) IF EXISTS;

DELIMITER //

CREATE PROCEDURE Payment(INOUT change int, IN p_id int)
BEGIN
  DECLARE patron_fees DOUBLE;

  SELECT fees INTO patron_fees FROM patron WHERE patron_id = p_id;

  SET patron_fees -= change/100.0;

  UPDATE patron
    SET fees = patron_fees
  WHERE patron_id = p_id;
END //

DELIMITER ;
