JUnitEE
=======

This tool provides a TestRunner and a servlet which will run JUnit test suites
in an app server.


Directory layout
================

- lib         pre-compiled libraries
- doc         documentation
- example     example appliation + test case
- src         Java sources for the JUnitEE Test Servlet and Ant Task


Contact
=======

junitee-user mailing list (subscribe at http://lists.sourceforge.net/lists/listinfo/junitee-user)
Jeff Schnitzer (jeff@infohazard.com)
Oliver Rossmueller (oliver@oross.net)


Change log
==========

Release 1.5

- improved user interface
- possibility to run single tests
- optional xml output of test results
- new JUnitEEWar Ant task

Release 1.4

- New option to run all tests found in a specified jar file
- Ant task for build integration
- TestServletBase is no longer abstract and should work as is
- Bug Fixes (Bug 583856, 583859)


Release 1.3

- Changes to output so that it shows correctly text using <, & and >
- Option to list all tests that have been run
