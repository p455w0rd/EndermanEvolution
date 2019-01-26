package p455w0rd.endermanevo.integration;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mezz.jei.api.*;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import p455w0rd.endermanevo.init.ModItems;

@JEIPlugin
public class JEI implements IModPlugin {

	@Override
	public void register(@Nonnull IModRegistry registry) {
		registry.addIngredientInfo(new ItemStack(ModItems.FRIENDER_PEARL), VanillaTypes.ITEM, "jei.friender_pearl.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.SKULL_ENDERMAN), VanillaTypes.ITEM, "jei.skull_enderman.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.SKULL_EVOLVED_ENDERMAN), VanillaTypes.ITEM, "jei.skull_evolved_enderman.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.SKULL_FRIENDERMAN), VanillaTypes.ITEM, "jei.skull_frienderman.desc");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime runtime) {
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
	}

	public static void setRecipeItems(@Nonnull IRecipeLayout recipeLayout, @Nonnull IIngredients ingredients, @Nullable int[] itemInputSlots, @Nullable int[] itemOutputSlots, @Nullable int[] fluidInputSlots, @Nullable int[] fluidOutputSlots) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		if (itemInputSlots != null) {
			List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
			for (int i = 0; i < inputs.size() && i < itemInputSlots.length; i++) {
				int inputSlot = itemInputSlots[i];
				guiItemStacks.set(inputSlot, inputs.get(i));
			}
		}

		if (itemOutputSlots != null) {
			List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
			for (int i = 0; i < outputs.size() && i < itemOutputSlots.length; i++) {
				int outputSlot = itemOutputSlots[i];
				guiItemStacks.set(outputSlot, outputs.get(i));
			}
		}

		if (fluidInputSlots != null) {
			List<List<FluidStack>> fluidInputs = ingredients.getInputs(VanillaTypes.FLUID);
			for (int i = 0; i < fluidInputs.size() && i < fluidInputSlots.length; i++) {
				int inputTank = fluidInputSlots[i];
				guiFluidStacks.set(inputTank, fluidInputs.get(i));
			}
		}

		if (fluidOutputSlots != null) {
			List<List<FluidStack>> fluidOutputs = ingredients.getOutputs(VanillaTypes.FLUID);
			for (int i = 0; i < fluidOutputs.size() && i < fluidOutputSlots.length; i++) {
				int outputTank = fluidOutputSlots[i];
				guiFluidStacks.set(outputTank, fluidOutputs.get(i));
			}
		}
	}

}
