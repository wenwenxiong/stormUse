package stormUse.stormUse.java8stream;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

public class StreamUse 
{

	public static void main(String[] args)
	{
		List<Integer> nums = Lists.newArrayList(1,null, 3, 4, null, 6);
		nums.stream().filter(num -> num != null).forEach(System.out::println);
		
		AtomicInteger index = new AtomicInteger();
		Stream.generate(
				new Supplier<Double>() 
				{
					@Override
					public Double get()
					{
						return Math.random();
					}
		
				}
		).limit(10).forEach(element -> System.out.println("index: " + index.getAndIncrement() + " " + element));
		
		Stream.generate(() -> Math.random()).limit(10).forEach(System.out::println);
		
		Stream.generate(Math::random).limit(10).forEach(System.out::println);
	}
}
