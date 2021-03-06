package stormUse.stormUse.java8function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used for
 *
 */
public class PageView<T> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int page;
	private final int pageSize;
	private final long total;
	private final ArrayList<T> entries;

	public static class Builder<T>
	{

		private ArrayList<T> entries;
		private int page;
		private int pageSize;
		private long total;
		private Stream<T> allentries;

		/**
		 * @param elements
		 */
		Builder(ArrayList<T> entries)
		{
			this.entries = entries;
			allentries = null;
		}

		/**
		 * @param entries2
		 * @param all2
		 */
		public Builder(Stream<T> entries2)
		{
			this.allentries = entries2;
			entries = null;
		}

		public static <T> Builder<T> elements(ArrayList<T> entries)
		{
			return new Builder<T>(entries);
		}

		public static <T> Builder<T> allElements(Stream<T> entries)
		{
			return new Builder<T>(entries);
		}

		public Builder<T> page(int page)
		{
			this.page = page;
			return this;
		}

		public Builder<T> pageSize(int pageSize)
		{
			this.pageSize = pageSize;
			return this;
		}

		public Builder<T> total(long total)
		{
			this.total = total;
			return this;
		}

		public PageView<T> build()
		{
			if (allentries != null)
			{
				int skip = (page - 1) > 0 ? (page - 1) * pageSize : 0;
				int limit = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
				List<T> list = allentries.collect(Collectors.toList());
				ArrayList<T> curr =
						list.stream().skip(skip).limit(limit).collect(Collectors.toCollection(ArrayList::new));
				long count = list.size();
				return new PageView<T>(page, pageSize, count, curr);
			}

			return new PageView<T>(page, pageSize, total, entries);
		}
	}

	PageView(int page, int pageSize, long total, ArrayList<T> entries)
	{
		this.page = page;
		this.pageSize = pageSize;
		this.total = total;
		this.entries = entries;
	}

	/**
	 * @return the page
	 */
	public int getPage()
	{
		return page;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * @return the total
	 */
	public long getTotal()
	{
		return total;
	}

	/**
	 * @return the entries
	 */
	public ArrayList<T> getEntries()
	{
		return entries;
	}

	public <R> PageView<R> map(Function<T, R> mapper)
	{
		return PageView.Builder.elements(entries.stream().map(mapper).collect(Collectors.toCollection(ArrayList::new)))
				.page(page).pageSize(pageSize).total(total).build();
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("PageView [page=");
		builder.append(page);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append(", total=");
		builder.append(total);
		builder.append(", entries=");
		builder.append(entries);
		builder.append("]");
		return builder.toString();
	}

}
