restful-gs-spark
================

RESTFul Service for GigaSpaces data using SparkJava

Steps to run this example
* Clone the project to your local folder
* Install GigaSpaces jars into your local repository using the installmavenrep.sh script
* Run "mvn package" inside the gs-spark project
* Run "mvn jetty:run" inside the gs-spark project

Using a RESTful client invoke following methods,

* POST on this URL, http://localhost:9080/books?author=GS&title=GigaSpacesForDummies for adding a new Book
* GET on this URL, http://localhost:9080/books for getting ids of all Books in the space
* GET on this URL, http://localhost:9080/books/<id1> for getting Details of a book with id "<id1>". Ids are generated randomly so replace it with a valid id.

A good RESTful client that I used for my testing is a Google Chrome plugin called Advanced REST client and can be downloaded from here, https://chrome.google.com/webstore/detail/advanced-rest-client/hgmloofddffdnphfgcellkdfbfbjeloo?hl=en-US.
