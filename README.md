# File Repository
This project was an exercise used to explore [Spring Boot](http://projects.spring.io/spring-boot/) and [AngularJS](https://angularjs.org/).  Files may be uploaded to the repository with Title, Description and Creation Date meta data.  A list of files in the repository may be viewed, individual files may be downloaded and files and their metadata may be modified.  

To run:
* Using Maven:
> > mvn spring-boot:run
* or:
> > mvn clean package
> > java -jar target/file-repository-1.0-SNAPSHOT.jar

## Design considerations
* In memory persistance was used.  Repository will be wiped out on restart.
* Implementation does not address all security converns.  For instance, files should not be directly uploaded to the web application file system and then served, especially without authentication or virus scanning.
* A UUID is used for the unique idntifier of the file so that files can be updated without the filename matching (hello_v1.txt, hello_v2.txt).
* The URI to download the file resource should be added to the file descriptor.
* File listing should implement paging for a scalable user experience.
* Client-side testing should be added using a framework such as Jasmine.

Reference projects:
* https://spring.io/guides/gs/uploading-files/
* https://github.com/spring-guides/tut-spring-security-and-angular-js/tree/master/basic