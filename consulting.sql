DROP TABLE IF EXISTS c_employee;
DROP TABLE IF EXISTS c_customer;
DROP TABLE IF EXISTS c_invoice;

CREATE TABLE c_employee (
	employee_id INT PRIMARY KEY AUTO_INCREMENT,
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	hire_date DATE NOT NULL,
	manager_id INT NULL
	);


CREATE TABLE c_customer (
	customer_id INT PRIMARY KEY AUTO_INCREMENT,
	account_manager INT NOT NULL,
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL
	);

CREATE TABLE c_invoice (
	invoice_id INT PRIMARY KEY AUTO_INCREMENT,
	customer_id INT NOT NULL,
	invoice_amount FLOAT NOT NULL,
		--Added fields--
	total_invoice_amount FLOAT NOT NULL DEFAULT 0,
	account_manager_id INT NOT NULL DEFAULT 0
	);

--Added table--
CREATE TABLE c_logs (
	log_id INT PRIMARY KEY AUTO_INCREMENT
	current_date DATE NOT NULL,
	invoice FLOAT NOT NULL,
	customer_name VARCHAR(255) NOT NULL
	);

ALTER TABLE c_employee ADD CONSTRAINT managers FOREIGN KEY (manager_id) REFERENCES c_employee (employee_id);
ALTER TABLE c_customer ADD CONSTRAINT account_manager FOREIGN KEY (account_manager) REFERENCES c_employee (employee_id);
ALTER TABLE c_invoice ADD CONSTRAINT invoice_cid FOREIGN KEY (customer_id) REFERENCES c_customer (customer_id);

--Triggers for lab6--

/**
* A trigger that performs validation in insertion and updates to the
* employee table such that we cannot have employees whose manager field
* refers to their employee_id.
*/
DELIMITER //
CREATE TRIGGER no_self_managed_employees BEFORE INSERT
ON c_employee
FOR EACH ROW
BEGIN
	IF NEW.employee_id = NEW.manager_id THEN
		SIGNAL SQLSTATE '4500'
			SET MESSAGE_TEXT = 'Cannot be your own manager';
	END IF;
END //

/**
* A trigger which automatically sets the account_manager_id field to be the
* account_manager of the customer the invoice is created for.
*/
CREATE TRIGGER denormalizing_for_joins BEFORE INSERT
ON c_invoice
BEGIN
	DECLARE coolest_employee_id INT;

	SELECT account_manager INTO coolest_employee_id FROM c_customer
		INNER JOIN c_invoice ON c_invoice.customer_id = c_customer.customer_id
		WHERE c_customer.customer_id = NEW.customer_id;

	SET NEW.account_manager_id = coolest_employee_id;
END //

/**
* A trigger which automatically updates total_invoice_amount when an
* invoice is created.
*/
CREATE TRIGGER best_employee BEFORE UPDATE
ON c_employee
BEGIN
	INSERT into employees(total_invoice_amount) VALUES (OLD.total_invoice_amount + NEW.total_invoice_amount);
END //

/**
* A trigger which will add a new log every time an invoice is created.
*/
CREATE TRIGGER logging_invoices AFTER INSERT
ON c_invoice
BEGIN
	DECLARE customer VARCHAR(255);

	--Get full name--
	SELECT CONCAT(firstname, ' ', lastname) INTO customer FROM c_customer
		INNER JOIN c_invoice ON c_invoice.customer_id = c_customer.customer_id;
		WHERE c_customer.customer_id = NEW.c_invoice.customer_id;

	INSERT INTO c_logs(current_date, invoice,customer_name) VALUES(CURDATE(),NEW.invoice_amount, customer);
END //

DELIMITER ;
--End of triggers--

INSERT INTO c_employee(employee_id, firstname,lastname,hire_date,manager_id) VALUES (1,'Tim','McTimmer','2015-01-01',NULL);
INSERT INTO c_employee(employee_id, firstname,lastname,hire_date,manager_id) VALUES (2,'Johan','Donaldson','2016-01-01',1);
INSERT INTO c_employee(employee_id, firstname,lastname,hire_date,manager_id) VALUES (3,'Amanda','Dooblydoob','2016-02-02',1);
INSERT INTO c_employee(employee_id, firstname,lastname,hire_date,manager_id) VALUES (4,'Jessica','Stark','2017-02-02',2);

INSERT INTO c_customer(customer_id,account_manager,firstname,lastname) VALUES (1,2,'Shane','Thelps');
INSERT INTO c_customer(customer_id,account_manager,firstname,lastname) VALUES (2,2,'Katniss','Sobey');
INSERT INTO c_customer(customer_id,account_manager,firstname,lastname) VALUES (3,3,'Orbek','Wallbert');

INSERT INTO c_invoice(invoice_id,customer_id,invoice_amount) VALUES (1,1,10.00);
INSERT INTO c_invoice(invoice_id,customer_id,invoice_amount) VALUES (2,1,20.00);
INSERT INTO c_invoice(invoice_id,customer_id,invoice_amount) VALUES (3,2,32.00);
INSERT INTO c_invoice(invoice_id,customer_id,invoice_amount) VALUES (4,3,18.00);
