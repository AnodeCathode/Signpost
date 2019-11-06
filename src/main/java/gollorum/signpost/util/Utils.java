package gollorum.signpost.util;

public class Utils {

	public static <T> T castOrNull(Object obj, Class<T> t) {
		return t.isInstance(obj) ? t.cast(obj) : null;
	}
	
}
