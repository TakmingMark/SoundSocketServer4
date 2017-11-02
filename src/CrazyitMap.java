import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CrazyitMap<K,V> 
{
	//�إߤ@�Ӧw������w����HashMap
	public Map<K,V> map=Collections.synchronizedMap(new HashMap<K,V>());
	
	//�ھ�Value�ӧR�����w����
	public synchronized void removeByValue(Object value)
	{
		for(Object key:map.keySet()) //object�i�_��K
		{
			if(map.get(key)==value)
			{
				map.remove(key);
				break;
			}
		}
	}
	
	//����Ҧ�value�զ���Set���X
	public synchronized Set<V> valueSet()
	{
		Set<V> result=new HashSet<V>();
		map.forEach((Key,value)->result.add(value));
		return result;
	}
	
	//����Ҧ�key�զ���Set���X
		public synchronized Set<K> keySet()
		{
			Set<K> result=new HashSet<K>();
			map.forEach((Key,value)->result.add(Key));
			return result;
		}
		
	//�ھ�value�ӴM��key
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
	
	//�ھ�key�ӴM��value
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
	
	//��@put()��k�A�Ӥ�k�����\value����
	public synchronized V put(K key,V value)
	{
		//�]�M�Ҧ�value�զ������X
		for (V val : valueSet())
		{
			if(val.equals(value) && val.hashCode()==value.hashCode())
			{
				//�p�G�Y��value�M���X����value�ۦP�A�h�ߥX�@��RuntimeException���`
				throw new RuntimeException("MyMap��Ҥ������\������value!");
			}
		}
		return map.put(key, value);
	}
	
	public synchronized int size()
	{
		
		return map.size();
	}
}