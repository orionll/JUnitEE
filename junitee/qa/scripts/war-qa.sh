#! /bin/sh
jar tf build/junitee-example-qa.war | sort | diff - "../qa/reference/war-$1"
