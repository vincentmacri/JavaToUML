# JavaToUML
Program to convert Java code to UML class diagrams.

Uses the [JavaParser](https://github.com/javaparser/javaparser) library and [PlantUML](http://plantuml.com/).

# Using
Either run this program with all of the .java files to process as arguments, or place the .jar in a folder with all of the .java files to process and run it with no arguments.

Either way, the program will output UMLOutput.txt in the folder it is running in.

The format is PlantUML. The easiest way to view it is with https://www.planttext.com/

# Future Plans
Currently, it only processes private, protected, and public fields, methods, and constructors. It was built to be easy to extend, but I plan to focus on finding other info such as relationships first.
