/**
 *
 */
package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.model.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.Mockito.*;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Test for EnhancedOldBuildDiscarder holdMaxBuilds setting
 * author @BenjaminBeggs
 */

public class EnhancedOldBuildDiscarderTest {

	@Rule
	public JenkinsRule jRule = new JenkinsRule();

	private FreeStyleProject projectInstantiation() throws Exception {
		FreeStyleProject project = jRule.createFreeStyleProject("test");

		FreeStyleBuild build1 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build1);

		FreeStyleBuild build2 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build2);

		FreeStyleBuild build3 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build3);

		FreeStyleBuild build4 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build4);

		FreeStyleBuild build5 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build5);

		FreeStyleBuild build6 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build6);

		FreeStyleBuild build7 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build7);

		FreeStyleBuild build8 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build8);

		FreeStyleBuild build9 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build9);

		FreeStyleBuild build10 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build10);

		return project;
	}

	@Test
	public void testPerformHoldMaxBuildsFirstCnd() throws Exception {
		// testing for circumstance where builds to be discarded
		// are greater in amount than builds present, causing build discard queue to be cleared
		FreeStyleProject p = projectInstantiation();
		Calendar calMock = new GregorianCalendar();
		calMock.add(Calendar.DAY_OF_YEAR,+10);

		EnhancedOldBuildDiscarder publisher = spy(new EnhancedOldBuildDiscarder(
				"10", "20", "", "",
				false, true));

		when(publisher.getCalDaysToKeep(10)).thenReturn(calMock);
		publisher.perform(p);
		List<? extends Run<?,?>> buildList = p.getBuilds();
		assertEquals(10, buildList.size());
	}

	@Test
	public void testPerformHoldMaxBuildsSecondCnd() throws Exception {
		// testing for circumstance where only max build quantity is kept
		// while remaining build history is cleared since it exceeds max age
		FreeStyleProject p = projectInstantiation();
		Calendar calMock = new GregorianCalendar();
		calMock.add(Calendar.DAY_OF_YEAR,+10);

		EnhancedOldBuildDiscarder publisher = spy(new EnhancedOldBuildDiscarder(
				"10", "5", "", "",
				false, true));

		when(publisher.getCalDaysToKeep(10)).thenReturn(calMock);
		publisher.perform(p);
		List<? extends Run<?,?>> buildList = p.getBuilds();
		assertEquals(5, buildList.size());
	}

	@Test
	public void testPerformHoldMaxBuildsThirdCnd() throws Exception {
		// testing for circumstance where no builds are cleared since logs are beneath max age
		// despite exceeding max build quantity, for this reason getCalDaysToKeep(daysToKeep) is not stubbed
		FreeStyleProject p = projectInstantiation();

		EnhancedOldBuildDiscarder publisher = new EnhancedOldBuildDiscarder(
				"10", "5", "", "",
				false, true);

		publisher.perform(p);
		List<? extends Run<?,?>> buildList = p.getBuilds();
		assertEquals(10, buildList.size());
	}

	@Test
	public void testPerformHoldMaxBuildsFourthCnd() throws Exception {
		// testing for circumstance where max builds is 1 and days to keep is 1
		// forcing deletion of all builds except the last successful one
		FreeStyleProject p = projectInstantiation();
		Calendar calMock = new GregorianCalendar();
		calMock.add(Calendar.DAY_OF_YEAR,+1);

		EnhancedOldBuildDiscarder publisher = spy(new EnhancedOldBuildDiscarder(
				"1", "1", "", "",
				false, true));

		when(publisher.getCalDaysToKeep(1)).thenReturn(calMock);
		publisher.perform(p);
		List<? extends Run<?,?>> buildList = p.getBuilds();
		assertEquals(1, buildList.size());
	}
}
