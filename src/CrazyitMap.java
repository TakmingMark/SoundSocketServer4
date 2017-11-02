import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CrazyitMap<K,V> 
{
	//建立一個安執行緒安全的HashMap
	public Map<K,V> map=Collections.synchronizedMap(new HashMap<K,V>());
	
	//根據Value來刪除指定項目
	public synchronized void removeByValue(Object value)
	{
		for(Object key:map.keySet()) //object可否改K
		{
			if(map.get(key)==value)
			{
				map.remove(key);
				break;
			}
		}
	}
	
	//獲取所有value組成的Set集合
	public synchronized Set<V> valueSet()
	{
		Set<V> result=new HashSet<V>();
		map.forEach((Key,value)->result.add(value));
		return result;
	}
	
	//獲取所有key組成的Set集合
		public synchronized Set<K> keySet()
		{
			Set<K> result=new HashSet<K>();
			map.forEach((Key,value)->result.add(Key));
			return result;
		}
		
	//根據value來尋找key
	public synchronized K getKeyByValue(V val)
	{
		for(K key:map.keySet())
		{
			if(map.get(key)==val || map.get(key).equals(val))
			{
				return key;
			}
		}
		return null;
	}
	
	//根據key來尋找value
	public synchronized V getValueByKey(K key)
	{
		for(K k:map.keySet())
		{
			if(k==key)
			{
				return map.get(k);
			}
		}
		return null;
	}
	
	//實作put()方法，該方法不允許value重複
	public synchronized V put(K key,V value)
	{
		//跑遍所有value組成的集合
		for (V val : valueSet())
		{
			if(val.equals(value) && val.hashCode()==value.hashCode())
			{
				//如果某個value和集合中的value相同，則拋出一個RuntimeException異常
				throw new RuntimeException("MyMap實例中不允許有重複value!");
			}
		}
		return map.put(key, value);
	}
	
	public synchronized int size()
	{
		
		return map.size();
	}
}