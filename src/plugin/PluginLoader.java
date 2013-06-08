package plugin;import java.io.*;public class PluginLoader extends ClassLoader {    public Object loadClassOBJ (String filename) throws Exception     {		if (filename.endsWith(".class"))		{	    	String classname = filename.substring(0,filename.indexOf('.'));	    		    	System.out.print(classname + ", ");	    		    	Class c = findLoadedClass(classname);		        if (c == null) 	        {					File f = new File(filename);				  				int length = (int) f.length();				byte[] classbytes = new byte[length];				DataInputStream in = new DataInputStream(new FileInputStream(f));				in.readFully(classbytes);				in.close();				  				c = defineClass(classname, classbytes, 0, length);	        }		        resolveClass(c);	        	        if (c.getInterfaces().length != 0)	        	return c.newInstance();		}        return null;    }}