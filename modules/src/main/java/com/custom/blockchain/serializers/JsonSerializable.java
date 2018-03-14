package com.custom.blockchain.serializers;

import java.io.Serializable;

public interface JsonSerializable<T> extends Serializable {

	public T unserializable() throws Exception;

}
