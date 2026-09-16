package net.apartium.cocoabeans.spigot.inventory;

import org.bukkit.Material;

public class ItemUtilsHelpers_1_20_R1 implements ItemUtilsHelpers {

    @Override
    public String name(Material material) {
        return material.getKey().getKey();
    }

}
