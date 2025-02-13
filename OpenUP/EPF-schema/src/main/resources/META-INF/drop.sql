DROP SCHEMA IF EXISTS OpenUP;
DROP SCHEMA IF EXISTS EPF_Tasks;
DROP SCHEMA IF EXISTS EPF_Work_Products;
DROP SCHEMA IF EXISTS EPF_Roles;
DROP SCHEMA IF EXISTS EPF_Delivery_Processes;
DROP SCHEMA IF EXISTS EPF_Net;
DROP SCHEMA IF EXISTS EPF_Security;

CREATE SCHEMA IF NOT EXISTS EPF_Delivery_Processes;
CREATE SCHEMA IF NOT EXISTS EPF_Roles;
CREATE SCHEMA IF NOT EXISTS EPF_Tasks;
CREATE SCHEMA IF NOT EXISTS EPF_Work_Products;
CREATE SCHEMA IF NOT EXISTS OpenUP;
CREATE SCHEMA IF NOT EXISTS EPF_Net;
CREATE SCHEMA IF NOT EXISTS EPF_Security;

/*Basic Roles BEGIN*/
DROP USER IF EXISTS 'analyst1'@'localhost';
DROP USER IF EXISTS 'any_role1'@'localhost';
DROP USER IF EXISTS 'architect1'@'localhost';
DROP USER IF EXISTS 'developer1'@'localhost';
DROP USER IF EXISTS 'project_manager1'@'localhost';
DROP USER IF EXISTS 'stakeholder1'@'localhost';
DROP USER IF EXISTS 'tester1'@'localhost';
/*Basic Roles END*/

/*Deployment BEGIN*/
DROP USER IF EXISTS 'course_developer1'@'localhost';
DROP USER IF EXISTS 'deployment_engineer1'@'localhost';
DROP USER IF EXISTS 'deployment_manager1'@'localhost';
DROP USER IF EXISTS 'product_owner1'@'localhost';
DROP USER IF EXISTS 'technical_writer1'@'localhost';
DROP USER IF EXISTS 'trainer1'@'localhost';
/*Deployment END*/

/*Environment BEGIN*/
DROP USER IF EXISTS 'process_engineer1'@'localhost';
DROP USER IF EXISTS 'tool_specialist1'@'localhost';
/*Environment END*/