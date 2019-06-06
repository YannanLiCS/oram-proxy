package eoram.cloudexp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;


/**
 * Implements misc utility functions.
 */
public class MiscUtils 
{
	private static final MiscUtils instance = new MiscUtils();
	
	private MiscUtils() {}
	
	public static MiscUtils getInstance() { return instance; }
	
	public byte[] ByteArrayFromInputStream(InputStream is)
	{
	    try
	    {
	    	ByteArrayOutputStream os = new ByteArrayOutputStream();
	        byte[] buf = new byte[0xFFFF];
	
	        for (int len = 0; (len = is.read(buf)) != -1;) { os.write(buf, 0, len); }
	        os.flush();
	
	        return os.toByteArray();
	    }
	    catch (IOException e)
	    {
	    	Errors.error(e);
	        return null;
	    }
	}
	
	/* Solution from http://stackoverflow.com/questions/12385284/how-to-select-a-random-key-from-a-hashmap-in-java */
	public <T,S> T randomMapKey(Map<T, S>  map, Random rng)
	{
		if(map.size() == 0) { return null; }
		try 
		{ 
			Field table = HashMap.class.getDeclaredField("table");
			table.setAccessible(true);
			Entry<T, S>[] entries = (Entry<T, S>[])table.get(map);
		    int start = rng.nextInt(entries.length);
		    for(int i=0;i<entries.length;i++) 
		    {
		       int idx = (start + i) % entries.length;
		       Entry<T, S> entry = (Entry<T, S>)entries[idx];
		       
		       Set<Entry<T, S>> entriesSet = new HashSet<>();
		       while(entry != null)
	    	   {
		    	   entriesSet.add(entry);
		    	   Field next = entry.getClass().getDeclaredField("next"); next.setAccessible(true);
		    	   entry = (Entry<T, S>)next.get(entry);
		       }
		       if(entriesSet.isEmpty() == false) { return entriesSet.iterator().next().getKey(); }
		    }
		} 
		catch (Exception e) { Errors.error("Coding FAIL!"); }
	    return null;
	}

	public String getRandomHexString(int length)
	{
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(Integer.toHexString(random.nextInt(16)));
		}
		return sb.toString();
	}

	public String getMD5Hash(byte[] data)
	{
		String hash = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data);
			hash = EncodingUtils.getInstance().toHexString(md.digest()).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	public String getMD5Hash(String data)
	{
		return getMD5Hash(data.getBytes());
	}
}
