package ca.vincemacri.javauml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Converter {

	public static final String OPT_HELP = "h";
	public static final String OPT_OUTPUT = "o";
	public static final String OPT_DIRECTORY = "d";
	public static final String OPT_RECURSIVE = "r";
	public static final String OPT_FULLY_QUALIFIED_NAME = "fqn";
	public static final String OPT_OMIT_CONSTRUCTORS = "oc";
	public static final String OPT_OMIT_METHODS = "om";
	public static final String OPT_OMIT_MODIFIERS = "omod";
	public static final String OPT_SHOW_RELATIONS = "sr";

	/**
	 * The main method.
	 * 
	 * @param args List of all .java files to process. Processes all .java files in
	 *             current directory if no arguments are given.
	 * @throws IOException If a file passed as an argument is invalid.
	 */
	public static void main(String[] args) throws IOException {
		final Options opt = new Options();

		opt.addOption(OPT_HELP, "help", false, "Print help for this application");
		opt.addOption(OPT_OUTPUT, "output", true, "Where to save output");
		opt.addOption(OPT_DIRECTORY, "directory", true, "Directory to process");
		opt.addOption(OPT_RECURSIVE, "recursive", false, "Process directory recursively");
		opt.addOption(OPT_FULLY_QUALIFIED_NAME, "fully-qualified-name", false, "Use fully qualified class name");
		opt.addOption(OPT_OMIT_CONSTRUCTORS, "omit-constructors", false, "Omit constructors");
		opt.addOption(OPT_OMIT_METHODS, "omit-methods", false, "Omit methods");
		opt.addOption(OPT_OMIT_MODIFIERS, "omit-modifiers", false, "Omit modifiers");
		opt.addOption(OPT_SHOW_RELATIONS, "relations", false, "Show relations");

		final BasicParser parser = new BasicParser();
		final HelpFormatter hf = new HelpFormatter();
		CommandLine cl = null;
		try {
			cl = parser.parse(opt, args);
		} catch (ParseException e) {
			hf.printHelp("JavaToUML", opt, true);
			e.printStackTrace();
			System.exit(1);
		}

		if (cl.hasOption(OPT_HELP)) {
			hf.printHelp("JavaToUML", opt, true);
			System.exit(0);
		}

		final JavaParser javaParser = new JavaParser();
		ParsedClass[] parsedClasses;

		String[] restArgs = cl.getArgs();
		if (restArgs.length > 0) {
			parsedClasses = new ParsedClass[restArgs.length];
			for (int i = 0; i < restArgs.length; i++) {
				parsedClasses[i] = new ParsedClass(javaParser.parse(new File(restArgs[i])).getResult().get(),
						cl.hasOption(OPT_FULLY_QUALIFIED_NAME));
			}
		} else {
			List<File> javaFiles = listFiles(cl.getOptionValue(OPT_DIRECTORY, "."), cl.hasOption(OPT_RECURSIVE));
			parsedClasses = new ParsedClass[javaFiles.size()];
			for (int i = 0; i < javaFiles.size(); i++) {
				parsedClasses[i] = new ParsedClass(javaParser.parse(javaFiles.get(i)).getResult().get(),
						cl.hasOption(OPT_FULLY_QUALIFIED_NAME));
			}
		}

		/*
		 * PlantUML symbol reference: -private #protected ~package private +public
		 */

		FileWriter fWrite = new FileWriter(new File(cl.getOptionValue(OPT_OUTPUT, "UMLOutput.txt")));
		PrintWriter writer = new PrintWriter(fWrite);

		writer.println("@startuml");
		writer.println("skinparam classAttributeIconSize 0");

		for (ParsedClass parsedClass : parsedClasses) {
			writer.println("class " + parsedClass.getName() + " {");

			for (String privateNonStaticField : parsedClass.getFields(Modifier.privateModifier(),
					Modifier.staticModifier())) {
				writer.println("\t-" + privateNonStaticField);
			}

			for (String protectedNonStaticField : parsedClass.getFields(Modifier.protectedModifier(),
					Modifier.staticModifier())) {
				writer.println("\t#" + protectedNonStaticField);
			}

			for (String publicNonStaticField : parsedClass.getFields(Modifier.publicModifier(), Modifier.staticModifier())) {
				writer.println("\t+" + publicNonStaticField);
			}

			for (String privateStaticField : parsedClass
					.getFields(new Modifier[] { Modifier.privateModifier(), Modifier.staticModifier() })) {
				writer.println("\t-{static} " + privateStaticField);
			}

			for (String protectedStaticField : parsedClass
					.getFields(new Modifier[] { Modifier.protectedModifier(), Modifier.staticModifier() })) {
				writer.println("\t#{static} " + protectedStaticField);
			}

			for (String publicStaticField : parsedClass
					.getFields(new Modifier[] { Modifier.publicModifier(), Modifier.staticModifier() })) {
				writer.println("\t+{static} " + publicStaticField);
			}

			if (!cl.hasOption(OPT_OMIT_CONSTRUCTORS)) {
				for (String privateConstructor : parsedClass.getConstructors(new Modifier[] { Modifier.privateModifier() })) {
					writer.println("\t-" + privateConstructor);
				}

				for (String protectedConstructor : parsedClass
						.getConstructors(new Modifier[] { Modifier.protectedModifier() })) {
					writer.println("\t#" + protectedConstructor);
				}

				for (String publicConstructor : parsedClass.getConstructors(new Modifier[] { Modifier.publicModifier() })) {
					writer.println("\t+" + publicConstructor);
				}
			}

			if (!cl.hasOption(OPT_OMIT_METHODS)) {
				for (String privateNonStaticMethod : parsedClass.getMethods(Modifier.privateModifier(),
						Modifier.staticModifier())) {
					writer.println("\t-" + privateNonStaticMethod);
				}

				for (String protectedNonStaticMethod : parsedClass.getMethods(Modifier.protectedModifier(),
						Modifier.staticModifier())) {
					writer.println("\t#" + protectedNonStaticMethod);
				}

				for (String publicNonStaticMethod : parsedClass.getMethods(Modifier.publicModifier(),
						Modifier.staticModifier())) {
					writer.println("\t+" + publicNonStaticMethod);
				}
			}

			if (!cl.hasOption(OPT_OMIT_MODIFIERS)) {
				for (String privateStaticMethod : parsedClass
						.getMethods(new Modifier[] { Modifier.privateModifier(), Modifier.staticModifier() })) {
					writer.println("\t-{static} " + privateStaticMethod);
				}

				for (String protectedStaticMethod : parsedClass
						.getMethods(new Modifier[] { Modifier.protectedModifier(), Modifier.staticModifier() })) {
					writer.println("\t#{static} " + protectedStaticMethod);
				}

				for (String publicStaticMethod : parsedClass
						.getMethods(new Modifier[] { Modifier.publicModifier(), Modifier.staticModifier() })) {
					writer.println("\t+{static} " + publicStaticMethod);
				}
			}

			writer.println("}");

			if (cl.hasOption(OPT_SHOW_RELATIONS)) {
				for (String relation : parsedClass.getRelations()) {
					writer.println(relation);
				}
			}
		}
		writer.println("@enduml");

		writer.close();
		fWrite.close();
	}

	private static List<File> listFiles(String path, boolean recursive) {
		File[] files = new File(path).listFiles();

		List<File> javaFiles = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && recursive) {
				javaFiles.addAll(listFiles(files[i].getAbsolutePath(), recursive));
			} else if (files[i].getName().length() > 5
					&& files[i].getName().substring(files[i].getName().length() - 5).equalsIgnoreCase(".java")
					&& !files[i].isDirectory()) {
				javaFiles.add(files[i]);
			}
		}

		return javaFiles;
	}

}
