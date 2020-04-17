package com.sudocode.sudohide;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class PrefMap<K, V> extends HashMap<K, V> {

	public Object getObject(String key, Object defValue) {
		return get(key) == null ? defValue : get(key);
	}

	public int getInt(String key, int defValue) {
		return get(key) == null ? defValue : (Integer)get(key);
	}

	public String getString(String key, String defValue) {
		return get(key) == null ? defValue : (String)get(key);
	}

	@SuppressWarnings("unchecked")
	public Set<String> getStringSet(String key) {
		return get(key) == null ? new LinkedHashSet<String>() : (Set<String>)get(key);
	}

	public boolean getBoolean(String key) {
		return get(key) == null ? false : (Boolean)get(key);
	}

}
