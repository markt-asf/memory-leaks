package org.apache.markt.leaks.security;

import javax.security.auth.Policy;

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
@SuppressWarnings("deprecation")
public class PolicyLeak extends LeakBase {

    public static void main(String[] args) {
        PolicyLeak policyLeak = new PolicyLeak();
        policyLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        Policy.getPolicy();
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }
}