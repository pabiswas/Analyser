package com.nsn.tsmanalyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author pabiswas
 *
 */
public class MapErrorProcessor implements IErrorProcessor {

	Set<String> mapErrorSet = new HashSet<String>();
	HashMap ErrorMap = new HashMap();
	
	public MapErrorProcessor() throws Throwable {
		mapErrorSet = new HashSet<String>();
		ErrorMap.clear();
		
                InputStream is = com.nsn.tsmanalyser.tsmAnalyser.class.getResourceAsStream("/Map/MapErrors.txt");
                
                File tmpFile = File.createTempFile("tmpMapErrors", "txt");
                // tempFile.deleteOnExit();
                assert (tmpFile.exists()) : "could not create tempfile";

                OutputStream out = new FileOutputStream(tmpFile);
                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = is.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                
		BufferedReader input =  new BufferedReader(new FileReader(tmpFile));
		String line;
		
		try 
		{
			while (( line = input.readLine()) != null)
			{
				mapErrorSet.add(line);
				ErrorMap.put(line, 0);
			}
		}
		finally 
		{
		  input.close();
		}		
	}

	@Override
	public String processLine(String line) throws Throwable {
		// TODO Auto-generated method stub
		final int endIndex = 59;
		String msg = null;
                File aFile = utils.readResource("/Map/MapMessages.txt", "tmpMapMessages.txt");
		BufferedReader input =  new BufferedReader(new FileReader(aFile));

		String tcFailed = "FAILED: ";
		
		if(line.contains(tcFailed))
		{
			String failureReason = line.substring(line.indexOf(tcFailed) + tcFailed.length(),endIndex);
			return "ERROR:" + failureReason;
		}
		
		String tcPassed = "SUCCESS";
		if(line.contains(tcPassed ))
		{
			String failureReason = line. substring(line.indexOf(tcPassed) + tcPassed.length() + 1,endIndex);
			return "SUCCESS:" +failureReason;
		}
		
		try 
		{
			  while (( msg = input.readLine()) != null)
			  {		  
				  if(line.contains(msg))
				  {
					  analyseError(msg);
					  return msg;
				  }
				  else
					  continue;
			  }
		}
		finally 
		{
		  input.close();
		}
		
		return null;
	}
	
	/**
	 * @param msg : IPSL o/p file line which is a valid message e.g R000000-0 -0  : usr: |--.R_ISD--------->|                  | 24 19:36:47.78
	 */
	public void analyseError(String msg)
	{
		//--- mapErrorSet => Contains MAP specific errors e.g. E_US 
		
		Iterator<String> it = mapErrorSet.iterator();
		String value = null;
		
		while(it.hasNext())
		{
			value = it.next();
			
			if(msg.contains(value))
			{
				if(ErrorMap.containsKey(value))
				{
					int count = (Integer)(ErrorMap.get(value));
					ErrorMap.put(value, ++count);
				}
				else
				{
					ErrorMap.put(value, 1);
				}
			}
		}
	}

	@Override
	public void resetErrorMap() {
		ErrorMap.clear();
	}
	
	@Override
	public HashMap<String, Integer> getErrorMap() {
		return ErrorMap;
	}
}
