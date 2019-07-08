/**
 *
 */
package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.model.*;
import hudson.util.RunList;
import junit.framework.TestCase;

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

	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public FreeStyleBuild buildHMS = mock(FreeStyleBuild.class);
	public FreeStyleProject jobHMS = mock(FreeStyleProject.class);
	public List<FreeStyleBuild> buildListHMS = new ArrayList<FreeStyleBuild>(); // buildList used to test feature conditions

	public void setUp() throws Exception {
		// instantiates hold max build relevant histories
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

		when(jobHMS.getBuilds()).thenReturn(RunList.fromRuns(buildListHMS));
		when(buildHMS.getParent()).thenReturn(jobHMS);
	}

	public void testPerformHoldMaxBuildsFirstCnd() throws Exception {
		// testing for circumstance where builds to be discarded
		// are greater in amount than builds present, causing build discard queue to be cleared
		EnhancedOldBuildDiscarder publisher = (new EnhancedOldBuildDiscarder(
				"1", "20", "", "",
				false, true));
		// emulates build and plugin operation
		publisher.perform(jobHMS);
		for (int i = 0; i < 10; i++) {
			verify(buildListHMS.get(i), never()).delete();
		}
	}

	public void testPerformHoldMaxBuildsSecondCnd() throws Exception {
		// testing for circumstance where only max build quantity is kept
		// while remaining build history is cleared since it exceeds max age
		EnhancedOldBuildDiscarder publisher = (new EnhancedOldBuildDiscarder(
				"10", "5", "", "",
				false, true));

		publisher.perform(jobHMS);

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
		EnhancedOldBuildDiscarder publisher = (new EnhancedOldBuildDiscarder(
				"100000", "5", "", "",
				false, true));

		publisher.perform(jobHMS);

		for (int i = 0; i < 11; i++) {
			verify(buildListHMS.get(i), never()).delete();
		}
	}

	public void testPerformHoldMaxBuildsFourthCnd() throws Exception {
		// testing for circumstance where max builds is set to 0 and days to keep is not
		// forcing deletion of all builds exceeding age, with no effect from max build quantity
		EnhancedOldBuildDiscarder publisher = (new EnhancedOldBuildDiscarder(
				"10", "", "", "",
				false, true));

		publisher.perform(jobHMS);

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
}
