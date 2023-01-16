package com.backyard.DL1200LIFT.impl.installation;

import java.util.Locale;

import com.backyard.DL1200LIFT.impl.LiftDaemonService;
import com.backyard.DL1200LIFT.impl.Style;
import com.backyard.DL1200LIFT.impl.V3Style;
import com.backyard.DL1200LIFT.impl.V5Style;
import com.backyard.DL1200LIFT.impl.i18n.CommandNamesResource;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.SystemAPI;
import com.ur.urcap.api.domain.data.DataModel;

public class BackyardLiftInstallationNodeService implements SwingInstallationNodeService<BackyardLiftInstallationNodeContribution, BackyardLiftInstallationNodeView> {
    private LiftDaemonService daemonService;

    public BackyardLiftInstallationNodeService(LiftDaemonService daemonService) {
        this.daemonService = daemonService;
    }

    @Override
    public void configureContribution(ContributionConfiguration configuration) {

    }

    @Override
    public String getTitle(Locale locale) {
        CommandNamesResource commandNames = new CommandNamesResource(locale);
        return commandNames.nodeName();
    }

    @Override
    public BackyardLiftInstallationNodeView createView(ViewAPIProvider apiProvider) {
        SystemAPI systemAPI = apiProvider.getSystemAPI();
        Style style = systemAPI.getSoftwareVersion().getMajorVersion() >= 5 ? new V5Style() : new V3Style();
        return new BackyardLiftInstallationNodeView(style);
    }

    @Override
    public BackyardLiftInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider,
                                                                           BackyardLiftInstallationNodeView view, DataModel model, CreationContext context) {
        return new BackyardLiftInstallationNodeContribution(apiProvider, model, view, daemonService);
    }
}
