# statUploader
This utility parses through excel files (saved as csv) and uploads each map found to the new @cod_stats database. 
It will be used to transfer "old" stats without manually re-entering data. 

This utility has two main components. 
The first component is the statUploader java application, which parses the data and sends it to the PHP component.
The PHP component is made up of PHP files which are responsible for entering the data in a mysql database on the server. This component is what will change to reflect schema changes. 


