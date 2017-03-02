package ca.vincemacri.javauml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;

public class Converter {

	/**
	 * The main method.
	 * 
	 * @param args List of all .java files to process. Processes all .java files in current directory if no arguments are given.
	 * @throws IOException If a file passed as an argument is invalid.
	 */
	public static void main(String[] args) throws IOException {
		ParsedClass[] parsedClasses;

		if (args.length > 0) {
			parsedClasses = new ParsedClass[args.length];
			for (int i = 0; i < args.length; i++) {
				parsedClasses[i] = new ParsedClass(JavaParser.parse(new File(args[i])));
			}
		} else {
			File[] files = new File(".").listFiles();
			List<File> javaFiles = new ArrayList<File>();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().length() > 5 && files[i].getName().substring(files[i].getName().length() - 5).equalsIgnoreCase(".java") && !files[i].isDirectory()) {
					javaFiles.add(files[i]);
				}
			}
			parsedClasses = new ParsedClass[javaFiles.size()];
			for (int i = 0; i < javaFiles.size(); i++) {
				parsedClasses[i] = new ParsedClass(JavaParser.parse(javaFiles.get(i)));
			}
		}

		/*
		 * PlantUML symbol reference:
		 *	-private
		 *	#protected
		 *	~package private
		 *	+public
		 */
		
		FileWriter fWrite = new FileWriter(new File("UMLOutput.txt"));
		PrintWriter writer = new PrintWriter(fWrite);

		writer.println("@startuml");
		writer.println("skinparam classAttributeIconSize 0");

		for (ParsedClass parsedClass : parsedClasses) {
			writer.println("class " + parsedClass.getName() + " {");

			for (String privateNonStaticField : parsedClass.getFields(Modifier.PRIVATE, Modifier.STATIC)) {
				writer.println("\t-" + privateNonStaticField);
			}

			for (String protectedNonStaticField : parsedClass.getFields(Modifier.PROTECTED, Modifier.STATIC)) {
				writer.println("\t#" + protectedNonStaticField);
			}

			for (String publicNonStaticField : parsedClass.getFields(Modifier.PUBLIC, Modifier.STATIC)) {
				writer.println("\t+" + publicNonStaticField);
			}

			for (String privateStaticField : parsedClass.getFields(new Modifier[]{Modifier.PRIVATE, Modifier.STATIC})) {
				writer.println("\t-{static} " + privateStaticField);
			}

			for (String protectedStaticField : parsedClass.getFields(new Modifier[]{Modifier.PROTECTED, Modifier.STATIC})) {
				writer.println("\t#{static} " + protectedStaticField);
			}

			for (String publicStaticField : parsedClass.getFields(new Modifier[]{Modifier.PUBLIC, Modifier.STATIC})) {
				writer.println("\t+{static} " + publicStaticField);
			}

			for (String privateConstructor : parsedClass.getConstructors(new Modifier[]{Modifier.PRIVATE})) {
				writer.println("\t-" + privateConstructor);
			}

			for (String protectedConstructor : parsedClass.getConstructors(new Modifier[]{Modifier.PROTECTED})) {
				writer.println("\t#" + protectedConstructor);
			}

			for (String publicConstructor : parsedClass.getConstructors(new Modifier[]{Modifier.PUBLIC})) {
				writer.println("\t+" + publicConstructor);
			}
			for (String privateNonStaticMethod : parsedClass.getMethods(Modifier.PRIVATE, Modifier.STATIC)) {
				writer.println("\t-" + privateNonStaticMethod);
			}

			for (String protectedNonStaticMethod : parsedClass.getMethods(Modifier.PROTECTED, Modifier.STATIC)) {
				writer.println("\t#" + protectedNonStaticMethod);
			}

			for (String publicNonStaticMethod : parsedClass.getMethods(Modifier.PUBLIC, Modifier.STATIC)) {
				writer.println("\t+" + publicNonStaticMethod);
			}

			for (String privateStaticMethod : parsedClass.getMethods(new Modifier[]{Modifier.PRIVATE, Modifier.STATIC})) {
				writer.println("\t-{static} " + privateStaticMethod);
			}

			for (String protectedStaticMethod : parsedClass.getMethods(new Modifier[]{Modifier.PROTECTED, Modifier.STATIC})) {
				writer.println("\t#{static} " + protectedStaticMethod);
			}

			for (String publicStaticMethod : parsedClass.getMethods(new Modifier[]{Modifier.PUBLIC, Modifier.STATIC})) {
				writer.println("\t+{static} " + publicStaticMethod);
			}

			writer.println("}");
		}
		writer.println("@enduml");
		
		writer.close();
		fWrite.close();

	}

}
