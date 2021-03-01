package co.deability.libs.util;

import java.util.*;

import static co.deability.libs.util.Strings.NL;

/**
 * OSTest provides a simple means of testing the OS running the JVM in which it was instantiated. It
 * currently tests for the following operating systems:
 *	<ul>
 * 		<li>Windows</li>
 * 		<li>Windows Server</li>
 * 		<li>Linux</li>
 * 		<li>Solaris</li>
 * 		<li>AIX</li>
 * 		<li>BSD</li>
 * 		<li>OSX</li>
 * 		<li>MacOS</li>
 *	</ul>
 * It also provides a means of determining whether the platform is fundamentally a Unix or
 * Windows variant.
 */
public class OSTest{

	/**
	 * The name of the operating system environmet, transmogrified into UPPER CASE.
	 */
	private final String osName;
	/**
	 * A collection of applicable {@link OS} types that describe the operating system in which
	 * this OSTest's parent JVM is running.
	 */
	private final Set<OS> osEnvironment;
	/**
	 * The singleton instance of OSTest that's returned by {@link #getInstance()};
	 */
	private static final OSTest INSTANCE = new OSTest();


	/**
	 * Private constructor, since this class is designed to be run as a singleton.
	 *
	 * @see #getInstance()
	 */
	private OSTest(){
		this.osName = System.getProperty("os.name").toUpperCase();
		Set<OS> osEnvironmentHolder = new HashSet<>();
		if (Strings.isNotEmpty(osName)){
			if (osName.contains("WINDOWS")){
				osEnvironmentHolder.add(OS.WINDOWS);
				if (osName.contains("NT") || osName.contains("SERVER"))
					osEnvironmentHolder.add(OS.WINDOWS_SERVER);
			}
			else if (osName.contains("BSD")
			|| osName.contains("UNIX")
			|| osName.contains("SUNOS")
			|| osName.contains("SOLARIS")
			|| osName.contains("AIX")){
				osEnvironmentHolder.add(OS.UNIX);
				if (osName.contains("SUNOS")
				|| osName.contains("SOLARIS")){
					osEnvironmentHolder.add(OS.SOLARIS);
				}
			}
			else if (osName.contains("MAC")
			|| osName.contains("OS X")
			|| osName.contains("MACOS")
			|| osName.contains("DARWIN")){
				osEnvironmentHolder.add(OS.UNIX);
				osEnvironmentHolder.add(OS.MAC);
			}
			else if (osName.contains("LINUX")){
				osEnvironmentHolder.add(OS.UNIX);
				osEnvironmentHolder.add(OS.LINUX);
			}
			else{
				osEnvironmentHolder.add(OS.UNKNOWN);
			}
		}
		else{
			osEnvironmentHolder.add(OS.UNKNOWN);
		}
		this.osEnvironment = Collections.unmodifiableSet(osEnvironmentHolder);
	}


	/**
	 * Returns an OSTest instance that can be used to determine the nature of the operating
	 * system in which the JVM is running.
	 *
	 * @return an OSTest instance that can be used to determine the nature of the operating
	 * system in which the JVM is running.
	 */
	public static OSTest getInstance(){
		return INSTANCE;
	}


	/**
	 * Returns the name of the operating system in which the JVM is running.
	 *
	 * @return  the name of the operating system in which the JVM is running.
	 */
	public String getOSName(){
		return osName;
	}


	/**
	 * Returns {@code true} if the operating system in which the JVM is running is a Windows
	 * variant; {@code false} otherwise.
	 *
	 * @return {@code true} if the operating system in which the JVM is running is a Windows
	 * variant; {@code false} otherwise.
	 */
	public boolean isWindows(){
		return osEnvironment.contains(OS.WINDOWS);
	}


	/**
	 * Returns {@code true} if the operating system in which the JVM is running is Solaris;
	 * {@code false} otherwise.
	 *
	 * @return {@code true} if the operating system in which the JVM is running is Solaris;
	 * {@code false} otherwise.
	 */
	public boolean isSolaris(){
		return osEnvironment.contains(OS.SOLARIS);
	}

