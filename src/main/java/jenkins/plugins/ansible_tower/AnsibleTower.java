package jenkins.plugins.ansible_tower;

import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkins.plugins.ansible_tower.util.TowerInstallation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Janario Oliveira
 */
public class AnsibleTower extends Builder {

	private @Nonnull String towerServer     = DescriptorImpl.towerServer;
	private @Nonnull String jobTemplate     = DescriptorImpl.jobTemplate;
	private String extraVars                = DescriptorImpl.extraVars;
	private String jobTags                  = DescriptorImpl.jobTags;
    private String limit                    = DescriptorImpl.limit;
    private String inventory                = DescriptorImpl.inventory;
    private String credential               = DescriptorImpl.credential;
    private Boolean verbose                 = DescriptorImpl.verbose;
    private Boolean importTowerLogs			= DescriptorImpl.importTowerLogs;
    private Boolean removeColor				= DescriptorImpl.removeColor;

	@DataBoundConstructor
	public AnsibleTower(
			@Nonnull String towerServer, @Nonnull String jobTemplate, String extraVars, String jobTags,
			String limit, String inventory, String credential, Boolean verbose, Boolean importTowerLogs,
			Boolean removeColor
	) {
		this.towerServer = towerServer;
		this.jobTemplate = jobTemplate;
		this.extraVars = extraVars;
		this.jobTags = jobTags;
		this.limit = limit;
		this.inventory = inventory;
		this.credential = credential;
		this.verbose = verbose;
		this.importTowerLogs = importTowerLogs;
		this.removeColor = removeColor;
	}

	@Nonnull
	public String getTowerServer() { return towerServer; }
	@Nonnull
	public String getJobTemplate() { return jobTemplate; }
	public String getExtraVars() { return extraVars; }
	public String getJobTags() { return jobTags; }
	public String getLimit() { return limit; }
	public String getInventory() { return inventory; }
	public String getCredential() { return credential; }
	public Boolean getVerbose() { return verbose; }
	public Boolean getImportTowerLogs() { return importTowerLogs; }
	public Boolean getRemoveColor() { return removeColor; }

	@DataBoundSetter
	public void setTowerServer(String towerServer) { this.towerServer = towerServer; }
	@DataBoundSetter
	public void setJobTemplate(String jobTemplate) { this.jobTemplate = jobTemplate; }
	@DataBoundSetter
	public void setExtraVars(String extraVars) { this.extraVars = extraVars; }
	@DataBoundSetter
	public void setJobTags(String jobTags) { this.jobTags = jobTags; }
	@DataBoundSetter
	public void setLimit(String limit) { this.limit = limit; }
	@DataBoundSetter
	public void setInventory(String inventory) { this.inventory = inventory; }
	@DataBoundSetter
	public void setCredential(String credential) { this.credential = credential; }
	@DataBoundSetter
	public void setVerbose(Boolean verbose) { this.verbose = verbose; }
	@DataBoundSetter
	public void setImportTowerLogs(Boolean importTowerLogs) { this.importTowerLogs = importTowerLogs; }
	@DataBoundSetter
	public void setRemoveColor(Boolean removeColor) { this.removeColor = removeColor; }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException
	{
		AnsibleTowerRunner runner = new AnsibleTowerRunner();
		boolean runResult = runner.runJobTemplate(
				listener.getLogger(), this.getTowerServer(), this.getJobTemplate(), this.getExtraVars(),
				this.getLimit(), this.getJobTags(), this.getInventory(), this.getCredential(), this.verbose,
				this.importTowerLogs, this.getRemoveColor()
		);
		if(runResult) {
			build.setResult(Result.SUCCESS);
		} else {
			build.setResult(Result.FAILURE);
		}

		return runResult;
    }

	@Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public static final String towerServer    	= "";
        public static final String jobTemplate    	= "";
		public static final String extraVars      	= "";
		public static final String limit          	= "";
        public static final String jobTags        	= "";
		public static final String inventory      	= "";
		public static final String credential     	= "";
		public static final Boolean verbose       	= false;
		public static final Boolean importTowerLogs	= false;
		public static final Boolean removeColor		= false;

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() { return "Ansible Tower"; }

        public ListBoxModel doFillTowerServerItems() {
			ListBoxModel items = new ListBoxModel();
			items.add(" - None -");
			for(TowerInstallation towerServer : AnsibleTowerGlobalConfig.get().getTowerInstallation()) {
				items.add(towerServer.getTowerDisplayName());
			}
			return items;
        }

        // Some day I'd like to be able to make all of these dropdowns from quering the tower API
		// Maybe not in real time because that would be slow when loading a the configure job
        /*
        public ListBoxModel doFillPlaybookItems() {
        	ListBoxModel items = new ListBoxModel();
			items.add(" - None -");
            return null;
        }
        */
    }
}
