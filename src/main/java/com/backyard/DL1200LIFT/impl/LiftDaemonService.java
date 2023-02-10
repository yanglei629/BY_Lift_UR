package com.backyard.DL1200LIFT.impl;

import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.DaemonService;

import java.net.MalformedURLException;
import java.net.URL;

public class LiftDaemonService implements DaemonService {
    private DaemonContribution daemonContribution;

    @Override
    public void init(DaemonContribution daemon) {
        this.daemonContribution = daemon;

        try {
            System.out.println("Daemon Status Before: " + daemon.getState());
            //daemonContribution.installResource(new URL("file:daemonEnv/"));
            daemonContribution.installResource(new URL("file:daemonEnv/daemon"));
            daemon.start();
            System.out.println("Daemon Status After: " + daemon.getState());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public URL getExecutable() {
        try {
            //return new URL("file:daemonEnv/daemon.sh");
            return new URL("file:daemonEnv/daemon/main.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public DaemonContribution getDaemon() {
        return daemonContribution;
    }
}
