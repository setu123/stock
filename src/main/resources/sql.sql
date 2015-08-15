#Function to calculate close price
DELIMITER $$

DROP FUNCTION IF EXISTS `closeprice` $$
CREATE DEFINER=`root`@`localhost` FUNCTION `closeprice`(last_price float, close_price float) RETURNS decimal(10,1)
BEGIN
DECLARE result DECIMAL(10,1);

IF close_price>0 THEN SET result=close_price;
ELSE SET result=last_price;
END IF;

return result;
END $$

DELIMITER ;


#Function to calculate yesterday
DELIMITER $$

DROP FUNCTION IF EXISTS `stock`.`yesterday` $$
CREATE FUNCTION `stock`.`yesterday` () RETURNS DATE
BEGIN
DECLARE yesterday DATE;
SELECT MAX(date) INTO yesterday FROM bs_pressure WHERE date < (SELECT MAX(date) FROM bs_pressure);
RETURN yesterday;
END $$

DELIMITER ;


#Function to calculate day before yesterday
DELIMITER $$

DROP FUNCTION IF EXISTS `stock`.`daybeforeyesterday` $$
CREATE FUNCTION `stock`.`daybeforeyesterday` () RETURNS DATE
BEGIN
DECLARE dayBeforeYesterday DATE;
SELECT MAX(date) INTO dayBeforeYesterday FROM bs_pressure
WHERE date < (
               SELECT MAX(date) FROM bs_pressure WHERE date < (SELECT MAX(date) FROM bs_pressure)
             );
RETURN dayBeforeYesterday;
END $$

DELIMITER ;


#Function to calculate hammer
DELIMITER $$

DROP FUNCTION IF EXISTS `hammervalue` $$
CREATE DEFINER=`root`@`localhost` FUNCTION `hammervalue`(DAY_HIGH DECIMAL(5,1), OPEN_PRICE DECIMAL(5,1), CLOSE_PRICE DECIMAL(5,1), DAY_LOW DECIMAL(5,1)) RETURNS decimal(5,1)
BEGIN

DECLARE DIFFERENCE DECIMAL(5,1);
DECLARE LARGEST DECIMAL(5,1);
DECLARE SMALLEST DECIMAL(5,1);
DECLARE HAMMER DECIMAL(5,1);

SET DIFFERENCE = ABS(OPEN_PRICE-CLOSE_PRICE);
SET LARGEST = (OPEN_PRICE+CLOSE_PRICE+DIFFERENCE)/2;
SET SMALLEST = (OPEN_PRICE+CLOSE_PRICE-DIFFERENCE)/2;
SET HAMMER = ((SMALLEST-DAY_LOW)-(DAY_HIGH-LARGEST))/DIFFERENCE;
RETURN HAMMER;

END $$

DELIMITER ;