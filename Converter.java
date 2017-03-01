package ca.vincemacri.javauml;

import java.io.File;
import java.io.IOException;

import com.github.javaparser.JavaParser;

public class Converter {
	
	public static int test1;
	
	private int test2;
	
	protected int test3;
	
	public static void main(String[] args) throws IOException {
		ParsedClass[] parsedClasses = new ParsedClass[args.length];
		
		for (int i = 0; i < args.length; i++) {
			parsedClasses[i] = new ParsedClass(JavaParser.parse(new File(args[i])));
		}
	}

}
