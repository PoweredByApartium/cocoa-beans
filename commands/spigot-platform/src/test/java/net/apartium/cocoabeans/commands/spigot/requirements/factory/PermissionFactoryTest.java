package net.apartium.cocoabeans.commands.spigot.requirements.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class PermissionFactoryTest {

    @Test
    void passingPermissionFactory() {
        PermissionFactory permissionFactory = new PermissionFactory();

        assertNull(permissionFactory.getRequirement(null, null));
    }

}
