package net.collaud.fablab.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.collaud.fablab.data.AbstractDataEO;

/**
 *
 * @author gaetan
 * @param <T> Data type
 */
public class ListToMapConverter<T extends AbstractDataEO>{
	
	private final List<T> entries;
	
	public ListToMapConverter(List<T> entries){
		this.entries = entries;
	}
	
	public Map<Integer, T> getMap(){
		Map<Integer, T> map = new HashMap<>();
		for(T obj : entries){
			map.put(obj.getId(), obj);
		}
		return map;
	}
}
