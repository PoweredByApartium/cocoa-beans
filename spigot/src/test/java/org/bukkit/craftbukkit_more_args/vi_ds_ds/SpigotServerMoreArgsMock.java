package org.bukkit.craftbukkit_more_args.vi_ds_ds;

import org.bukkit.craftbukkit.SpigotServerMock;
import org.bukkit.craftbukkit.v1_8_R3.SpigotServerMock_1_8;

public class SpigotServerMoreArgsMock extends SpigotServerMock {

    public SpigotServerMock_1_8.Method_v_1_8_R3 getHandle() {
        return new SpigotServerMock_1_8().new Method_v_1_8_R3();
    }
}
