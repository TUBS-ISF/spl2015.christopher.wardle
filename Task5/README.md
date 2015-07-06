#**Read Me**

##**_Vocabulartrainer for SPL_**

Third party libraries used are:

- [OpenCSV] (http://opencsv.sourceforge.net/)
  Published under [Apache 2] (http://www.apache.org/licenses/LICENSE-2.0.txt)
- [MongoDB Java Driver 2.13.0-rc0] (http://mvnrepository.com/artifact/org.mongodb/mongo-java-driver/2.13.0-rc0)
  Published under [Apache 2] (http://www.apache.org/licenses/LICENSE-2.0.txt)
  
**Usage**
Simply run VocTrainer.java and the trainer will start.

For the DatabaseIntegration and UserManagement feature a MongoDB Server is required to run on port 27017 at the specified IP.
After installing MongoDB, run the mongod.exe with the --dbpath parameter pointing to the resources\MongoDB directory of this project (that's where the database is).
Currently there's only one user in the database and no function to add new ones. You can find the login data in the corresponding text file in the resources directory.