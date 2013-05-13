package com.nsn.tsmanalyser;

import java.util.HashMap;

/**
 * @author pabiswas
 *
 */
public interface IErrorProcessor {
	void resetErrorMap();
	HashMap getErrorMap();
	String processLine(String line) throws Throwable;
}
