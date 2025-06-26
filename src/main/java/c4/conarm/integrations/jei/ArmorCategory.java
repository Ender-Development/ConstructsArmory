package c4.conarm.integrations.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.plugin.jei.material.AbstractCategory;
import slimeknights.tconstruct.plugin.jei.material.MaterialWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArmorCategory extends AbstractCategory {
    IGuiHelper guiHelper;
    MaterialWrapper wrapper;

    public ArmorCategory(IGuiHelper guiHelper, List<String> parts) {
        super(guiHelper, parts);
        this.guiHelper = guiHelper;
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.drawableBuilder(new ResourceLocation("tconstruct", "textures/gui/jei/materials.png"), 0, 16, 16, 16).setTextureSize(32, 32).build();
    }

    @Nonnull
    @Override
    public String getTitle() {
        return new TextComponentTranslation("gui.jei.material.armor").getFormattedText();
    }

    @Nonnull
    @Override
    public String getUid() {
        return Util.MODID + ":armor_stats";
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull MaterialWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
        wrapper = recipeWrapper;
        super.setRecipe(recipeLayout, recipeWrapper, ingredients);
    }

    @Override
    protected List<String> additionalTooltips(List<String> statInfo, List<String> statDesc, int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();
        float height = 4 + HEADING_SPACING;
        for (int i = 0; i < Math.min(statInfo.size(), statDesc.size()); ++i) {
            if (i == 2 || i == 5) {
                height += LINE_SPACING + HEADING_SPACING;
                height++;
            }
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(i)), height++, mouseX, mouseY)) {
                tooltip.addAll(formatTooltip(statDesc.get(i)));
            }
        }
        return tooltip;
    }

    @Override
    protected void drawStats(LinkedList<String> statInfo, float lineNumber) {
        String[] header = new String[]{
                getHeading("stat.core.name"),
                getHeading("stat.plates.name"),
                getHeading("stat.trim.name")
        };
        int index = 0;

        for (int i = 0; i < statInfo.size(); ++i) {
            if (i == 2 || i == 5) {
                lineNumber += LINE_SPACING;
            }
            if (i == 0 || i == 2 || i == 5) {
                drawComponent(header[index], 0, lineNumber++, wrapper.getMaterial().materialTextColor, true);
                lineNumber += HEADING_SPACING;
                index++;
            }
            drawStatComponent(statInfo.get(i), lineNumber++);
        }
    }
}
