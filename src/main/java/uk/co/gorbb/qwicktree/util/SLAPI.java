package uk.co.gorbb.qwicktree.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/** SLAPI = Saving/Loading API
 * API for Saving and Loading Objects.
 * @author Tomsik68
 */
public class SLAPI
{
	public static void save(Object obj,String path) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	
	public static Object load(String path) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		Object result = ois.readObject();
		ois.close();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, Integer> loadIgnoreList(File folder, String file) {
		File path = new File(folder, file);
		
		try {
			return (HashMap<String, Integer>) load(path.getAbsolutePath());
		}
		catch (Exception e) {
			return new HashMap<String, Integer>();
		}
	}
	
	public static void saveIgnoreList(File folder, String file, HashMap<String, Integer> ignoreList) {
		File path = new File(folder, file);
		
		try {
			save(ignoreList, path.getAbsolutePath());
		}
		catch (Exception e) { }
	}
}