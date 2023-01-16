package com.backyard.DL1200LIFT.impl.program;

import java.util.Locale;

import com.backyard.DL1200LIFT.impl.Style;
import com.backyard.DL1200LIFT.impl.V3Style;
import com.backyard.DL1200LIFT.impl.V5Style;
import com.backyard.DL1200LIFT.impl.i18n.CommandNamesResource;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.SystemAPI;
import com.ur.urcap.api.domain.data.DataModel;


public class BackyardLiftProgramNodeService implements SwingProgramNodeService<BackyardLiftProgramNodeContribution, BackyardLiftProgramNodeView> {

    @Override
    public String getId() {
        return "Backyad Lift Program Node";
    }

    @Override
    public void configureContribution(ContributionConfiguration configuration) {
        configuration.setChildrenAllowed(false);

    }

    @Override
    public String getTitle(Locale locale) {
        CommandNamesResource commandNames = new CommandNamesResource(locale);
        return commandNames.nodeName();
    }

    @Override
    public BackyardLiftProgramNodeView createView(ViewAPIProvider apiProvider) {
        SystemAPI systemAPI = apiProvider.getSystemAPI();
        Style style = systemAPI.getSoftwareVersion().getMajorVersion() >= 5 ? new V5Style() : new V3Style();
        return new BackyardLiftProgramNodeView(style);
    }

    @Override
    public BackyardLiftProgramNodeContribution createNode(ProgramAPIProvider apiProvider,
                                                          BackyardLiftProgramNodeView view, DataModel model, CreationContext context) {
        return new BackyardLiftProgramNodeContribution(apiProvider, view, model);
    }

}
