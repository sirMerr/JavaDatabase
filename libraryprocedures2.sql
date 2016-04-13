/**
*The library wants us to implement a number of “reader of the month”
* awards. For each genre, we want to print out (use a select statement)
* the person who has read the most books in that genre in the past month.
*/
DROP PROCEDURE TopReaders() IF EXISTS;

DELIMITER //

CREATE PROCEDURE TopReaders()
BEGIN
  DECLARE l_department_count INT;
  DECLARE l_department_id INT;
  DECLARE l_department_name VARCHAR(255);
  DECLARE no_more_departments INT;

  DECLARE dept_csr CURSOR FOR SELECT * FROM departments;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_departments=1;
  SET l_department_count=0;
  SET no_more_departments=0;
  OPEN dept_csr;
  dept_loop1:LOOP
    FETCH dept_csr INTO l_department_id,l_department_name;
    IF no_more_departments=1 THEN
      LEAVE dept_loop1;
      END IF;
      SET l_department_count=l_department_count+1;
      END LOOP;
      CLOSE dept_csr;
      SELECT CONCAT("The number of departments is: ", l_department_count);
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
