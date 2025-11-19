# MovieJournal

A simple Java-based movie journal application that stores user accounts and movie reviews. Each user can create, search, sort, update and delete their own reviews. The project includes a small GUI, a JDBC-backed persistence layer, and comprehensive unit tests.

---

## How to run the project


1. Run project at Main.java

2. Ensure you MySQL downloaded and create a .env file:

```powershell
DB_URL=jdbc:mysql://localhost:3306/moviejournal?useSSL=false&serverTimezone=UTC
DB_USER=root
DB_PASSWORD=mypassword
```

Skip step 3 if not facing connection MySQL issues

3. If facing issues with pom.xml not connecting with MySQL, follow step 3 and the steps after:

Download the MySQL connector here based on your operating system:
https://dev.mysql.com/downloads/connector/j/

Extract the folder and find this .jar file for example:

```powershell
mysql-connector-j-8.0.31.jar
```

4. Open the project using IntelliJ IDEA and open "Project Structure", click "Modules", click "+", click "dependencies" and add this jar:
```powershell
mysql-connector-j-8.0.31.jar
```

5. Click "Apply" and "Ok" to save.
---

## Project Structure

- `src/main/java` — application source
	- `com.cpp.moviejournal.model` — data models (`User`, `MovieReview`)
	- `com.cpp.moviejournal.manager` — business logic and persistence (`UserManager`, `MovieReviewManager`)
	- `com.cpp.moviejournal.gui` — Swing GUI components (e.g., `UserProfilePanel`)
	- `com.cpp.moviejournal.util` — utilities (DB connection, password hashing)

- `src/test/java` — unit tests for managers and models

---

## Database

The project uses a JDBC `DatabaseConnection` utility. By default it may create and use an embedded or configured database. Check `src/main/java/com/cpp/moviejournal/util/DatabaseConnection.java` to configure the JDBC URL, username and password.

Schema highlights (created automatically by managers when needed):

`users` table — stores user accounts

`movie_reviews` table — stores reviews, now with fields `id`, `user_id`, `title`, `director`, `genre`, `rating`, `review`, `date_watched`, `created_at`, `updated_at`.

---


