package org.hydra.utils;

import java.util.Arrays;

public final class ByteArray {
	private final byte[] _data;
	private final int _hashCode;
	
	public ByteArray(byte[] data){
		if(data == null){
			throw new NullPointerException();
		}
		_data = data;
		_hashCode = Arrays.hashCode(_data);
	}

	@Override
	public boolean equals(Object inByteArray) {
		if(inByteArray == null) return false;
		
		if(!(inByteArray instanceof ByteArray)){
			return false;
		}
		return Arrays.equals(_data, ((ByteArray)inByteArray)._data);
	}

	@Override
	public int hashCode() {
		return _hashCode;
	}
}
