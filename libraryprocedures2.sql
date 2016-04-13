/**
*The library wants us to implement a number of “reader of the month”
* awards. For each genre, we want to print out (use a select statement)
* the person who has read the most books in that genre in the past month.
*/
DROP PROCEDURE TopReaders() IF EXISTS;

DELIMITER //

CREATE PROCEDURE TopReaders()
BEGIN
  DECLARE genre_id INT;
  DECLARE patron_id INT;
  DECLARE temp INT;
  DECLARE biggest INT;
  DECLARE biggest_patron INT;
  DECLARE patron_count INT;
  DECLARE genre_count INT;

  SELECT COUNT(patron_id) INTO patron_count FROM patron;
  SELECT COUNT(genre_id) INTO genre_count FROM genre;
  loop1:LOOP
    SET genre_id = 1;
    SET patron = 1;
    SET biggest = 0;
    SET biggest_patron = 1;

    loop2: LOOP
      SELECT COUNT(book) INTO temp FROM book_loan INNER JOIN book ON isbn = book
      WHERE genre=genre_id AND patron_id = patron;
      IF (biggest < temp)
        SET biggest = temp;
        SET biggest_patron = patron;
        END IF;
      IF patron = patron_count THEN
        SELECT CONCAT("The patron that loves genre ", genre_id, " is patron ", biggest_patron);
        LEAVE loop2;
      END IF;
      SET patron += 1;
      END LOOP;
    IF genre_id = genre_count THEN
      LEAVE loop1;
    END IF;
    SET genre_id += 1;
    END LOOP;
END //
DELIMITER ;
/**
* The library is starting a program where it introduces its patrons to
* other patrons with similar tastes. In order to do this, it needs a
* stored function that, for a given patron, finds the patron who has the
* most similar taste in books. The patron with the most similar taste in
* books is the patron who has taken out the largest number of books
* from the list of books taken out by the patron we are trying to match.
* Find the list of books that have been taken out by the patron identified
* by patron_id and identify the patron_pal_id by finding the patron who
* has the largest number of these books in their own list.
*/
DELIMITER //
