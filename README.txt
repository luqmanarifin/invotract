**** INSTALLATION ****


1. Add <Invotract Path>/lib into $PATH environment variable.

- Add invotract lib path into `/etc/environment`, then apply the change with
`source /etc/environment`

Make sure `tesseract` command is able to be executed from any directory.


2. Install SQLite for database

sudo apt install sqlite

Change directory to /var/www/html/phpliteadmin
If there is no existing directory, create one. 

Create database with name invotract.db ON THAT DIRECTORY:
sudo sqlite invotract.db

Make this directory and the database accessible for everyone
sudo chmod 777 invotract.db
sudo chmod 777 ../phpliteadmin


3. Install PHPLiteAdmin for easier DB management (Optional)


. Run the project
- Run the project using `java -jar invotract.jar`
