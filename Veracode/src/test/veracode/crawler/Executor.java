package test.veracode.crawler;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import test.veracode.crawler.processes.TestWebCrawler;

public class Executor {
	
	public static void main(String[] args) {
	      Result result = JUnitCore.runClasses(TestWebCrawler.class);
			
	      for (Failure failure : result.getFailures()) {
	         System.out.println(failure.toString());
	      }
			
	      System.out.println(result.wasSuccessful());
	}

}
