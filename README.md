# Vaultify

Vaultify is a password manager built with Jakarta Server Pages. It provides
user registration and login, encrypted credential storage, credential search
and filtering, weak-password detection using a wordlist, and a password
generator.

## Technology

- Java 17
- Maven with WAR packaging
- JSP 3.1 with JSTL
- Tomcat 10.1
- MySQL 8.0+
- Docker Compose

## Deployment with Docker Compose

### Preparation

Generate an application key once:

```sh
openssl rand -base64 32
```

Do not change `VAULTIFY_APP_KEY` after credentials have been stored. Encrypted
passwords cannot be decrypted with a different key.

### Start the application

```sh
cp .env.example .env
# edit .env, set VAULTIFY_APP_KEY, and optionally change APP_PORT
docker compose up -d --build
```

Check the service status and logs:

```sh
docker compose ps
docker compose logs -f app
```

The application is available at
`http://localhost:<APP_PORT>/vaultify/`, or at
<http://localhost:8080/vaultify/> when using the default port. The database
schema is created automatically when the application starts.

### Choose the application port

Set `APP_PORT` in `.env` before starting Docker Compose:

```env
APP_PORT=9090
```

Then recreate the application:

```sh
docker compose up -d --build
```

Open <http://localhost:9090/vaultify/>. `APP_PORT` changes the host port; the
Tomcat port inside the container remains `8080`.

Stop the application without deleting database data:

```sh
docker compose down
```

Do not use `docker compose down -v` unless all database data should be deleted.

## WAR Deployment on Tomcat

Make sure MySQL is running, then set the same environment variables for the
Tomcat process:

```sh
export VAULTIFY_APP_KEY="$(openssl rand -base64 32)"
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_DATABASE=vaultify
export DB_USERNAME=vaultify
export DB_PASSWORD=secret
```

Build and deploy the WAR:

```sh
mvn clean package
cp target/vaultify.war "$CATALINA_HOME/webapps/"
```

The `vaultify.war` filename produces this application URL:

```text
http://localhost:8080/vaultify/
```

Database tables are created automatically when Tomcat deploys the application.

To change the port for a standalone Tomcat deployment, edit the Tomcat
`conf/server.xml` file and change the HTTP connector port, for example:

```xml
<Connector port="9090" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

Restart Tomcat after changing `server.xml`, then use
`http://localhost:9090/vaultify/`.

## Importing a Wordlist

The wordlist must be a UTF-8 file containing one password per line. Run the
importer from the repository directory using the same database configuration
and `VAULTIFY_APP_KEY` as the application.

Example for Docker Compose:

```sh
export VAULTIFY_APP_KEY="$(grep '^VAULTIFY_APP_KEY=' .env | cut -d= -f2-)"
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_DATABASE=vaultify
export DB_USERNAME=vaultify
export DB_PASSWORD=secret

mvn -q exec:java \
  -Dexec.args="/absolute/path/to/wordlist.txt"
```

Example for a Tomcat deployment:

```sh
export VAULTIFY_APP_KEY='base64:...'
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_DATABASE=vaultify
export DB_USERNAME=vaultify
export DB_PASSWORD='secret'

mvn -q exec:java \
  -Dexec.args="/absolute/path/to/wordlist.txt"
```

Example wordlist:

```text
password
123456
qwerty
admin
```

Blank lines are counted but not inserted. Entries longer than 255 bytes are
skipped, while duplicates within the file and database are ignored. After the
import, wordlist status is recalculated for existing credentials.

Successful output looks like this:

```text
Import complete: 4 added, 0 duplicates, 0 blank lines, 0 skipped.
Credential recheck: 12 checked, 0 skipped.
```

## Configuration

| Variable | Default | Description |
| --- | --- | --- |
| `VAULTIFY_APP_KEY` | required | Key used to encrypt credentials |
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_DATABASE` | `vaultify` | Database name |
| `DB_USERNAME` | `vaultify` | Database user |
| `DB_PASSWORD` | `secret` | Database password |

## Verification

```sh
mvn test
```

After deployment, register a user, add a credential, verify that the password
can be revealed, and confirm that wordlist status appears after importing the
wordlist.
