package com.custom.blockchain.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 
 * @author marcos
 *
 */
public class ObjectUtil extends ObjectUtils {

	/**
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static int sizeof(Object obj) throws IOException {

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

		objectOutputStream.writeObject(obj);
		objectOutputStream.flush();
		objectOutputStream.close();

		return byteOutputStream.toByteArray().length;
	}

}
