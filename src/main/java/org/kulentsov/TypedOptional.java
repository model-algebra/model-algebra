package org.kulentsov;

import java.util.Optional;

public class TypedOptional<T>
{
	private final Class<T> clazz;

	public TypedOptional(Class<T> value)
	{
		this.clazz = value;
	}

	public static <T, V> Optional<T> of(Class<T> clazz, V value)
	{
		return new TypedOptional<>(clazz).of(value);
	}

	public <V> Optional<T> of(V value)
	{
		return clazz.isInstance(value) ? Optional.ofNullable(clazz.cast(value)) : Optional.empty();
	}
}
