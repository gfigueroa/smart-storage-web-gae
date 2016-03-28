Smart Storage Cloud Application
===============================================================================

## Description

Smart Storage System
The Smart Storage System is a cloud application for managing smart storage equipment (with sensors) from customers.
Storage equipment includes equipment models, equipment types and sensor types. The system can keep track of a storage device's
sensors and stored items. The system also handles alarms and marketing information.
The system is developed for Google App Engine using Java.
It has a basic web interface and a web service API.

Can be accessed at: [link](http://smasrv-iot.appspot.com/)

### Related Projects

## Acknowledgements

Engineers and designers at SMASRV

## Directory Structure

    ArtMeGo_API_Tornado/
        src/
			datastore/
			exceptions/
			META-INF/
			servlets/
			util/
			webservices/
		war/
			admin/
			css/
			customer/
			footer/
			header/
			images/
			javascript/
			menu/
			testing/
			WEB-INF/

### src

All Java packages and source code goes here.

#### datastore

Datastore tables and table managers (CRUD operations).

#### exceptions

Specific exception classes.

#### META-INF

JDO configuration files.

#### servlets

All HTTP servlet implementations.

#### util

Various utilities.

#### webservices

Web service classes and routes file.

### war

Web elements.

## Authors

* [Smart Personalized Service Technology Inc.](http://www.smasrv.com)
 * Gerardo Figueroa, gfigueroa@smasrv.com