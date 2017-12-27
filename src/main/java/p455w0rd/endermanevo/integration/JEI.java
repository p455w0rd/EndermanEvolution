package p455w0rd.endermanevo.integration;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import p455w0rd.endermanevo.init.ModItems;

@JEIPlugin
public class JEI implements IModPlugin {

	@Override
	public void register(@Nonnull IModRegistry registry) {
		registry.addIngredientInfo(new ItemStack(ModItems.FRIENDER_PEARL), ItemStack.class, "jei.friender_pearl.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.SKULL_ENDERMAN), ItemStack.class, "jei.skull_enderman.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.SKULL_EVOLVED_ENDERMAN), ItemStack.class, "jei.skull_evolved_enderman.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.SKULL_FRIENDERMAN), ItemStack.class, "jei.skull_frienderman.desc");
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
