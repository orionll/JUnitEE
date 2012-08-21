/*
 * $Id: TestRunner.java,v 1.1.1.1 2007-07-13 23:45:16 martinfr62 Exp $
 *
 * (c) 2002 Oliver Rossmueller
 *
 *
 */

package org.junitee.runner;

import java.util.ArrayList;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

// import junit.runner.BaseTestRunner;
// import junit.runner.TestSuiteLoader;
// import junit.framework.*;

/**
 * This is the JUnitEE testrunner.
 * 
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1.1.1 $
 * @since 1.5
 */
public class TestRunner extends RunListener {

	private TestSuiteLoader loader;

	private TestRunnerResults listener;

	private boolean forkThread;

	private JUnitCore juc;

	/**
	 * Create a new instance and set the classloader to be used to load test
	 * classes.
	 * 
	 * @param loader
	 *            classloader to load test classes
	 * @param listener
	 *            test listener to be notfied
	 */
	public TestRunner(ClassLoader loader, TestRunnerResults listener,
			boolean forkThread) {
		this.listener = listener;
		this.loader = new org.junitee.runner.TestSuiteLoader(loader);
		this.forkThread = forkThread;
		juc = new JUnitCore();
		juc.addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
	 */
	@Override
	public void testFailure(Failure failure) throws Exception {

		listener.addFailure(failure.getDescription(), failure);
		System.out.println("testFailure " + failure.toString());
		super.testFailure(failure);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.notification.RunListener#testFinished(org.junit.runner.Description)
	 */
	@Override
	public void testFinished(Description description) throws Exception {

		listener.endTest(description);
		System.out.println("testFinished " + description.toString());
		super.testFinished(description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.notification.RunListener#testIgnored(org.junit.runner.Description)
	 */
	@Override
	public void testIgnored(Description description) throws Exception {

		System.out.println("testIgnored " + description.toString());
		super.testIgnored(description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.notification.RunListener#testRunFinished(org.junit.runner.Result)
	 */
	@Override
	public void testRunFinished(Result result) throws Exception {

		listener.finish();
		System.out.println("testRunFinished " + result.toString());
		super.testRunFinished(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.notification.RunListener#testRunStarted(org.junit.runner.Description)
	 */
	@Override
	public void testRunStarted(Description description) throws Exception {

		super.testRunStarted(description);
		listener.start(false);
		if (description.testCount() == 1) {
			listener.start(true);
		}
		if (description.isSuite()) {
			listener.testRunStarted(description);
		}
		System.out.println("testRunStarted " + description.toString());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.notification.RunListener#testStarted(org.junit.runner.Description)
	 */
	@Override
	public void testStarted(Description description) throws Exception {

		super.testStarted(description);
		listener.startTest(description);
		System.out.println("testStarted " + description.toString());
	}

	public void stop() {
		// notify the listener immediatley so we can display this information
		listener.setStopped();
		System.out.println("sto");
	}

	/**
	 * Run all tests in the given test classes.
	 * 
	 * @param testClassNames
	 *            names of the test classes
	 */
	public void run(final String[] testClassNames) {
		Runnable runnable = new Runnable() {

			public void run() {

				listener.start(false);

				for (int i = 0; i < testClassNames.length; i++) {

					Class clazz = null;
					;
					try {
						clazz = this.getClass().getClassLoader().loadClass(
								testClassNames[i]);
					} catch (ClassNotFoundException e1) {

						e1.printStackTrace();
					}

					if (clazz != null) {
						Request req = Request.aClass(clazz);

						juc.run(req);
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// back to work
				}

				listener.finish();
			}
		};
		if (forkThread) {
			Thread thread = new Thread(runnable, this.toString());
			thread.start();
		} else {
			runnable.run();
		}
	}

	public void run(final String testClassName, final String testName) {

		final Filter filter = new Filter() {

			@Override
			public String describe() {

				return "Only Run " + testClassName + " test " + testName;
			}

			@Override
			public boolean shouldRun(Description description) {
				if (description.getDisplayName().equals(testName))
					return true;
				return false;
			}

		};

		Runnable runnable = new Runnable() {

			public void run() {

				Request runRequest = null;

				Class clazz = null;
				;
				try {
					clazz = this.getClass().getClassLoader().loadClass(
							testClassName);
				} catch (ClassNotFoundException e1) {

					e1.printStackTrace();
				}

				if (clazz != null) {
					Request req = Request.aClass(clazz);

					runRequest = req.filterWith(filter);

				}
				listener.start(false);

				if (runRequest != null) {
					juc.run(runRequest);
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// back to work
				}

				listener.finish();
			}
		};
		if (forkThread) {
			Thread thread = new Thread(runnable, this.toString());
			thread.start();
		} else {
			runnable.run();
		}

	}

	public TestSuiteLoader getLoader() {
		return loader;
	}

	// protected void runFailed(String className) {
	// listener.runFailed(className);
	// System.out.println("runFailed "+className);
	// }

}
