package ca.vincemacri.javauml;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ParsedClass {

	/** The {@link CompilationUnit} for this parsed class. */
	private CompilationUnit unit;

	/**
	 * The {@link ParsedClass} constructor.
	 * @param unit A {@link CompilationUnit} for this class.
	 */
	public ParsedClass(CompilationUnit unit) {
		this.unit = unit;
	}

	/**
	 * Get all of the public non-static methods.
	 * @return String array of all public non-static methods.
	 */
	public String[] getPublicNonStaticMethods() {
		List<String> methods = new ArrayList<String>();
		for (MethodDeclaration method : unit.getNodesByType(MethodDeclaration.class)) {
			if (method.isPublic() && !method.isStatic()) {
				methods.add(method.getNameAsString());
			}
		}
		return methods.toArray(new String[methods.size()]);
	}

	/**
	 * Get all of the public static methods.
	 * @return String array of all public static methods.
	 */
	public String[] getPublicStaticMethods() {
		List<String> methods = new ArrayList<String>();
		for (MethodDeclaration method : unit.getNodesByType(MethodDeclaration.class)) {
			if (method.isPublic() && method.isStatic()) {
				methods.add(method.getNameAsString());
			}
		}
		return methods.toArray(new String[methods.size()]);

	}
	/**
	 * Get all of the private non-static methods.
	 * @return String array of all private non-static methods.
	 */
	public String[] getPrivateNonStaticMethods() {
		List<String> methods = new ArrayList<String>();
		for (MethodDeclaration method : unit.getNodesByType(MethodDeclaration.class)) {
			if (method.isPrivate() && !method.isStatic()) {
				methods.add(method.getNameAsString());
			}
		}
		return methods.toArray(new String[methods.size()]);
	}
}
