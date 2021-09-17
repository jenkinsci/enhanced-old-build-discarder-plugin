package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import com.google.common.collect.Lists;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.Job;
import hudson.model.Run;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;
import static java.util.logging.Level.*;

import jenkins.model.BuildDiscarder;
import jenkins.model.BuildDiscarderDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class EnhancedOldBuildDiscarder extends ModifiedLogRotator {
	private boolean discardOnlyOnSuccess;

	@DataBoundConstructor
	public EnhancedOldBuildDiscarder (
    		String daysToKeepStr,
    		String numToKeepStr,
    		String artifactDaysToKeepStr,
    		String artifactNumToKeepStr,
    		boolean discardOnlyOnSuccess,
    		boolean holdMaxBuilds) {
        this (parse(daysToKeepStr),
    		parse(numToKeepStr),
    		parse(artifactDaysToKeepStr),
    		parse(artifactNumToKeepStr),
    		discardOnlyOnSuccess,
    		holdMaxBuilds);
    }

    	public EnhancedOldBuildDiscarder(
    		int daysToKeep,
    		int numToKeep,
    		int artifactDaysToKeep,
    		int artifactNumToKeep,
    		boolean discardOnlyWhenLastBuildIsSuccess,
    		boolean discardHoldMaxBuilds) {
        super(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep);
        this.setDiscardOnlyOnSuccess(discardOnlyWhenLastBuildIsSuccess);
        this.setHoldMaxBuilds(discardHoldMaxBuilds);
	}
	
	@Override
	public void perform(Job<?, ?> job) throws IOException, InterruptedException {
		if (discardOnlyOnSuccess && lastBuildWasntStable(job))
			return;
		super.perform(job);
	}

	private boolean lastBuildWasntStable(Job<?, ?> job) {
		Run<?, ?> lastBuild = job.getLastBuild();
		if (lastBuild == null)
			return true;
		
		if (lastBuild.isBuilding())
			return true;
		
		if (lastBuild.hasntStartedYet())
			return true;
		
		return !lastBuild.getResult().equals(Result.SUCCESS);
	}

	public int getDaysToKeep() {
		return daysToKeep;
	}

	public int getNumToKeep() {
		return numToKeep;
	}

	public int getArtifactDaysToKeep() {
		return unbox(artifactDaysToKeep);
	}

	public int getArtifactNumToKeep() {
		return unbox(artifactNumToKeep);
	}

	public String getDaysToKeepStr() {
		return toString(daysToKeep);
	}

	public String getNumToKeepStr() {
		return toString(numToKeep);
	}

	public String getArtifactDaysToKeepStr() {
		return toString(artifactDaysToKeep);
	}

	public String getArtifactNumToKeepStr() {
		return toString(artifactNumToKeep);
	}

	public boolean isDiscardOnlyOnSuccess() {
		return discardOnlyOnSuccess;
	}

	public boolean isHoldMaxBuilds() {
		return holdMaxBuilds;
	}

	public void setDiscardOnlyOnSuccess(boolean discardOnlyOnSuccess) {
		this.discardOnlyOnSuccess = discardOnlyOnSuccess;
	}

	public void setHoldMaxBuilds(boolean holdMaxBuilds) {
		this.holdMaxBuilds = holdMaxBuilds;
	}

	public Boolean isKeepLastBuild() {
		return super.isKeepLastBuild();
	}
	@Extension
	public static final class EnhancedOldBuildDiscarderDescriptor extends BuildDiscarderDescriptor {
		public String getDisplayName() {
			return "Enhanced Log Rotator";
		}
	}
}

/**
 * Default implementation of {@link BuildDiscarder}.
 *
 * For historical reason, this is called LogRotator, but it does not rotate logs :-)
 *
 * Since 1.350 it has also the option to keep the build, but delete its recorded artifacts.
 *
 * @author Kohsuke Kawaguchi (modifications by Benjamin Beggs)
 */
class ModifiedLogRotator extends BuildDiscarder {

	/**
	 * If not -1, history is only kept up to this days.
	 */
	public int daysToKeep;

	/**
	 * If not -1, only this number of build logs are kept.
	 */
	public int numToKeep;

	/**
	 * If not -1 nor null, artifacts are only kept up to this days.
	 * Null handling is necessary to remain data compatible with old versions.
	 * @since 1.350
	 */
	public Integer artifactDaysToKeep;

	/**
	 * If not -1 nor null, only this number of builds have their artifacts kept.
	 * Null handling is necessary to remain data compatible with old versions.
	 * @since 1.350
	 */
	public Integer artifactNumToKeep;

	// Boolean used to record activation of "require both conditions to be met..." feature on the UI
	public boolean holdMaxBuilds;

	/**
	 * Boolean used to always keep the last successful and the last stable build. 
	 * feature on UI as checkbox. 
	 */
	@CheckForNull
	private Boolean keepLastBuild;

	@DataBoundConstructor
	public ModifiedLogRotator (
		  String daysToKeepStr, 
		  String numToKeepStr, 
		  String artifactDaysToKeepStr, 
		  String artifactNumToKeepStr) {
		  this (parse(daysToKeepStr),
		  parse(numToKeepStr),
		  parse(artifactDaysToKeepStr),
		  parse(artifactNumToKeepStr));
	}

