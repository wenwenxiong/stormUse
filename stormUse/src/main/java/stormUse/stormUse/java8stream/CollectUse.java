package stormUse.stormUse.java8stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CollectUse
{
	public static void main( String[] args) throws IOException
	{
		Boolean debug = false;
		DebugPrint debugPrint = new DebugPrint(debug);
		
		List<Person> persons = Arrays.asList(
				new Person("Max", 18),
				new Person("Peter", 23),
				new Person("Pamela", 23),
				new Person("David", 12));
		
		//stream to list
		List<Person> filterPersons = persons.stream().filter( p -> p.age > 18 ).collect(Collectors.toList());
		debugPrint.print(debug, filterPersons);
		
		//stream to set
		Set<Person> Persons = persons.stream().filter( p -> p.name.startsWith("P")).collect(Collectors.toSet());
		debugPrint.print(debug, Persons);		
		
		//stream to map
		Map<String, Person> personsMap = persons.stream().collect(Collectors.toMap(Person::getName, p -> p));
		Map<String, List<Person>> personMaps = persons.stream().collect(Collectors.groupingBy(Person::getName));
		debugPrint.print(debug, personsMap);
		debugPrint.print(debug, personMaps);
		
		//collect join
		Collector<Person, StringJoiner, String> personNameCollector = Collector.of(
				() -> new StringJoiner(" | "),
				(j, p) -> j.add(p.name.toUpperCase()),
				(j1, j2) -> j1.merge(j2), 
				StringJoiner::toString);
		String names = persons.stream().collect(personNameCollector);
		debugPrint.print(debug, names);
		
		//flatMap 
		List<Teacher> teachers = new ArrayList<Teacher>();
		IntStream.range(1, 4).forEach(i -> teachers.add(new Teacher("teacher" + i)));
		teachers.forEach(f -> IntStream.range(1, 4).forEach(i -> f.students.add(new Student("student" + i + " <- " + f.name))));
		teachers.stream().flatMap(t -> t.students.stream()).forEach(s -> debugPrint.print(false, s.name));
		
		//mapToObject
		IntStream.range(1, 4).mapToObj(i -> new Teacher("teacher" + i))
		.peek(f -> IntStream.range(1, 4)
				.mapToObj(i -> new Student("student"+i+f.name)).forEach(f.students::add)).flatMap(f -> f.students.stream())
				.forEach(s -> debugPrint.print(false, s.name));
		
		//reduce
		persons.stream().reduce((p1, p2) -> p1.age > p2.age  ? p1 : p2).ifPresent(System.out::println);
		Person result = persons.stream().reduce(new Person("Max", 0), (p1, p2) -> { p1.age += p2.age; p1.name += p2.name; return p1;});
		//System.out.format("name=%s; age=%s", result.name, result.age);
		
		//parallel streams
		ForkJoinPool commonPool = ForkJoinPool.commonPool();
		System.out.println(commonPool.getParallelism());
		Arrays.asList("a1", "a2", "b1", "c2", "c1").parallelStream().filter(s -> 
		{System.out.format("filter: %s [%s]\n", s, Thread.currentThread().getName()); return true;})
		.map(s -> {System.out.format("map: %s [%s]\n", s, Thread.currentThread().getName()); return s.toUpperCase();})
		.forEach(s -> System.out.format("foreach: %s [%s]\n", s, Thread.currentThread().getName()));
		
		/* easy wrong */
		IntStream.iterate(0, i -> i +1).limit(10).skip(5).forEach(i -> debugPrint.print(false, i));
		IntStream.iterate(0, i -> i +1).skip(5).limit(5).forEach(i -> debugPrint.print(false, i));
		
		Files.walk(Paths.get(".")).filter(p -> !p.toFile().getName().startsWith(".")).forEach(o -> debugPrint.print(false, o));
		debugPrint.print(true, "------------------------");
		Files.walk(Paths.get(".")).filter(p -> !p.toString().contains(File.separator + ".")).forEach(i -> debugPrint.print(false, i));
		
		List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toCollection(ArrayList::new));
		//list.stream().peek(list::remove).forEach(i -> debugPrint.print(true, i));  //error
		list.stream().sorted().peek(list::remove).forEach(i -> debugPrint.print(true, i));
		
	}

}
