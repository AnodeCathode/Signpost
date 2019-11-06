package gollorum.signpost.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class CollectionUtil {
	
	public static <T> Collection<T> where(Collection<T> collection, Predicate<T> condition){
		List<T> ret = new ArrayList<T>();
		for(T t: collection) {
			if(condition.test(t)) {
				ret.add(t);
			}
		}
		return ret;
	}
	
	public static <Key, Value> Map<Key, Value> mutateOr(Map<Key, Value> map, BiPredicate<Key, Value> condition, BiFunction<Key, Value, Value> mutation, BiConsumer<Key, Value> elseAction){
		Map<Key, Value> ret = new HashMap<Key, Value>();
		for(Entry<Key, Value> entry: map.entrySet()) {
			Key key = entry.getKey();
			Value value = entry.getValue();
			if(condition.test(key, value)) {
				ret.put(key, mutation.apply(key, value));
			} else {
				elseAction.accept(key, value);
			}
		}
		return ret;
	}

}
