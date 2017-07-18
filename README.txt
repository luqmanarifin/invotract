**** INSTALLATION ****


1. Add <Invotract Path>/lib into $PATH environment variable.

- Add invotract lib path into `/etc/environment`, then apply the change with
`source /etc/environment`

Make sure `tesseract` command is able to be executed from any directory.


2. Install SQLite for database

sudo apt install sqlite3

Change directory to /var/www/html/phpliteadmin. 
If there is no existing directory, create one. 

Create database with name invotract.db ON THAT DIRECTORY:
sudo sqlite3 invotract.db

On sqlite3 command-line, type this
.databases

And then quit from sqlite
.quit

Make this directory and the database accessible for everyone
sudo chmod 777 invotract.db
sudo chmod 777 ../phpliteadmin


3. [OPTIONAL] Install PHPLiteAdmin for easier DB management

sudo apt-get install php7.0-sqlite3

Download newest PHPLiteAdmin from here
https://bitbucket.org/phpliteadmin/public/downloads/

Put the phpliteadmin.php file into /var/www/html/phpmyadmin directory.

Install Apache2 server
sudo apt-get install apache2

Install PHP 7.0
sudo apt install php7.0 libapache2-mod-php

Restart Apache server
sudo service apache2 restart

Check if it is working at:
localhost/phpliteadmin/phpliteadmin.php


4. Run the project
- Run the project using `java -jar invotract.jar`
