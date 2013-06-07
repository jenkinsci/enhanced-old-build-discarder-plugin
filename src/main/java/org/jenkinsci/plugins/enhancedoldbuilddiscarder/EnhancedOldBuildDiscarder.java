package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.Job;
import hudson.model.Run;
import hudson.tasks.LogRotator;

import java.io.IOException;

import jenkins.model.BuildDiscarderDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class EnhancedOldBuildDiscarder extends LogRotator {
	
	private boolean discardOnlyOnSuccess;

	@DataBoundConstructor
    public EnhancedOldBuildDiscarder (
    		String daysToKeepStr, 
    		String numToKeepStr, 
    		String artifactDaysToKeepStr, 
    		String artifactNumToKeepStr,
    		boolean discardOnlyOnSuccess) {
        this (parse(daysToKeepStr),
        	   parse(numToKeepStr),
               parse(artifactDaysToKeepStr),
               parse(artifactNumToKeepStr),
               discardOnlyOnSuccess);
    }
	
	public EnhancedOldBuildDiscarder(
			int daysToKeep, 
			int numToKeep, 
			int artifactDaysToKeep, 
			int artifactNumToKeep,
			boolean discardOnlyWhenLastBuildIsSuccess) {
		super(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep);
		this.setDiscardOnlyOnSuccess(discardOnlyWhenLastBuildIsSuccess);
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

	public boolean isDiscardOnlyOnSuccess() {
		return discardOnlyOnSuccess;
	}

	public void setDiscardOnlyOnSuccess(boolean discardOnlyOnSuccess) {
		this.discardOnlyOnSuccess = discardOnlyOnSuccess;
	}

	@Extension
    public static final class EnhancedOldBuildDiscarderDescriptor extends BuildDiscarderDescriptor {
        public String getDisplayName() {
            return "Enhanced Log Rotation";
        }
    }
}
