package jenkins.plugins.ansible_tower.util;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;

import java.util.List;

public class TowerInstallation extends AbstractDescribableImpl<TowerInstallation> {
    private static final long getSerialVersionUID = 1L;

    private final String towerDisplayName;
    private final String towerURL;
    private final String towerCredentialsId;
    private final boolean towerTrustCert;

    @DataBoundConstructor
    public TowerInstallation(String towerDisplayName, String towerURL, String towerCredentialsId, boolean towerTrustCert) {
        this.towerDisplayName = towerDisplayName;
        this.towerCredentialsId = towerCredentialsId;
        this.towerURL = towerURL;
        this.towerTrustCert = towerTrustCert;
    }

    public String getTowerDisplayName() { return this.towerDisplayName; }
    public String getTowerURL() { return this.towerURL; }
    public String getTowerCredentialsId() { return this.towerCredentialsId; }
    public boolean getTowerTrustCert() { return this.towerTrustCert; }

    public TowerConnector getTowerConnector() {
        return TowerInstallation.getTowerConnecorStatic(this.towerURL, this.towerCredentialsId, this.towerTrustCert);
    }

    public static TowerConnector getTowerConnecorStatic(String towerURL, String towerCredentialsId, boolean trustCert) {
        String username = null;
        String password = null;
        if(StringUtils.isNotBlank(towerCredentialsId)) {
            List<StandardUsernamePasswordCredentials> credsList = CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class);
            for(StandardUsernamePasswordCredentials creds : credsList) {
                if(creds.getId().equals(towerCredentialsId)) {
                    username = creds.getUsername();
                    password = creds.getPassword().getPlainText();
                }
            }
        }
        TowerLogger.writeMessage("Creating a test connector with "+ username +"@"+ towerURL);
        TowerConnector testConnector = new TowerConnector(towerURL, username, password, trustCert);
        return testConnector;
    }

    @Extension
    public static class TowerInstallationDescriptor extends Descriptor<TowerInstallation> {

        public FormValidation doTestTowerConnection(
                @QueryParameter("towerURL") final String towerURL,
                @QueryParameter("towerCredentialsId") final String towerCredentialsId,
                @QueryParameter("towerTrustCert") final boolean towerTrustCert
        ) {
            TowerLogger.writeMessage("Starting to test connection with ("+ towerURL +") and ("+ towerCredentialsId +") and ("+ towerTrustCert +")");
            TowerConnector testConnector = TowerInstallation.getTowerConnecorStatic(towerURL, towerCredentialsId, towerTrustCert);
            try {
                testConnector.testConnection();
                return FormValidation.ok("Success");
            } catch(Exception e) {
                return FormValidation.error(e.getMessage());
            }
        }

        public ListBoxModel doFillTowerCredentialsIdItems(@AncestorInPath Project project) {
            return new StandardListBoxModel().withEmptySelection().withMatching(
                    instanceOf(UsernamePasswordCredentials.class),
                    CredentialsProvider.lookupCredentials(StandardUsernameCredentials.class, project)
            );
        }

        @Override
        public String getDisplayName() {
            return "Tower Installation";
        }
    }
}


