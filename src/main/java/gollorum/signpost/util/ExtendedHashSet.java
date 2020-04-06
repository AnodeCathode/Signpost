package gollorum.signpost.util;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class ExtendedHashSet<T> extends HashSet<T> {

	public <S> ExtendedHashSet<S> select(Function<T, S> mapping){
		ExtendedHashSet<S> ret = new ExtendedHashSet<S>();
		for(T t: this) {
			ret.add(mapping.apply(t));
		}
		return ret;
	}
	public ExtendedHashSet<T> where(Predicate<T> condition){
		ExtendedHashSet<T> ret = new ExtendedHashSet<T>();
		for(T t: this)
			if(condition.test(t)) 
				ret.add(t);
		return ret;
	}
	
}
