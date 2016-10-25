package edu.neu.scheduler.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.AbstractMap.SimpleEntry;

public class AllocationTable{
	private Queue<MyEntry> allocHist;
	
	private Map<Worker, JobInstance> fwdMap;
	private Map<JobInstance, Worker> revMap;
	
	private class MyEntry extends SimpleEntry<Worker, JobInstance> implements Comparable<MyEntry>{
		private static final long serialVersionUID = 1L;

		public MyEntry(Entry<? extends Worker, ? extends JobInstance> entry) {
			super(entry);
		}

		public MyEntry(Worker w, JobInstance jobInstance) {
			super(w, jobInstance);
		}

		@Override
		public int compareTo(MyEntry o) {
			return this.getValue().start.compareTo(o.getValue().start);
		}
	}; 
	/**
	 * Default Constructor
	 */
	public AllocationTable(){
		allocHist = new PriorityQueue<MyEntry>();
		fwdMap    = new HashMap<>();
		revMap    = new HashMap<>();
	}
	
	/**
	 * Archives an entries and marks it as done
	 * @param w is the worker entry to archive
	 */
	public void archiveEntry(Worker w){
		MyEntry e = new MyEntry(w, new JobInstance(fwdMap.get(w)));
		JobInstance ji = e.getValue();
		if(ji==null) return;
		
		allocHist.add(e);
		fwdMap.remove(w);
		revMap.remove(ji);
	}
	
	/**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public JobInstance put(Worker key, JobInstance value) {
    	if(fwdMap.get(key)==null){
    		revMap.put(value, key);
			fwdMap.put(key, value);
    	}else{
    		if(fwdMap.get(key).job.getID().compareTo(value.job.getID())!=0){
    			this.archiveEntry(key);
    			revMap.put(value, key);
        		fwdMap.put(key, value);
    		}
    	}
    	
        return value;
    }
    
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public Worker put(JobInstance key, Worker value) {
    	if(fwdMap.get(value)==null){
    		fwdMap.put(value, key);
    		revMap.put(key, value);
    	}else{
    		if(fwdMap.get(value).job.getID().compareTo(key.job.getID())!=0){
    			this.archiveEntry(value);
        		fwdMap.put(value, key);
        		revMap.put(key, value);
    		}
    	}
        return value;
    }
    
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @see #put(Object, Object)
     */
    public Worker get(JobInstance key) {
        return revMap.get(key);
    }
    
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @see #put(Object, Object)
     */
    public JobInstance get(Worker key) {
        return fwdMap.get(key);
    }
 
    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this map
     */
    public int size(){
    	return fwdMap.size();
    }
    
    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty(){
    	return (fwdMap.size()==0);
    }
    
    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    public Set<Map.Entry<Worker, JobInstance>> entrySet(){
    	return fwdMap.entrySet();
    }

    
	public String toString(){
		Queue<MyEntry> tmpQ = new PriorityQueue<>(this.allocHist);
		for(Entry<Worker, JobInstance> e: this.fwdMap.entrySet())
			tmpQ.add(new MyEntry(e));

		StringBuilder sb = new StringBuilder();
		while(!tmpQ.isEmpty())
			sb.append(this.toString(tmpQ.poll()));
		
		return sb.toString();
	}
	
	/**
	 * Given a collection of entries converters them to output format
	 * @param lst is the list of entries to convert
	 * @return A string containing converted version of list entries
	 */
	private String toString(MyEntry e){
		StringBuilder sb = new StringBuilder();
		sb.append(e.getValue().start);
		sb.append(" ");
		sb.append(e.getKey().toString());
		sb.append(" ");
		sb.append(e.getValue().toString());
		sb.append("\n");
		return sb.toString();
	}
}
