package stormUse.stormUse.java8function;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

public class ConsumerTest 
{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args)
	{
		Consumer consumer = ConsumerTest::printNames;
		
		consumer.accept("consumer: xiongwenwen");
		
		List<String> names = Lists.newArrayList("David", "Sam", "Ben");
		names.stream().forEach(consumer);
		
		names.stream().forEach( (x) -> { printNames(() -> x); });
		
		Optional.ofNullable(names).map(Stream::of).orElseGet(Stream::empty).forEach(consumer); 
	}

	private static  <T> void printNames(T name)
	{
		System.out.println(name);
		
	}
	
	private static void printNames(Supplier<String> supplier)
	{
		System.out.println(supplier.get());
	}
}
