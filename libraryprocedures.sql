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
    DECLARE due_date DATE;
    DECLARE difference INT;

    SELECT book_loan.due_date INTO due_date, CURDATE() INTO date_now FROM book_loan
    WHERE book_loan.isbn = isbn;

    SET difference = DATEDIFF(date_now,due_date);

    IF (difference < 0) THEN
      SET difference = 0;
    END IF;

    RETURN (difference);
  END

/**
* This procedure should create a new entry in the book_loan table,
* with the due date set at two weeks from the current date, and the
* returned field set to 0 (false) since the book is just being loaned out.
*/
DROP PROCEDURE LoanBook(isbn int) IF EXISTS;

DELIMITER //

CREATE PROCEDURE LoanBook(in isbn int, in patron_id int)
BEGIN
  DECLARE date_now DATE;
