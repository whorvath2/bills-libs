package co.deability.libs.util;

import java.io.File;
import java.net.URI;

/**
 * This class contains static utility methods for certain common URI operations.
 */

public final class URIs {

	/**
	 * A URI that represents the current working directory, provided for convenience.
	 */
	public static final URI FOLDER;

	static {
		FOLDER = URI.create("./");
	}

	/**
	 * Since this class is a utility class, it should never be instantiated.  Ergo, a private empty
	 * constructor is provided to prevent initialization by any sub-classes.
	 */
	private URIs() {
	}


	/**
	 * This method calculates the directory of the parent URI (which may point to either a
	 * directory
	 * or a file), and determines whether the child URI lies within that directory. Both the parent
	 * and the child must be absolute.
	 *
	 * @param parent
	 * @param child
	 *
	 * @return <code>true</code> if the folder represented in the path of <code>parent</code> lies
	 * at or above the folder represented in the path of <code>child</code>; <code>false</code>
	 * otherwise.
	 *
	 * @throws NullPointerException     if either <code>parent</code> or <code>child</code> is
	 *                                  null.
	 * @throws IllegalArgumentException if either <code>parent</code> or <code>child</code> is not
	 *                                  absolute.
	 */
	public static boolean parentContains(URI parent, URI child) {
		assert parent != null : "URI parent is null!";
		assert child != null : "URI child is null!";
		if (!parent.isAbsolute() || !child.isAbsolute()) {
			throw new IllegalArgumentException("Either the parent parameter ("
					+ parent + "), the child parameter (" + child
					+ "), or both are not absolute.");
		}
		URI folder = URIs.folder(parent);
		String parentPath = folder.getRawPath();
		String childPath = child.getRawPath();
		return childPath.startsWith(parentPath);
	}


	/**
	 * This method relativises the <code>child</code> URI parameter against the <code>parent</code>
	 * URI parameter.  It is intended to address weaknesses in the <code>relativize</code>
	 * method in {@link URI URI}, which does not appear to work properly.
	 *
	 * @param parent
	 * @param child
	 *
	 * @return A URI representing the differences in the paths of <code>parent</code> and
	 * <code>child</code>, such that ...
	 *
	 * @throws NullPointerException if either <code>parent</code> or <code>child</code> is null.
	 */
	public static URI relativise(URI parent, URI child) {
		assert parent != null : "URI parent is null!";
		assert child != null : "URI child is null!";
		URI newUri = parent;
		String parentPath = parent.getRawPath();
		String childPath = child.getRawPath();
		if (childPath.startsWith(parentPath)) {
			String str = childPath.substring(parentPath.length());
			if (!Strings.isEmpty(str)) {
				newUri = URI.create(str);
			}
		}
		return newUri;
	}


	/**
	 * This method calculates and returns a {@link URI URI} pointing to the lowest directory
	 * contained in the path heirarchy of the <code>uri</code> parameter.  If the <code>uri</code>
	 * parameter points directly to a directory, a URI which points to the same location will be
	 * returned.  If the <code>uri</code> points to a file, a URI which points to the file's parent
	 * directory will be returned.
	 *
	 * @param uri
	 *
	 * @return a URI which points to the lowest directory in the path heirarchy contained within
	 * the
	 * submitted <code>uri</code> parameter.
	 * @throws NullPointerException if the <code>uri</code> parameter is null.
	 * @see File#getParentFile()
	 * @see File#getPath()
	 */

	public static URI folder(URI uri) {
		URI folder = uri.resolve("./");
		return folder;
	}


	/**
	 * Calculates and returns the parent of <code>uri</code>.
	 *
	 * @param uri
	 * @return A URI representing the parent of <code>uri</code>.
	 * @throws NullPointerException if <code>uri</code> is null.
	 */

	public static URI parentOfFolder(URI uri) {
		URI folder = URIs.folder(uri);
		folder = folder.resolve("../");
		return folder;
	}


	/**
	 *
	 * @param uri
	 * @return
	 */
	public static URI file(URI uri) {
		URI folderUri = URIs.folder(uri);
		if (folderUri.equals(uri)) {
			assert false : "uri = " + uri;
			return URIs.FOLDER;
		}
		URI fileUri = URIs.relativise(folderUri, uri);
		return fileUri;
	}


	/**
	 *
	 * @param parent
	 * @param child
	 * @return
	 */
	public static URI resolve(URI parent, String child) {
		URI parentFolder = URIs.folder(parent);
		if (parentFolder.toString().endsWith("/"))
			child = Strings.trimLeading(child, '/');
		URI result = null;
		try {
			result = parentFolder.resolve(child);
		}
		catch (IllegalArgumentException e) {
			File file = new File(parent);
			file = new File(file, child);
			result = file.toURI();
		}
		return result;
	}


	/**
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static int compare(Object a, Object b) {

		if (a == null && b == null) return 0;
		if (a == null) {
			if (b instanceof URI) return -1;
			throw new UnsupportedOperationException("Object a is null, and "
					+ "Object b is not an instance of URI.");
		}

		if (b == null) {
			if (a instanceof URI) return 1;
			throw new UnsupportedOperationException("Object b is null, "
					+ "and Object a is not an instance of URI.");
		}

		if (!(a instanceof URI && b instanceof URI))
			throw new UnsupportedOperationException("Both objects must be "
					+ "instances of URI.");
		return ((URI) a).compareTo((URI) b);
	}


	/**
	 * Returns true if <code>one</code> and <code>two</code> are equivalent in the practical sense;
	 * that is, they both point to the same file.
	 * <p>
	 * This method calculates equivalence by first comparing the URIs for referential or object
	 * equivalence. If neither shows equality, this method compares the paths of files constructed
	 * by <code>new File(one)</code> and <code>new File(two)</code>.
	 *
	 * @param one
	 * @param two
	 *
	 * @return <code>true</code> if both one and two are non-null, and if either A) one is
	 * referentially identical or equal to two, or B) the files to which one and two point have the
	 * same paths. Otherwise, this method returns <code>false</code>.
	 */
	public static boolean equivalent(URI one, URI two) {
		if (one == null || two == null) return false;
		if (one == two || one.equals(two)) return true;
		File fileA = new File(one);
		File fileB = new File(two);
		return fileA.getPath().equals(fileB.getPath());
	}


	/**
	 * Returns the extension of the file to which <code>uri</code> points, exclusive of the period.
	 * (I.e., The parameter value "/foobar.doc" will return "doc".) If <code>uri</code> does not
	 * point to a file, or it points to a file with no extension, this method returns the empty
	 * string ("").
	 *
	 * @param uri
	 *
	 * @return A string representing the file extension of the <code>uri</code>, or the empty
	 * string
	 * ("") if <code>uri</code> points to a folder, or to a file with no extension.
	 * @throws NullPointerException if the <code>uri</code> parameter is null.
	 */
	public static String extension(URI uri) {
		assert uri != null : "The uri parameter is null!";
		String name = new File(uri).getName();
		int dot = name.lastIndexOf('.');
		if (dot != -1)
			return name.substring(dot + 1).toLowerCase();
		return "";
	}


	/**
	 * Since this class will never be instantiated, this method will always return -1.
	 *
	 * @return <code>-1</code>.
	 */
	@Override
	public int hashCode() {
		return -1;
	}


	/**
	 * Since this class will never be instantiated, this method will always return false.
	 *
	 * @return <code>false</code>.
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}


	/**
	 * Since this class will never be instantiated, this method will always return null.
	 *
	 * @return <code>null</code>.
	 */
	@Override
	public String toString() {
		return null;
	}
}

