package xyz.xenondevs.nova.data.recipe

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice

/**
 * The interface for all recipes for Nova blocks.
 */
interface NovaRecipe {
    val key: NamespacedKey
    val type: RecipeType<out NovaRecipe>
}

/**
 * The interface for all recipes that have on or more resulting [ItemStacks][ItemStack].
 */
sealed interface ResultRecipe {
    fun getAllResults(): List<ItemStack>
}

/**
 * The interface for all recipes that have only one result.
 */
interface SingleResultRecipe : ResultRecipe {
    
    val result: ItemStack
    
    override fun getAllResults(): List<ItemStack> {
        return listOf(result)
    }
    
}

/**
 * The interface for all recipes that have multiple results.
 */
interface MultiResultRecipe : ResultRecipe {
    
    val results: List<ItemStack>
    
    override fun getAllResults(): List<ItemStack> {
        return results
    }
    
}

/**
 * The interface for all recipes that have one or more input [RecipeChoices][RecipeChoice].
 */
sealed interface InputChoiceRecipe {
    fun getAllInputs(): List<RecipeChoice>
}

/**
 * The interface for all recipes that have only one input [RecipeChoice].
 */
interface SingleInputChoiceRecipe : InputChoiceRecipe {
    
    val input: RecipeChoice
    
    override fun getAllInputs(): List<RecipeChoice> {
        return listOf(input)
    }
    
}

/**
 * The interface for all recipes that have multiple input [RecipeChoices][RecipeChoice].
 */
interface MultiInputChoiceRecipe : InputChoiceRecipe {
    
    val inputs: List<RecipeChoice>
    
    override fun getAllInputs(): List<RecipeChoice> {
        return inputs
    }
    
}

/**
 * The abstract base class for all recipes that convert one [input] into a [result] in a defined [time].
 */
abstract class ConversionNovaRecipe(
    override val key: NamespacedKey,
    override val input: RecipeChoice,
    override val result: ItemStack,
    val time: Int
) : NovaRecipe, SingleResultRecipe, SingleInputChoiceRecipe
