package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import processor.OrdersProcessor;


/**
 * 
 * You need student tests if you are looking for help during office hours about
 * bugs in your code.
 * 
 * @author UMCP CS Department
 *
 */
public class StudentTests {

	@Test
	public void test01pub1() throws FileNotFoundException {
		/* Retrieving the name of the results file */
		
		/* Deleting results file (in case it exists) */

		/* Actual execution of the test by using input redirection and calling 
		/* OrdersProcessor.main(null) */
		TestingSupport.redirectStandardInputTo("pubTest1Results.txt");
		OrdersProcessor.main(null);
	}


}
