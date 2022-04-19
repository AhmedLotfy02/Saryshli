# Saryshli--


 
## Documentation about faced problems
<ol>
<li>Put jsoup library in General Libraries tab not Libraries only tab in intellij</li>
<li>Take care to create the class inside src file as outside it will cause missing the main entry</li>
<li>Can't start mongodb service: write these two commands
<ol><li>sudo chown -R mongodb:mongodb /var/lib/mongodb </li>
<li>sudo chown mongodb:mongodb /tmp/mongodb-27017.sock</li>
</ol>

</li>
</ol>

## Most Frequenctly Used Commands related to MongoDB:
- sudo systemctl start mongod: to start service
- mongosh : to start mongo shell
- mongo -u 'user' -p 'pass': to authenticate user.
- in mongoshell : `show collections` to show collections in database.
- in mongoshell : `use admin` to use admin database you can replace admin by any database name.

## Maven Dependenices
- You should put these dependencies inside your pom.xml.
```
 <dependencies>
  	<dependency>
  		<groupId>org.mongodb</groupId>
  		<artifactId>mongodb-driver-sync</artifactId>
  		<version>4.1.0-beta2</version>
 	 </dependency>
 	 <dependency>
 	 	<groupId>org.slf4j</groupId>
 	 	<artifactId>slf4j-api</artifactId>
 	 	<version>1.7.13</version>
 	 </dependency>
 	 
 	 <dependency>
 	 	<groupId>org.slf4j</groupId>
 	 	<artifactId>slf4j-log4j12</artifactId>
 	 	<version>1.7.13</version>
 	 </dependency>
  </dependencies>
```
