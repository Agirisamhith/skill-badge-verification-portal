# Verified Digital Skill-Badge Portal

A web-based portal for verifying digital skill badges and certificates from online learning platforms. The project demonstrates the use of CGI and Java Servlets for handling verification requests, along with MySQL for storing and managing credential information.

## Project Overview

Digital certificates and skill badges are increasingly used to demonstrate learning and professional skills. However, manually checking whether a certificate is genuine can be time-consuming.

The Verified Digital Skill-Badge Portal provides a simple platform where users can submit credential details and verify their validity. The system checks the provided information against stored credential records and displays the verification result.

The project also demonstrates the difference between CGI-based request handling and Java Servlet-based request handling as part of the Web Technologies PBL.

## Features

- Digital certificate and skill-badge verification
- Verification using credential or badge details
- Support for credentials from platforms such as Coursera and IBM SkillsBuild
- Database-based credential management
- Java Servlet-based request processing
- CGI-based verification implementation
- User-friendly web interface
- Verification result with clear status
- MySQL database integration
- External provider information management

## Technologies Used

- HTML5
- CSS3
- JavaScript
- Java
- Java Servlets
- CGI
- Python
- MySQL
- Maven
- Gson
- Apache Tomcat

## System Architecture

The application follows a simple web application architecture:

User
  |
  v
Web Interface
  |
  v
Verification Request
  |
  +-------------------+
  |                   |
  v                   v
CGI Implementation   Servlet Implementation
  |                   |
  +---------+---------+
            |
            v
       MySQL Database
            |
            v
     Verification Result

## CGI Implementation

The CGI component is implemented using Python.

The CGI script receives verification requests and processes the submitted credential information. It communicates with the database and returns the appropriate verification response.

The CGI implementation demonstrates how a web server can execute an external program to process HTTP requests.

Main CGI file:

`cgi-bin/verify.py`

## Servlet Implementation

The main application logic is implemented using Java Servlets.

Servlets provide a more efficient and structured approach for handling multiple web requests within a Java web application.

The project includes servlets such as:

- VerifyBadgeServlet
- IssueBadgeServlet
- WalletServlet

These components handle badge verification, badge-related operations, and wallet functionality.

## Certificate Verification

The portal is designed to verify digital credentials by checking the submitted information against records stored in the database.

A typical verification process is:

1. User enters the credential or badge information.
2. The portal receives the verification request.
3. The system validates the submitted details.
4. The database is queried for a matching credential.
5. The system checks the associated provider and credential information.
6. The verification result is displayed to the user.

For demonstration purposes, credentials from platforms such as Coursera and IBM SkillsBuild can be represented through provider records and verified against the corresponding information stored in the system.

## CGI vs Servlet

The project demonstrates both CGI and Java Servlet approaches.

| Feature | CGI | Servlet |
|---|---|---|
| Implementation | Python | Java |
| Request Processing | External CGI process | Servlet container |
| Performance | Lower for repeated requests | Better for repeated requests |
| Resource Usage | Creates a separate process | Uses managed servlet instances |
| Integration | External program | Java web application |
| Project Usage | Verification demonstration | Main web application |

The comparison helps demonstrate why Servlets are generally more suitable for larger Java-based web applications.

## Database

MySQL is used to store the information required by the application.

The database contains information related to:

- Students
- Skill badges
- Modules
- External providers

The database schema is provided in:

`schema.sql`

## Project Structure

```text
skill-badge-verification-portal/
│
├── .freebuff/
│
├── cgi-bin/
│   └── verify.py
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── skillbadge/
│       │           └── servlet/
│       │               ├── IssueBadgeServlet.java
│       │               ├── VerifyBadgeServlet.java
│       │               └── WalletServlet.java
│       │
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml
│           │
│           └── index.html
│
├── pom.xml
├── schema.sql
├── gson-2.10.1.jar
└── .gitignore
