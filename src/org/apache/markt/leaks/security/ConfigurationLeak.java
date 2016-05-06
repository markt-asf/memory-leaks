package org.apache.markt.leaks.security;

import org.apache.markt.leaks.LeakBase;

/**
 * Java 5
 *   - leaks
 * Java 6
 *   - leaks
 * Java 7
 *  - update <=45 - leaks
 *  - update >=51 - no leak
 * Java 8
 *  - no leak
 */
public class ConfigurationLeak extends LeakBase {

    public static void main(String[] args) {
        ConfigurationLeak configurationLeak = new ConfigurationLeak();
        configurationLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        try {
            Class.forName("javax.security.auth.login.Configuration");
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }
}