	/**
	 * Returns {@code true} if the operating system in which the JVM is running is a Unix
	 * variant; {@code false} otherwise.
	 *
	 * @return {@code true} if the operating system in which the JVM is running is a Unix
	 * variant; {@code false} otherwise.
	 */
	public boolean isUnix(){
		return osEnvironment.contains(OS.UNIX);
	}

	/**
	 * Returns {@code true} if the operating system in which the JVM is running is an Apple
	 * variant; {@code false} otherwise.
	 *
	 * @return {@code true} if the operating system in which the JVM is running is an Apple
	 * variant; {@code false} otherwise.
	 */
	public boolean isMac(){
		return osEnvironment.contains(OS.MAC);
	}

	/**
	 * Returns {@code true} if the operating system in which the JVM is running is a Linux
	 * variant; {@code false} otherwise.
	 *
	 * @return {@code true} if the operating system in which the JVM is running is a Linux
	 * variant; {@code false} otherwise.
	 */
	public boolean isLinux(){
		return osEnvironment.contains(OS.LINUX);
	}


	/**
	 * Returns {@code true} if the operating system in which the JVM is running is a Windows Server
	 * variant; {@code false} otherwise.
	 *
	 * @return {@code true} if the operating system in which the JVM is running is a Windows Server
	 * variant; {@code false} otherwise.
	 */

	public boolean isWindowsServer(){
		return osEnvironment.contains(OS.WINDOWS_SERVER);
	}


	/**
	 * Returns {@code true} if nothing about the nature of the operating system in which the JVM is 
	 * running can be determined directly via this class; {@code false} otherwise.
	 * 
	 * @return {@code true} if nothing about the nature of the operating system in which the JVM is
	 * running can be determined directly via this class; {@code false} otherwise.
	 */
	public boolean isUnknown(){
		return osEnvironment.contains(OS.UNKNOWN);
	}


	/**
	 * An enumeration of operating system types. Unlike most enumerations, they are not mutually 
	 * exclusive.
	 */
	public enum OS{
		WINDOWS,
		WINDOWS_SERVER,
		SOLARIS,
		UNIX,
		MAC,
		LINUX,
		UNKNOWN;


		/**
		 * Returns {@code true} if the supplied other is consonant (of the same or similar type)
		 * with this OS instance; {@code false} otherwise.
		 *
		 * @param other The other OS to be compared to this one for similarity.
		 * @return {@code true} if the supplied other is consonant with this OS instance;
		 * {@code false} otherwise.
		 */
		public boolean isConsonant(OS other){
			switch(this){
				case WINDOWS:
				case WINDOWS_SERVER:
					return other == WINDOWS || other == WINDOWS_SERVER;
				case SOLARIS: return other == SOLARIS || other == UNIX;
				case UNIX: return other == SOLARIS || other == MAC || other == LINUX || other == UNIX;
				case MAC: return other == MAC || other == UNIX;
				case LINUX: return other == LINUX || other == UNIX;
				case UNKNOWN: return false;
				default: throw new IllegalArgumentException(other + " is not a recognized OS.");
			}
		}
	}


	/**
	 * Reports out the available information about the operating system environment in which the
	 * JVM is running.
	 *
	 * @return A string that describes the operating system environment in which the JVM is running.
	 */
	@Override
	public String toString(){
		return "[OSTest]" + NL + Strings.indent(
			"osName = " + osName + NL
			+ "isWindows = " + isWindows() + NL
			+ "isWindowsServer = " + isWindowsServer() + NL
			+ "isUnix = " + isUnix() + NL
			+ "isSolaris = " + isSolaris() + NL
			+ "isMac = " + isMac() + NL
			+ "isLinux = " + isLinux() + NL
			+ "isUnknown = " + isUnknown() + NL);
	}
}
