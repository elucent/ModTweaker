package modtweaker.mods.tconstruct.handlers;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import com.blamejared.mtlib.helpers.LogHelper;
import modtweaker.mods.tconstruct.TConstructHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.LinkedList;
import java.util.List;

import static com.blamejared.mtlib.helpers.InputHelper.toIItemStack;
import static com.blamejared.mtlib.helpers.InputHelper.toStack;

@ZenClass("mods.tconstruct.Drying")
public class Drying {

    protected static final String name = "TConstruct Drying Rack";

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ZenMethod
    public static void addRecipe(IItemStack input, IItemStack output, int time) {
        if (input == null || output == null) {
            LogHelper.logError(String.format("Required parameters missing for %s Recipe.", name));
            return;
        }
        RecipeMatch match = new RecipeMatch.Item(toStack(input), toStack(input).stackSize);
        MineTweakerAPI.apply(new Add(TConstructHelper.getDryingRecipe(toStack(output), match, time)));
    }

    //Passes the list to the base list implementation, and adds the recipe
    private static class Add extends BaseListAddition<DryingRecipe> {
        public Add(DryingRecipe recipe) {
            super(Drying.name, TConstructHelper.dryingList);
            this.recipes.add(recipe);
        }

        @Override
        protected String getRecipeInfo(DryingRecipe recipe) {
            return LogHelper.getStackDescription(recipe.getResult());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Removing a TConstruct Drying Rack recipe
    @ZenMethod
    public static void removeRecipe(IIngredient ingredient) {
        List<DryingRecipe> recipes = new LinkedList<DryingRecipe>();

        for (DryingRecipe recipe : TinkerRegistry.getAllDryingRecipes()) {
            if (recipe != null && recipe.getResult() != null && ingredient.matches(toIItemStack(recipe.getResult()))) {
                recipes.add(recipe);
            }
        }

        if (!recipes.isEmpty()) {
            MineTweakerAPI.apply(new Remove(recipes));
        } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", Drying.name, ingredient.toString()));
        }
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class Remove extends BaseListRemoval<DryingRecipe> {
        public Remove(List<DryingRecipe> list) {
            super(Drying.name, TConstructHelper.dryingList, list);
        }

        @Override
        protected String getRecipeInfo(DryingRecipe recipe) {
            return LogHelper.getStackDescription(recipe.getResult());
        }
    }
}
