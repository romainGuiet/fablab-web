fablab-web [![Build Status](http://collaud2.collaud.me:8080/buildStatus/icon?job=fablab-web)](http://collaud2.collaud.me:8080/job/fablab-web/)
==========

This web application allows you to easily manage a Fablab (or any makerspace). You can manage :
* Users (with membership type)
* Machines (with machine type)
* Reservation
* Calendar (display a google calendar in the reservation module)
* Price (linked to membership type and machine type)
* Payment (when member give you money)
* Usage of machines (when member pay for your services)
* Subscription (price depend on membership type and subscription duration is editable)
* Audit of all action done on the platform
* Check of subsystems and send notification if they are down (or up again)
 
Demo
------------

You can try a demo version at this address : [collaud2.collaud.me](http://collaud2.collaud.me/). The server is a bit slow, sorry for that. Just click on login without any credential and you will be loged as an admin. Don't mind to mess up with data. Just tell me if you broke anything (for example if you remove the admin user...).

The default users are (password is _fablab_ for each of them) :

 * gaetan.collaud
 * animator1
 * member1
 * extern1

Requirement
------------

To run a build :

 * Java JRE 7
 * Glassfish 4
 * MySQL (or other SQL server)

To contribue (in addtion) : 

* Java JDK 7
* Maven 3

Libraries used :

* Java EE (EJB and cie)
* Log4j (logging)
* PrimeFaces (frontend)
* JPA EclipseLink (bdd)
* Jquery and sub-libraries
* Joday (time utils)
* Apache commons (general utils)

Configure, compile and install
------------------------

First you need to create the database with the sql schema provided in config/create.sql

Then you need to configure glassfish. Read the file config/INSTALL.txt to see how to properly configure glassfish.


Finaly you can run `mvn clean install`. The deploy the .war (under target/) in glassfish.
