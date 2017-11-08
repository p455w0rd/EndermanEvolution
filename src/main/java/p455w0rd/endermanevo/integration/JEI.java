package p455w0rd.endermanevo.integration;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import p455w0rd.endermanevo.init.ModIntegration.Mods;
import p455w0rd.endermanevo.init.ModItems;

@JEIPlugin
public class JEI implements IModPlugin {

	public static IIngredientBlacklist blacklist;
	private static RecipesGui recipesGui;
	private static IIngredientRegistry ingredientRegistry;
	private static ISubtypeRegistry subtypeRegistry;

	@Override
	public void register(@Nonnull IModRegistry registry) {
		IJeiHelpers helpers = registry.getJeiHelpers();
		blacklist = helpers.getIngredientBlacklist();
		IGuiHelper guiHelper = helpers.getGuiHelper();
		ingredientRegistry = registry.getIngredientRegistry();
		registry.addDescription(new ItemStack(ModItems.FRIENDER_PEARL), "jei.frienderpearl.desc");

	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime runtime) {
		recipesGui = (RecipesGui) runtime.getRecipesGui();
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
	}

	public static IIngredientRegistry getIngredientRegistry() {
		return ingredientRegistry;
	}

	public static void showRecipes(List<String> categoryUIDs) {
		recipesGui.showCategories(categoryUIDs);
	}

	public static void blacklistItem(ItemStack stack) {
		if (Mods.JEI.isLoaded() && blacklist != null) {
			blacklist.addIngredientToBlacklist(stack);
		}
	}

	public static boolean isItemBlacklisted(ItemStack stack) {
		if (Mods.JEI.isLoaded()) {
			return blacklist.isIngredientBlacklisted(stack);
		}
		return false;
	}

	public static void whitelistItem(ItemStack stack) {
		if (Mods.JEI.isLoaded()) {
			blacklist.removeIngredientFromBlacklist(stack);
		}
	}

	public static void handleItemBlacklisting(ItemStack stack, boolean shouldBlacklist) {
		if (shouldBlacklist) {
			if (!isItemBlacklisted(stack)) {
				blacklistItem(stack);
			}
			return;
		}
		if (isItemBlacklisted(stack)) {
			whitelistItem(stack);
		}
	}

	public static void setRecipeItems(@Nonnull IRecipeLayout recipeLayout, @Nonnull IIngredients ingredients, @Nullable int[] itemInputSlots, @Nullable int[] itemOutputSlots, @Nullable int[] fluidInputSlots, @Nullable int[] fluidOutputSlots) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		if (itemInputSlots != null) {
			List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
			for (int i = 0; i < inputs.size() && i < itemInputSlots.length; i++) {
				int inputSlot = itemInputSlots[i];
				guiItemStacks.set(inputSlot, inputs.get(i));
			}
		}

		if (itemOutputSlots != null) {
			List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
			for (int i = 0; i < outputs.size() && i < itemOutputSlots.length; i++) {
				int outputSlot = itemOutputSlots[i];
				guiItemStacks.set(outputSlot, outputs.get(i));
			}
		}

		if (fluidInputSlots != null) {
			List<List<FluidStack>> fluidInputs = ingredients.getInputs(FluidStack.class);
			for (int i = 0; i < fluidInputs.size() && i < fluidInputSlots.length; i++) {
				int inputTank = fluidInputSlots[i];
				guiFluidStacks.set(inputTank, fluidInputs.get(i));
			}
		}

		if (fluidOutputSlots != null) {
			List<List<FluidStack>> fluidOutputs = ingredients.getOutputs(FluidStack.class);
			for (int i = 0; i < fluidOutputs.size() && i < fluidOutputSlots.length; i++) {
				int outputTank = fluidOutputSlots[i];
				guiFluidStacks.set(outputTank, fluidOutputs.get(i));
			}
		}
	}

}