	public static int parse(String p) {
		if(p==null)     return -1;
		try {
			return Integer.parseInt(p);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	@Nonnull
	public Boolean isKeepLastBuild() {
        return (keepLastBuild == null) ? true : false;
    }

    @DataBoundSetter 
	public void setKeepLastBuild(@Nonnull Boolean keepLastBuild) {
        this.keepLastBuild = keepLastBuild ? null : false;
	}

	/**
	 * @deprecated since 1.350.
	 *      Use {@link #ModifiedLogRotator(int, int, int, int)}
	 */
	@Deprecated
	public ModifiedLogRotator(int daysToKeep, int numToKeep) {
		this(daysToKeep, numToKeep, -1, -1);
	}

	public ModifiedLogRotator(int daysToKeep, int numToKeep, int artifactDaysToKeep, int artifactNumToKeep) {
		this.daysToKeep = daysToKeep;
		this.numToKeep = numToKeep;
		this.artifactDaysToKeep = artifactDaysToKeep;
		this.artifactNumToKeep = artifactNumToKeep;
	}

	@SuppressWarnings("rawtypes")
	public void perform(Job<?,?> job) throws IOException, InterruptedException {
		LOGGER.log(FINE, "Running the log rotation for {0} with numToKeep={1} daysToKeep={2} artifactNumToKeep={3} artifactDaysToKeep={4}", new Object[] {job, numToKeep, daysToKeep, artifactNumToKeep, artifactDaysToKeep});

		// always keep the last successful and the last stable builds
		Run lsb = job.getLastSuccessfulBuild();
		Run lstb = job.getLastStableBuild();

		// Requires both age and build quantity conditions be met prior to build discard
		if((daysToKeep!=-1) && (numToKeep!=-1) && (holdMaxBuilds)) {
			int newCntr = 0;
			Calendar cal = getCalDaysToKeep(daysToKeep);
			Run r = job.getFirstBuild();
			while (r != null) {
				List<? extends Run<?,?>> builds = job.getBuilds();
				if (tooNew(r, cal)) {
					newCntr++;
					break;
				}
				if (!shouldKeepRun(r, lsb, lstb)&&((builds.size()-newCntr)>numToKeep)) {
					r.delete();
				}
				r = r.getNextBuild();
			}
		}

		else if(numToKeep!=-1) {
			// Note that RunList.size is deprecated, and indeed here we are loading all the builds of the job.
			// However we would need to load the first numToKeep anyway, just to skip over them;
			// and we would need to load the rest anyway, to delete them.
			// (Using RunMap.headMap would not suffice, since we do not know if some recent builds have been deleted for other reasons,
			// so simply subtracting numToKeep from the currently last build number might cause us to delete too many.)
			List<? extends Run<?,?>> builds = job.getBuilds();
			for (Run r : copy(builds.subList(Math.min(builds.size(), numToKeep), builds.size()))) {
				if (shouldKeepRun(r, lsb, lstb)) {
					continue;
				}
				LOGGER.log(FINE, "{0} is to be removed", r);
				r.delete();
			}
		}

		else if(daysToKeep!=-1) {
			Calendar cal = getCalDaysToKeep(daysToKeep);
			Run r = job.getFirstBuild();
			while (r != null) {
				if (tooNew(r, cal)) {
					break;
				}
				if (!shouldKeepRun(r, lsb, lstb)) {
					LOGGER.log(FINE, "{0} is to be removed", r);
					r.delete();
				}
				r = r.getNextBuild();
			}
		}

		if(artifactNumToKeep!=null && artifactNumToKeep!=-1) {
			List<? extends Run<?,?>> builds = job.getBuilds();
			for (Run r : copy(builds.subList(Math.min(builds.size(), artifactNumToKeep), builds.size()))) {
				if (shouldKeepRun(r, lsb, lstb)) {
					continue;
				}
				LOGGER.log(FINE, "{0} is to be purged of artifacts", r);
				r.deleteArtifacts();
			}
		}

		if(artifactDaysToKeep!=null && artifactDaysToKeep!=-1) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_YEAR,-artifactDaysToKeep);
			Run r = job.getFirstBuild();
			while (r != null) {
				if (tooNew(r, cal)) {
					break;
				}
				if (!shouldKeepRun(r, lsb, lstb)) {
					LOGGER.log(FINE, "{0} is to be purged of artifacts", r);
					r.deleteArtifacts();
				}
				r = r.getNextBuild();
			}
		}


	}

	private boolean shouldKeepRun(Run r, Run lsb, Run lstb) {
		if (r.isKeepLog()) {
			LOGGER.log(FINER, "{0} is not to be removed or purged of artifacts because it’s marked as a keeper", r);
			return true;
		}
		if (r == lsb && isKeepLastBuild()) {
			LOGGER.log(FINER, "{0} is not to be removed or purged of artifacts because it’s the last successful build", r);
			return true;
		}
		if (r == lstb && isKeepLastBuild()) {
			LOGGER.log(FINER, "{0} is not to be removed or purged of artifacts because it’s the last stable build", r);
			return true;
		}
		if (r.isBuilding()) {
			LOGGER.log(FINER, "{0} is not to be removed or purged of artifacts because it’s still building", r);
			return true;
		}
		return false;
	}

	private boolean tooNew(Run r, Calendar cal) {
		if (!r.getTimestamp().before(cal)) {
			LOGGER.log(FINER, "{0} is not to be removed or purged of artifacts because it’s still new", r);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates a copy since we'll be deleting some entries from them.
	 */
	private <R> Collection<R> copy(Iterable<R> src) {
		return Lists.newArrayList(src);
	}

	public Calendar getCalDaysToKeep(int daysToKeep) {
		Calendar calDays = new GregorianCalendar();
		calDays.add(Calendar.DAY_OF_YEAR,-daysToKeep);
		return calDays;
	}

	public int unbox(Integer i) {
		return i==null ? -1: i;
	}

	public String toString(Integer i) {
		if (i==null || i==-1)   return "";
		return String.valueOf(i);
	}

	private static final Logger LOGGER = Logger.getLogger(ModifiedLogRotator.class.getName());

}
