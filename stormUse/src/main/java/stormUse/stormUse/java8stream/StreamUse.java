package stormUse.stormUse.java8stream;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

public class StreamUse 
{

	public static void main(String[] args)
	{
		/*List<Integer> nums = Lists.newArrayList(1,null, 3, 4, null, 6);
		nums.stream().filter(num -> num != null).forEach(System.out::println);*/
		
		/*AtomicInteger index = new AtomicInteger();
		Stream.generate(
				new Supplier<Double>() 
				{
					@Override
					public Double get()
					{
						return Math.random();
					}
		
				}
		).limit(10).forEach(element -> System.out.println("index: " + index.getAndIncrement() + " " + element));*/
		
		//Stream.generate(() -> Math.random()).limit(10).forEach(System.out::println);
		
		//Stream.generate(Math::random).limit(10).forEach(System.out::println);
		
		/*List<String> strings = Arrays.asList("a", "b", " ", "", "c", "d", "e", "f", "g");
		strings.stream().map(str -> str.trim()).filter(str -> !str.isEmpty()).forEach(StreamUse::printNames);*/
		//List<String> filterStrings = strings.stream().map(str -> str.trim()).filter(str -> !str.isEmpty()).collect(Collectors.toList());
		//printNames(filterStrings);
		
		/*Random random = new Random();
		random.ints().limit(10).sorted().forEach(System.out::println);*/
		
		List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
		IntSummaryStatistics statistics = numbers.stream().mapToInt((x) -> x).summaryStatistics();
		System.out.println("Highest number in List : " + statistics.getMax());
		System.out.println("Lowest number in List : " + statistics.getMin());
		System.out.println("Sum of all numbers : " + statistics.getSum());
		System.out.println("Average of all numbers : " + statistics.getAverage());
	}
	
	private static <T> void printNames( T name)
	{
		System.out.print(name);
	}
}
