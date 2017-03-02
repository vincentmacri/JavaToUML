package ca.vincemacri.javauml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;

public class ParsedClass {

	/** The {@link CompilationUnit} for this parsed class. */
	private CompilationUnit unit;

	/**
	 * The {@link ParsedClass} constructor.
	 * 
	 * @param unit A {@link CompilationUnit} for this class.
	 */
	public ParsedClass(CompilationUnit unit) {
		this.unit = unit;
	}

	/**
	 * Get the name of this class.
	 * 
	 * @return The class name.
	 */
	public String getName() {
		return unit.getType(0).getNameAsString();
	}

	/**
	 * Get the parameters from the given {@link NodeList}.
	 * 
	 * @param nodeList The NodeList to extract parameters from.
	 * @return A string formatted for UML output that contains all of the parameters.
	 */
	private String getParameterList(NodeList<Parameter> nodeList) {
		StringBuilder parameters = new StringBuilder();
		for (Parameter parameter : nodeList) {
			parameters.append(parameter.getNameAsString() + " : " + parameter.getType() + ", ");
		}

		String result = parameters.toString();
		if (result.length() > 0) {
			return result.substring(0, result.length() - 2); // Remove extra comma and space.
		}
		return result;
	}

	/**
	 * Check if any not wanted modifiers are in the modifiers list.
	 * 
	 * @param modifiers The modifiers present.
	 * @param unwanted The modifiers to check against.
	 * @return True if the present modifiers contain no unwanted modifiers, or false if they do.
	 */
	private boolean containsNone(EnumSet<Modifier> modifiers, Modifier[] unwanted) {
		for (Modifier avoid : unwanted) {
			if (modifiers.contains(avoid)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert a {@link Stream} of {@link FieldDeclaration} to an array of PlantUML formatted strings.
	 * 
	 * @param validFields The fields to convert to PlantUML.
	 * @return A string array of PlantUML fields.
	 */
	private String[] formatFields(Stream<FieldDeclaration> validFields) {
		List<String> fields = new ArrayList<String>();
		for (Iterator<FieldDeclaration> iterator = validFields.iterator(); iterator.hasNext(); ) {
			FieldDeclaration field = (FieldDeclaration) iterator.next();
			for (VariableDeclarator variable : field.getVariables()) {
				fields.add(variable.getNameAsString() + " : " + field.getCommonType());
			}
		}
		return fields.toArray(new String[fields.size()]);
	}
	
	/**
	 * Convert a {@link Stream} of {@link MethodDeclaration} to an array of PlantUML formatted strings.
	 * 
	 * @param validMethods The methods to convert to PlantUML.
	 * @return A string array of PlantUML methods.
	 */
	private String[] formatMethods(Stream<MethodDeclaration> validMethods) {
		List<String> methods = new ArrayList<String>();
		for (Iterator<MethodDeclaration> iterator = validMethods.iterator(); iterator.hasNext(); ) {
			MethodDeclaration method = (MethodDeclaration) iterator.next();
			methods.add(method.getNameAsString() + "(" + getParameterList(method.getParameters()) + ") : " + method.getType());
		}
		return methods.toArray(new String[methods.size()]);
	}
	
	/**
	 * Convert a {@link Stream} of {@link ConstructorDeclaration} to an array of PlantUML formatted strings.
	 * 
	 * @param validConstructors The constructors to convert to PlantUML.
	 * @return A string array of PlantUML constructors.
	 */
	private String[] formatConstructors(Stream<ConstructorDeclaration> validConstructors) {
		List<String> constructors = new ArrayList<String>();
		for (Iterator<ConstructorDeclaration> iterator = validConstructors.iterator(); iterator.hasNext(); ) {
			ConstructorDeclaration method = (ConstructorDeclaration) iterator.next();
			constructors.add(method.getNameAsString() + "(" + getParameterList(method.getParameters()) + ")");
		}
		return constructors.toArray(new String[constructors.size()]);
	}
	
	/**
	 * Get all of the fields based on the given arrays of {@link Modifier}.
	 * 
	 * @param wanted Array of wanted modifiers.
	 * @param unwanted Array of all unwanted modifiers.
	 * @return String array of all fields matching the specified conditions.
	 */
	public String[] getFields(Modifier[] wanted, Modifier[] unwanted) {
		Stream<FieldDeclaration> fieldStream = unit.getNodesByType(FieldDeclaration.class).stream();
		Stream<FieldDeclaration> validFields = fieldStream.filter(f -> f.getModifiers().containsAll(Arrays.asList(wanted)) && containsNone(f.getModifiers(), unwanted));
		return formatFields(validFields);
	}


	/**
	 * Get all of the fields based on the given {@link Modifier}.
	 * 
	 * @param wanted The wanted modifier.
	 * @param unwanted The unwanted modifier.
	 * @return String array of all fields matching the specified conditions.
	 */
	public String[] getFields(Modifier wanted, Modifier unwanted) {
		Stream<FieldDeclaration> fieldStream = unit.getNodesByType(FieldDeclaration.class).stream();
		Stream<FieldDeclaration> validFields = fieldStream.filter(f -> f.getModifiers().contains(wanted) && !f.getModifiers().contains(unwanted));
		return formatFields(validFields);
	}

	/**
	 * Get all of the fields based on the given {@link Modifier}.
	 * 
	 * @param wanted Array of wanted modifiers.
	 * @return String array of all fields matching the specified conditions.
	 */
	public String[] getFields(Modifier[] wanted) {
		Stream<FieldDeclaration> fieldStream = unit.getNodesByType(FieldDeclaration.class).stream();
		Stream<FieldDeclaration> validFields = fieldStream.filter(f -> f.getModifiers().containsAll(Arrays.asList(wanted)));
		return formatFields(validFields);
	}

	/**
	 * Get all of the methods based on the given {@link Modifier}.
	 * 
	 * @param wanted Array of wanted modifiers.
	 * @param unwanted Array of all unwanted modifiers.
	 * @return String array of all methods matching the specified conditions.
	 */
	public String[] getMethods(Modifier[] wanted, Modifier[] unwanted) {
		Stream<MethodDeclaration> methodStream = unit.getNodesByType(MethodDeclaration.class).stream();
		Stream<MethodDeclaration> validMethods = methodStream.filter(m -> m.getModifiers().containsAll(Arrays.asList(wanted)) && containsNone(m.getModifiers(), unwanted));
		return formatMethods(validMethods);
	}

	/**
	 * Get all of the methods based on the given {@link Modifier}.
	 * 
	 * @param wanted The wanted modifier.
	 * @param unwanted The unwanted modifier.
	 * @return String array of all methods matching the specified conditions.
	 */
	public String[] getMethods(Modifier wanted, Modifier unwanted) {
		Stream<MethodDeclaration> methodStream = unit.getNodesByType(MethodDeclaration.class).stream();
		Stream<MethodDeclaration> validMethods = methodStream.filter(m -> m.getModifiers().contains(wanted) && !m.getModifiers().contains(unwanted));
		return formatMethods(validMethods);
	}

	/**
	 * Get all of the methods based on the given {@link Modifier}.
	 * 
	 * @param wanted Array of wanted modifiers.
	 * @return String array of all methods matching the specified conditions.
	 */
	public String[] getMethods(Modifier[] wanted) {
		Stream<MethodDeclaration> methodStream = unit.getNodesByType(MethodDeclaration.class).stream();
		Stream<MethodDeclaration> validMethods = methodStream.filter(m -> m.getModifiers().containsAll(Arrays.asList(wanted)));
		return formatMethods(validMethods);
	}

	/**
	 * Get all of the constructors based on the given {@link Modifier}.
	 * 
	 * @param wanted Array of wanted modifiers.
	 * @param unwanted Array of all unwanted modifiers.
	 * @return String array of all methods matching the specified conditions.
	 */
	public String[] getConstructors(Modifier[] wanted, Modifier[] unwanted) {
		Stream<ConstructorDeclaration> constructorStream = unit.getNodesByType(ConstructorDeclaration.class).stream();
		Stream<ConstructorDeclaration> validConstructors = constructorStream.filter(c -> c.getModifiers().containsAll(Arrays.asList(wanted)) && containsNone(c.getModifiers(), unwanted));
		return formatConstructors(validConstructors);
	}

	/**
	 * Get all of the methods based on the given {@link Modifier}.
	 * 
	 * @param wanted The wanted modifier.
	 * @param unwanted The unwanted modifier.
	 * @return String array of all methods matching the specified conditions.
	 */
	public String[] getConstructors(Modifier wanted, Modifier unwanted) {
		Stream<ConstructorDeclaration> constructorStream = unit.getNodesByType(ConstructorDeclaration.class).stream();
		Stream<ConstructorDeclaration> validConstructors = constructorStream.filter(c -> c.getModifiers().contains(wanted) && !c.getModifiers().contains(unwanted));
		return formatConstructors(validConstructors);
	}

	/**
	 * Get all of the methods based on the given {@link Modifier}.
	 * 
	 * @param wanted Array of wanted modifiers.
	 * @return String array of all methods matching the specified conditions.
	 */
	public String[] getConstructors(Modifier[] wanted) {
		Stream<ConstructorDeclaration> constructorStream = unit.getNodesByType(ConstructorDeclaration.class).stream();
		Stream<ConstructorDeclaration> validConstructors = constructorStream.filter(m -> m.getModifiers().containsAll(Arrays.asList(wanted)));
		return formatConstructors(validConstructors);
	}
}
