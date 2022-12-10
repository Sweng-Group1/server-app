# server-app


## Configuration
An `application.properties` file needs to be created in `src/main/resources/`. The file must consist of the following:

```
# URL of the MySQL database
spring.datasource.url=jdbc:mysql://localhost:3306/server-app-db

# Username and password of the MySQL user with permissions for the database
spring.datasource.username=username
spring.datasource.password=password

# SQL database initialisation, what Hibernate does on startup,
# below are the values you can set this to:
#	create - drops existing tables, then creates new tables (best for early development)
#	update - object models (in model namespace) is compared with existing schema, Hibernate
#			updates the schema to reflect the difference, but existing tables are never
#			deleted even if they're not required anymore
#	create-drop - similar to create, but Hibernate will additionally drop the database
#				 after operations are complete (best for unit testing)
#	validate - Hibernate only validates whether the tables and columns exist, it throws
#			  an exception if it doesn't
#	none - Database schema is not created, updated or deleted (best for production)
spring.jpa.hibernate.ddl-auto=create

# Prints the SQL statements executed in code onto the terminal
# Set to true when required during development/testing
# Use with format_sql to make the logged statements more readable.
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Choose the SQL dialect that matches the version of SQL database that is used for the
# project, MySQL8Dialect means that we need a MySQL v8 server.
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Set the database timezone, any date/time value will be formatted to the set timezone
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```