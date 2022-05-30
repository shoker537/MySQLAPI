# MySQLAPI
Compiled with Java 16

## Importing

* Add MySQLAPI to depends of your plugin**
* MySQLAPI must be installed as a plugin

```groovy
repositories {
    maven {
        name = 'shoker.su'
        url = 'https://nexus.shoker.su/repository/maven-releases/'
    }
}

dependencies {
    compileOnly 'ru.shk:MySQLAPI:2.0.1'
}
```
All versions can be found [here](https://nexus.shoker.su/#browse/browse:maven-releases:ru%2Fshk%2FMySQLAPI)

## Usage
Obtaining MySQL object
```java
// Create a MySQL connection to a default MySQL-server defined in config and enter the database-name database
MySQL sql = new MySQL("database-name");

// Create a MySQL connection to a server-name MySQL-server defined in config and enter the database-name database
MySQL sql = new MySQL("server-name", "database-name");

// Create a MySQL connection with custom connection arguments
MySQL sql = new MySQL("host", "user", "pass", "database");
```
Exeuting queries and updates
```java
// Execute update async and ignoring exceptions (an exception still throws to log)
sql.Update("UPDATE table SET a=1");
// Execute update sync throwing exceptions
sql.UpdateExpected("UPDATE table SET a=1");
// Execute update sync ignoring exceptions (an exception still throws to log)
sql.UpdateSync("UPDATE table SET a=1");

// Request a ResultSet
ResultSet rs = sql.Query("SELECT * FROM table");

// Create a PreparedStatement
PreparedStatment ps = sql.getStatement("UPDATE table SET a=?");

// Create a statement which returns a ResultSet of generated keys
PreparedStatement ps = sql.getStatementWithGeneratedKeys("INSERT INTO table a=1;")
ps.execute();
ResultSet rs = ps.getGeneratedKeys();

```
Using builders
```java
ResultSet rs = sql.Query().SELECT("what").FROM("table").WHERE("a=1").WHERE("b=2").WHERE("c=3 AND d=4").LIMIT(1).execute();
sql.Update().TABLE("table").SET("a=1", "b=2", "c=3").WHERE("d=4").execute();
// Also available: .executeSync() and .executeExpected() fpr UpdateBuilder
```