package com.billhorvath.libs.util;

import static com.billhorvath.libs.util.Strings.NL;

/**
This class provides a simple means of testing the OS running the JVM in which this class was instantiated. It currently tests for the following operating systems:
	
	Windows XP
	Windows Server 2003
	Windows NT
	Windows Vista
	Windows 7
	Linux
	Solaris
	BSD
	OSX
	
It also provides a means of determining whether the platform is fundamentally a unix or Windows variant.

Future updates will address current versions of listed OSes, as well as new OSes.

*/

public class OSTest{
	private String osName;
	private boolean windows = false;
	private boolean unix = false;
	private boolean mac = false;
	private boolean linux = false;
	private boolean xp = false;
	private boolean vista = false;
	private boolean seven = false;
	private boolean twothousandthree = false;
	private boolean nt = false;
	private boolean solaris = false;
	private boolean unknown = true;
	private static final OSTest INSTANCE = new OSTest();
	
	/**
		
	*/
	
	private OSTest(){
		osName = System.getProperty("os.name").toUpperCase();
		if (!(osName.equals(null)) 
		&& (!osName.equals(""))){
			if (osName.indexOf("WINDOWS") >= 0){
				windows = true;
				unknown = false;
				if (osName.indexOf("XP") >= 0) xp = true; 
				if (osName.indexOf("2003") >= 0) twothousandthree = true;
				if (osName.indexOf("NT") >= 0) nt = true;
				if (osName.indexOf("VISTA") >= 0) vista = true;
				if (osName.indexOf("7") >= 0) seven = true;
			}
			else if (osName.indexOf("BSD") >= 0
			|| osName.indexOf("UNIX") >= 0
			|| osName.indexOf("SUNOS") >= 0
			|| osName.indexOf("SOLARIS") >= 0){
				unix = true;
				unknown = false;
				if (osName.indexOf("SUNOS") >= 0
				|| osName.indexOf("SOLARIS") >= 0){
					solaris = true;
				}
			}
			else if (osName.indexOf("MAC") >= 0
			|| osName.indexOf("OS X") >= 0 
			|| osName.indexOf("DARWIN") >= 0){
				unix = true;
				mac = true;
				unknown = false;
			}
			else if (osName.indexOf("LINUX") >= 0){
				unix = true;
				linux = true;
				unknown = false;
			}
			else{
				unknown = true;
			}
		}
	}

	/**
	
	**/

	public static OSTest getInstance(){
		return INSTANCE;
	}

	/**
	
	**/

	public String getOSName(){
		return osName;
	}

	/**
	
	**/

	public boolean isWindows(){
		return windows;
	}
	
	/**
		
	*/
	
	public boolean isSolaris(){
		return solaris;
	}
	
	/**
		
	*/
	
	public boolean isUnix(){
		return unix;
	}
	
	/**
	
	**/

	public boolean isMac(){
		return mac;
	}

	/**
	
	**/

	public boolean isLinux(){
		return linux;
	}

	/**
	
	**/

	public boolean isXP(){
		return xp;
	}
	
	/**
		
	*/
	
	public boolean isNT(){
		return nt;
	}
	
	/**
		
	*/
	
	public boolean is2003(){
		return twothousandthree;
	}
	
	/**
		
	*/
	
	public boolean isVista(){
		return vista;
	}

	/**
		
	*/
	
	public boolean isSeven(){
		return seven;
	}
	
	/**
	
	**/

	public boolean isUnknown(){
		return unknown;
	}
	
	/**
		
	*/
	@Override
	public String toString(){
		return "[OSTest]" + NL + Strings.indent(
			"osName = " + osName + NL
			+ "isWindows = " + windows + NL
			+ "isXP = " + xp + NL
			+ "is2003 = " + twothousandthree + NL
			+ "isVista = " + vista + NL
			+ "isNT = " + nt + NL
			+ "isSeven = " + seven + NL
			+ "isUnix = " + unix + NL
			+ "isSolaris = " + solaris + NL
			+ "isMac = " + mac + NL
			+ "isLinux = " + linux + NL
			+ "isUnknown = " + unknown + NL);
	}
}
