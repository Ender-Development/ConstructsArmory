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

package c4.conarm.integrations.jei;

import c4.conarm.common.ConstructsRegistry;
import c4.conarm.lib.ArmoryRegistry;
import c4.conarm.lib.armor.ArmorCore;
import c4.conarm.lib.materials.ArmorMaterialType;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.plugin.jei.interpreter.TableSubtypeInterpreter;
import slimeknights.tconstruct.plugin.jei.interpreter.ToolSubtypeInterpreter;
import slimeknights.tconstruct.plugin.jei.material.MaterialWrapper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
    public static final List<String> PARTS = Arrays.asList(ArmorMaterialType.CORE, ArmorMaterialType.PLATES, ArmorMaterialType.TRIM);
    public static IJeiHelpers jeiHelpers;

    @Override
    public void registerItemSubtypes(@Nonnull ISubtypeRegistry registry) {

        ToolSubtypeInterpreter armorInterpreter = new ToolSubtypeInterpreter();
        TableSubtypeInterpreter tableInterpreter = new TableSubtypeInterpreter();

        for (ArmorCore armor : ArmoryRegistry.getArmor()) {
            registry.registerSubtypeInterpreter(armor, armorInterpreter);
        }

        registry.registerSubtypeInterpreter(Item.getItemFromBlock(ConstructsRegistry.armorForge), tableInterpreter);
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
        final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new ArmorCategory(guiHelper, PARTS));
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        List<MaterialWrapper> materialWrappers = TinkerRegistry.getAllMaterials().stream()
                .filter(material -> !material.isHidden() && material.hasItems() && !material.getAllStats().isEmpty())
                .map(MaterialWrapper::new).collect(Collectors.toList());

        ArmorCategory armorCategory = new ArmorCategory(guiHelper, PARTS);
        List<MaterialWrapper> armorWrapper = materialWrappers.stream()
                .filter(materialWrapper -> PARTS.stream().anyMatch(type -> materialWrapper.getMaterial().hasStats(type)))
                .collect(Collectors.toList());
        registry.addRecipes(armorWrapper, armorCategory.getUid());
        registry.addRecipeCatalyst(new ItemStack(ConstructsRegistry.armorForge, 1), armorCategory.getUid());
        registry.addRecipeCatalyst(new ItemStack(ConstructsRegistry.armorStation, 1), armorCategory.getUid());
    }
}
