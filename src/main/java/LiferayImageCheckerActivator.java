package com.liferay;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class LiferayImageCheckerActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) {
        System.out.println("LiferayImageChecker module started. Starting image check...");
        LiferayImageChecker.main(new String[0]);
    }

    @Override
    public void stop(BundleContext context) {
        System.out.println("LiferayImageChecker module stopped.");
    }
}
