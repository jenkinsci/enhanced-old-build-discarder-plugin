/**
 *
 */
package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.Launcher;
import hudson.model.*;
import hudson.util.RunList;
import junit.framework.TestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test for EnhancedOldBuildDiscarder holdMaxBuilds setting
 * author @BenjaminBeggs
 * method largely ported from Discard Old Builds plugin (authors @tamagawahiroko & @BenjaminBeggs)
 */

public class EnhancedOldBuildDiscarderTest extends TestCase {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private Launcher launcher = mock(Launcher.class);
	private PrintStream logger = mock(PrintStream.class);
	private BuildListener listener = mock(BuildListener.class);
	private FreeStyleBuild buildHMS = mock(FreeStyleBuild.class);
	private FreeStyleProject jobHMS = mock(FreeStyleProject.class);
	private List<FreeStyleBuild> buildListHMS = new ArrayList<FreeStyleBuild>(); // buildList used to test specific hold max build feature conditions

	public void setUp() throws Exception {
		// instantiates hold max build specific histories
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120110", true)); // #10
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120110")); // #10
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120109")); // #9
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120108")); // #8
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120107")); // #7
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120106")); // #6
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120105")); // #5
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120104")); // #4
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120103")); // #3
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120102")); // #2
		buildListHMS.add(createBuild(jobHMS, Result.SUCCESS, "20120101")); // #1

		when(listener.getLogger()).thenReturn(logger);
		when(jobHMS.getBuilds()).thenReturn(RunList.fromRuns(buildListHMS));
		when(buildHMS.getParent()).thenReturn(jobHMS);
	}

	public void testPerformHoldMaxBuildsFirstCnd() throws Exception {
		// testing for circumstance where builds to be discarded
		// are greater in amount than builds present, causing build discard queue to be cleared
		EnhancedOldBuildDiscarder publisher = getPublisher(new EnhancedOldBuildDiscarder(
				"10", "20", "", "",
				false, true));

		// emulates build data and post-build plugin operation
		publisher.perform(buildListHMS);

		for (int i = 0; i < 10; i++) {
			verify(buildListHMS.get(i), never()).delete();
		}
	}

	public void testPerformHoldMaxBuildsSecondCnd() throws Exception {
		// testing for circumstance where only max build quantity is kept
		// while remaining build history is cleared since it exceeds max age
		EnhancedOldBuildDiscarder publisher = getPublisher(new EnhancedOldBuildDiscarder(
				"10", "5", "", "",
				false, true);

		publisher.perform((AbstractBuild<?, ?>) buildHMS, launcher, listener);

		for (int i = 0; i < 5; i++) {
			verify(buildListHMS.get(i), never()).delete();
		}
		for (int i = 6; i < 11; i++) {
			verify(buildListHMS.get(i), times(1)).delete();
		}
	}

	public void testPerformHoldMaxBuildsThirdCnd() throws Exception {
		// testing for circumstance where no builds are cleared since logs are beneath max age
		// despite exceeding max build quantity
		EnhancedOldBuildDiscarder publisher = getPublisher(new EnhancedOldBuildDiscarder(
				"100000", "5", "", "",
				false, true));

		publisher.perform((AbstractBuild<?, ?>) buildHMS, launcher, listener);

		for (int i = 0; i < 11; i++) {
			verify(buildListHMS.get(i), never()).delete();
		}
	}

	public void testPerformHoldMaxBuildsFourthCnd() throws Exception {
		// testing for circumstance where max builds is set to 0 and days to keep is not
		// forcing deletion of all builds exceeding age, with no effect from max build quantity
		EnhancedOldBuildDiscarder publisher = getPublisher(new EnhancedOldBuildDiscarder(
				"10", "0", "", "",
				false, true);

		publisher.perform((AbstractBuild<?, ?>) buildHMS, launcher, listener);

		for (int i = 0; i < 11; i++) {
			verify(buildListHMS.get(i), times(1)).delete();
		}
	}

    private FreeStyleBuild createBuild(FreeStyleProject project, Result result, String yyyymmdd) throws Exception {
        return createBuild(project, result, yyyymmdd, false);
    }

	private FreeStyleBuild createBuild(FreeStyleProject project, Result result, String yyyymmdd, boolean building) throws Exception {
		FreeStyleBuild build = spy(new FreeStyleBuild(project));

		when(build.getResult()).thenReturn(result);
        when(build.isBuilding()).thenReturn(building);
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(yyyymmdd));
		when(build.getTimestamp()).thenReturn(cal);
		doNothing().when(build).delete();

		return build;
	}

	private EnhancedOldBuildDiscarder getPublisher(EnhancedOldBuildDiscarder publisher) throws Exception {
		EnhancedOldBuildDiscarder spy = spy(publisher);

		when(spy.getCurrentCalendar()).thenAnswer(new Answer() {
		     public Object answer(InvocationOnMock invocation) {
		    	 Calendar cal = Calendar.getInstance();
		 		try {
					cal.setTime(sdf.parse("20130120"));
				} catch (ParseException e) {
					new RuntimeException(e);
				}
		 		return cal;
		     }
		 });
		return spy;
	}

// creates fake clock listing to test build age discard conditions
	private Calendar createCalendar() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse("20130120"));
		return cal;
	}
}
