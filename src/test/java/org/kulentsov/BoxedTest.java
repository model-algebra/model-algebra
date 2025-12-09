package org.kulentsov;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BoxedTest
{
	@Test
	void testBoxed()
	{
		Boxed<Integer> boxedInt = new Boxed<>(42);
		assertEquals(42, boxedInt.value);

		Boxed<String> boxedString = new Boxed<>("Hello");
		assertEquals("Hello", boxedString.value);
	}
	
	@Test
	void testOf()
	{
		Boxed<Double> boxedDouble = Boxed.of(3.14);
		assertEquals(3.14, boxedDouble.value);

		Boxed<Boolean> boxedBoolean = Boxed.of(true);
		assertEquals(true, boxedBoolean.value);
	}
	
	@Test
	void testModifyingValueFromLambda()
	{
		Boxed<Integer> boxedCounter = Boxed.of(0);

		Runnable incrementer = () ->
		{
			for (int i = 0; i < 5; i++)
				boxedCounter.value++;
		};

		incrementer.run();
		assertEquals(5, boxedCounter.value);
	}
	

}
