This file includes all steps needed to run the JavaFX applet.

1a. Download the most current JavaFX SDK from www.oracle.com
	- Search for javafx in search bar
	- Make sure download is compatible with platform (Ex: unix,windows,os) and build (Ex: x64, x32, El Capitan)

1b. If you are running the most recent Eclipse IDE (NEON) you will need to download the plugin e(fx)clipse
	- https://www.eclipse.org/efxclipse/install.html
	- You'll need at least JDK 8 installed in your system
	- Follow directions to install using update sites

2. Steps to import FoodStoreDB_App:
	a. Download the zip file containing the application.
	b. Open Eclipse IDE. 
	c. Follow the directions according to this website: 
		- http://agile.csc.ncsu.edu/SEMaterials/tutorials/import_export/
		* IMPORTANT: After selecting the radio button next to archive, 
			     browse to the zip file that you downloaded.

3. Steps to run the program:
	a. Navigate FoodStoreDB_App >> default package >> Main.java >> Main 
	b. Click Run 

4. Using The Database Viewer:
	a. Upon running a window will appear named "Database Viewer UI".
	b. Before beginning you must select a databse to view:
		i. Select the button named "Open Database", you will be prompted with a file explorer.
		ii. Navigate to your workspace, then select the "FoodStoreDB_App" folder.
		iii. Select the DB file in this folder called "FinalProjectDB", and click open.
	c. Verification of a successful will be displayed in the left window, where it should list all the tables
	   in the DB selected.
	d. The top left button called "Quick Queries" is a drop down menu of DB queries reserverd for the final project only.
	e. Selecting "SQL Terminal" will allow for custom SQL queries to be written for those who know SQL. 
	   * IMPORTANT: When writing queries it is important to always include a ";" at the end or else the
			query will not work, and will throw an error.
	f. Upon selecting the "Search Button", you will be prompted with a window called "DB Search Window".
		- Use the "Select Table" drop down button to select the table to be searched.
		- The "Select Attribute" button is currently NOT working yet.
		- Finally, press the "Search" button to view everything from the table selected.

