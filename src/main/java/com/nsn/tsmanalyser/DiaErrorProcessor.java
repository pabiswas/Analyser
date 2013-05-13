package com.nsn.tsmanalyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author pabiswas
 *
 */
public class DiaErrorProcessor implements IErrorProcessor {

	Set<String> diameterResultCode = new HashSet<String>();
	HashMap ErrorMap = new HashMap();

	public DiaErrorProcessor() throws Throwable {
		diameterResultCode = new HashSet<String>();
		ErrorMap.clear();

                File aFile = utils.readResource("/Diameter/DiameterErrors.txt", "tmpDiameterErrors.txt");
		BufferedReader input =  new BufferedReader(new FileReader(aFile));
		String line;

		try
		{
			while (( line = input.readLine()) != null)
			{
				diameterResultCode.add(line);
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
		final int timeLength = 14 ;
		String msg = null;
		File aFile = utils.readResource("/Diameter/DiameterMessages.txt", "tmpDiametermessages.txt");
		BufferedReader input =  new BufferedReader(new FileReader(aFile));

		String tcFailed   = ": Error:";
		String resultCode = "Result Code is = ";
		String errReason  = "Failed:";

		if(line.contains(resultCode))
		{
			String code = line.substring(line.indexOf(resultCode) + resultCode.length(),line.indexOf(resultCode) + resultCode.length() + 4);
			analyseResultCode(code);
			return "RESULT CODE:" + code;
		}
		if(line.contains(tcFailed))
		{
			analyseResultCode("Other");
			String errorReason = line.substring(line.indexOf(tcFailed) + tcFailed.length(),line.length() - timeLength);
			return "Error: " + errorReason;
		}
		if(line.contains(errReason))
		{
			String errorReason = line.substring(line.indexOf(errReason) + errReason.length(),line.length() - timeLength);
			return "Error: " + errorReason;
		}

		try
		{
		  while (( msg = input.readLine()) != null)
		  {
			  if(line.contains(msg))
			  {
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

	private void analyseResultCode(String code) {
		Iterator<String> it = diameterResultCode.iterator();
		String value = null;

		if( code == "Other")
		{
			updateErrorMap(code);
		}
		else
		{
			while(it.hasNext())
			{
				value = it.next();

				if(value.equals(code))
				{
					updateErrorMap(code);
				}
			}
		}
	}

	private void updateErrorMap(String code) {
		if(ErrorMap.containsKey(code))
		{
			int count = (Integer)(ErrorMap.get(code));
			ErrorMap.put(code, ++count);
		}
		else
		{
			ErrorMap.put(code, 1);
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
