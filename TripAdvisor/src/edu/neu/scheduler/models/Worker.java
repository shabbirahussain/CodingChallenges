package edu.neu.scheduler.models;

public class Worker{
	public String name;
	
	/**
	 * Default Constructor
	 * @param name is the name od the worker
	 */
	public Worker(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
}