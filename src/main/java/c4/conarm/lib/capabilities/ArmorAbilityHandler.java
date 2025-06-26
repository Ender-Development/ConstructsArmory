/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Construct's Armory, a mod made for Minecraft.
 *
 * Construct's Armory is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Construct's Armory is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Construct's Armory.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.conarm.lib.capabilities;

import c4.conarm.ConstructsArmory;
import c4.conarm.Tags;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class ArmorAbilityHandler {

    @CapabilityInject(IArmorAbilities.class)
    public static final Capability<IArmorAbilities> ARMOR_AB_CAP = null;

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> evt) {

        if (evt.getObject() instanceof EntityPlayer) {
            evt.addCapability(new ResourceLocation(Tags.MOD_ID, "armor_abilities"), new Provider());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone evt) {

        IArmorAbilities old = getArmorAbilitiesData(evt.getOriginal());
        IArmorAbilities clone = getArmorAbilitiesData(evt.getEntityPlayer());

        if (old != null && clone != null) {
            clone.setAbilityMap(old.getAbilityMap());
        }
    }

    public static IArmorAbilities getArmorAbilitiesData(EntityPlayer player) {

        return player != null && player.hasCapability(ARMOR_AB_CAP, null) ? player.getCapability(ARMOR_AB_CAP, null) : null;
    }

    public static class ArmorAbilities implements IArmorAbilities {

        private Map<String, Integer> abilityMap = Maps.newHashMap();

        public ArmorAbilities() {}

        public void clearAllAbilities() {
            abilityMap.clear();
        }

        public Map<String, Integer> getAbilityMap() {
            return this.abilityMap;
        }

        public void setAbilityMap(Map<String, Integer> abilityMap) {
            this.abilityMap = abilityMap;
        }

        public void addAbility(String identifier, int amount) {
            abilityMap.merge(identifier, amount, (a, b) -> a + b);
        }

        public void removeAbility(String identifier, int amount) {
            if (abilityMap.get(identifier) != null) {

                int level = abilityMap.get(identifier) - amount;

                if (level <= 0) {
                    abilityMap.remove(identifier);
                } else {
                    abilityMap.replace(identifier, level);
                }
            }
        }

        public int getAbilityLevel(String identifier) {
            if (abilityMap.get(identifier) != null) {
                return abilityMap.get(identifier);
            } else {
                return 0;
            }
        }
    }

    public interface IArmorAbilities {

        Map<String, Integer> getAbilityMap();

        void clearAllAbilities();

        void setAbilityMap(Map<String, Integer> abilityMap);

        void addAbility(String identifier, int amount);

        void removeAbility(String identifier, int amount);

        int getAbilityLevel(String identifier);
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        private IArmorAbilities instance = ARMOR_AB_CAP.getDefaultInstance();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
            return capability == ARMOR_AB_CAP;
        }

        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
            return capability == ARMOR_AB_CAP ? ARMOR_AB_CAP.<T> cast(this.instance) : null;
        }

        @Override
        public NBTBase serializeNBT()
        {
            return ARMOR_AB_CAP.getStorage().writeNBT(ARMOR_AB_CAP, this.instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            ARMOR_AB_CAP.getStorage().readNBT(ARMOR_AB_CAP, this.instance, null, nbt);
        }
    }

    public static class Storage implements Capability.IStorage<IArmorAbilities> {

        @Override
        public NBTBase writeNBT(Capability<IArmorAbilities> capability, IArmorAbilities instance, EnumFacing side) {

            NBTTagCompound compound = new NBTTagCompound();
            NBTTagCompound tagcompound = new NBTTagCompound();
            Map<String, Integer> abilityMap = instance.getAbilityMap();

            for (String identifier : abilityMap.keySet()) {
                tagcompound.setInteger(identifier, abilityMap.get(identifier));
            }

            compound.setTag("ArmorAbilities", tagcompound);

            return compound;
        }

        @Override
        public void readNBT(Capability<IArmorAbilities> capability, IArmorAbilities instance, EnumFacing side, NBTBase nbt) {

            NBTTagCompound compound = (NBTTagCompound) nbt;
            NBTTagCompound tagcompound = compound.getCompoundTag("ArmorAbilities");
            Map<String, Integer> abilityMap = Maps.newHashMap();

            for (String identifier : tagcompound.getKeySet()) {
                abilityMap.putIfAbsent(identifier, tagcompound.getInteger(identifier));
            }

            instance.setAbilityMap(abilityMap);
        }
    }
}
