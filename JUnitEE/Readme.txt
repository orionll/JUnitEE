JUnitEE
=======

This tool provides a TestRunner and a servlet which will run JUnit test suites
in an app server.


Formal disclaimer
=================
 
If this tool explodes, destroying your machine room and everything around it in
a 150-foot radius, including that pyramid of empty Dr. Pepper cans around your 
monitor, well, don't sue us. Nobody did promise that it would work. In fact,
we are running it against our own systems, so it is pretty likely that it won't.
But you have source code, so you can fix it yourself (after you clear all the 
soda cans off your keyboard and chair). Just send us a copy of the changes and 
it will save someone else the trouble.


Directory layout
================

- lib         pre-compiled libraries
- doc         documentation
- example     example appliation + test case
- src         Java sources for the JUnitEE Test Servlet
- anttask     Java sources for the JUnitEE Ant Task

Contact
=======

junitee-user mailing list (subscribe at http://lists.sourceforge.net/lists/listinfo/junitee-user)
Jeff Schnitzer (jeff@infohazard.com)
Oliver Rossmueller (oliver@oross.net)


Change log
==========

Release 1.5

- Removed the search parameter
- added output parameter
- added JUnitEEWar Ant task
- refurbished TestRunner user interface
- TestServletBase deprecated
- moved classes to org.junitee.*

Release 1.4

- New option to run all tests found in a specified jar file
- Ant task for build integration
- TestServletBase is no longer abstract and should work as is
- Bug Fixes (Bug 583856, 583859)


Release 1.3

- Changes to output so that it shows correctly text using <, & and >
- Option to list all tests that have been run
