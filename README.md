# Genowa - Code Generation Tool

A unified code generation tool for APG and Rating, replacing the legacy IBM Open Class C++ implementation.

## Features

- **Tables Management**: Browse and search all 62 legacy table definitions with full field details
- **Insurance Line Table Assignment**: Assign tables to insurance lines (AUTO, etc.)
- **Search**: Quick filtering across tables by name or description
- **Field Viewer**: View field definitions including type, length, and key indicators

## Requirements

- Java 21 (OpenJDK)
- Maven 3.x
- MySQL 8.x

## Setup

### Database Setup

1. Create database and user:
```sql
CREATE DATABASE genowa;
CREATE USER 'genowa'@'localhost' IDENTIFIED BY 'genowa123';
GRANT ALL PRIVILEGES ON genowa.* TO 'genowa'@'localhost';
FLUSH PRIVILEGES;
```

2. Create schema:
```sql
USE genowa;

CREATE TABLE gen_tables (
    table_id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    table_desc VARCHAR(255),
    table_type VARCHAR(50),
    active_yn CHAR(1) DEFAULT 'Y',
    created_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gen_fields (
    field_id INT AUTO_INCREMENT PRIMARY KEY,
    table_id INT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_type VARCHAR(50),
    field_length INT,
    field_decimal INT,
    seq_no INT,
    key_field_yn CHAR(1) DEFAULT 'N',
    active_yn CHAR(1) DEFAULT 'Y',
    FOREIGN KEY (table_id) REFERENCES gen_tables(table_id)
);
```

### Build and Run

```bash
# Compile
mvn compile

# Run
mvn javafx:run
```

### Login

Default credentials: `admin` / `admin`

## Project Structure

```
src/main/java/com/genowa/
├── model/
│   ├── GenField.java      # Field entity
│   └── GenTable.java      # Table entity
├── service/
│   └── DatabaseService.java  # Database connection singleton
└── ui/
    ├── GenowaApp.java         # Main application entry
    └── screens/
        ├── LoginScreen.java           # Login UI
        ├── MainScreen.java            # Main tabbed interface
        ├── TablesScreen.java          # Tables browser with fields
        └── InsLineTableAssignScreen.java  # Insurance line assignments
```

## Database Content

- **62 tables** imported from legacy WARP_* definitions
- **386 fields** with type and length information

## Code Style

Allman style (braces on their own lines)

## License

Proprietary - Arsenal Hill
