package org.kulentsov;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Coalesce
{
	private Coalesce() {}
	
	@SafeVarargs
	public static <T> Optional<T> of(T... item)
	{
		return Arrays.stream(item).filter(Objects::nonNull).findFirst();
	}
	
	@SafeVarargs
	public static <T> Optional<T> of(Supplier<Optional<T>>... item)
	{
		return Arrays.stream(item).filter(Objects::nonNull).map(Supplier::get).filter(Objects::nonNull).filter(Optional::isPresent).findFirst().flatMap(Function.identity());
	}

	@SafeVarargs
	public static <T> Optional<T> ofOptionals(Optional<T>... item)
	{
		return Arrays.stream(item).filter(Objects::nonNull).filter(Optional::isPresent).findFirst().flatMap(Function.identity());
	}

}
