package xyz.xenondevs.nova.world.generation.builder

import com.mojang.datafixers.util.Either
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import xyz.xenondevs.nova.addon.registry.worldgen.BiomeRegistry
import xyz.xenondevs.nova.registry.NovaRegistries
import xyz.xenondevs.nova.registry.RegistryElementBuilder
import xyz.xenondevs.nova.registry.vanilla.VanillaRegistries
import xyz.xenondevs.nova.util.data.ResourceLocationOrTagKey
import xyz.xenondevs.nova.util.getOrCreateHolder
import xyz.xenondevs.nova.world.generation.ExperimentalWorldGen
import xyz.xenondevs.nova.world.generation.inject.biome.BiomeInjection

/**
 * Builder for [BiomeInjections][BiomeInjection]. Use [build] to get the [BiomeInjection] instance or [register] to register
 * it. Check out the [docs page](https://xenondevs.xyz/docs/nova/addon/worldgen/inject/biome/) on biome injections for more information.
 *
 * @see [BiomeRegistry]
 */
@ExperimentalWorldGen
class BiomeInjectionBuilder(id: ResourceLocation): RegistryElementBuilder<BiomeInjection>(NovaRegistries.BIOME_INJECTION, id) {
    
    private val biomes = mutableListOf<ResourceLocationOrTagKey<Biome>>()
    private val features = Array(11) { mutableListOf<Holder<PlacedFeature>>() }
    
    /**
     * Adds a [Biome's][Biome] [ResourceLocation] to the list of biomes this [BiomeInjection] should be applied to.
     */
    fun biome(biome: ResourceLocation): BiomeInjectionBuilder {
        biomes.add(ResourceLocationOrTagKey.ofLocation(biome))
        return this
    }
    
    /**
     * Adds a [Biome's][Biome] [ResourceKey] to the list of biomes this [BiomeInjection] should be applied to.
     */
    fun biome(biome: ResourceKey<Biome>): BiomeInjectionBuilder {
        return biome(biome.location())
    }
    
    /**
     * Adds a [Biome's][Biome] [String] id to the list of biomes this [BiomeInjection] should be applied to.
     * (e.g. "minecraft:plains")
     */
    fun biome(biome: String): BiomeInjectionBuilder {
        biomes.add(ResourceLocationOrTagKey.ofLocation(ResourceLocation(biome)))
        return this
    }
    
    /**
     * Adds a [TagKey] of [Biomes][Biome] to the list of biomes this [BiomeInjection] should be applied to.
     */
    fun biomes(biomeTag: TagKey<Biome>): BiomeInjectionBuilder {
        biomes.add(ResourceLocationOrTagKey.ofTag(biomeTag))
        return this
    }
    
    /**
     * Adds multiple [Biome's][Biome] [ResourceLocations][ResourceLocation] to the list of biomes this [BiomeInjection]
     * should be applied to.
     */
    fun biomes(vararg biomes: ResourceLocation): BiomeInjectionBuilder {
        biomes.forEach(::biome)
        return this
    }
    
    /**
     * Adds multiple [Biome's][Biome] [ResourceKeys][ResourceKey] to the list of biomes this [BiomeInjection] should be
     * applied to.
     */
    fun biomes(vararg biomes: ResourceKey<Biome>): BiomeInjectionBuilder {
        biomes.forEach(::biome)
        return this
    }
    
    /**
     * Adds multiple [Biome's][Biome] [String] ids to the list of biomes this [BiomeInjection] should be applied to.
     * (e.g. "minecraft:plains")
     */
    fun biomes(vararg biomes: String): BiomeInjectionBuilder {
        biomes.forEach(::biome)
        return this
    }
    
    /**
     * Adds a [PlacedFeature] at the specified [GenerationStep.Decoration] to the list of features this [BiomeInjection]
     * should add.
     *
     * For more information on features, check out their [docs page](https://xenondevs.xyz/docs/nova/addon/worldgen/features/features/).
     */
    fun feature(index: GenerationStep.Decoration, feature: PlacedFeature): BiomeInjectionBuilder {
        features[index.ordinal].add(Holder.direct(feature))
        return this
    }
    
    /**
     * Adds a [PlacedFeature] via a [Holder] that either already contains the [PlacedFeature],
     * or is set later by the [PlacedFeature Registry][Registries.PLACED_FEATURE] at the specified
     * [GenerationStep.Decoration] to the list of features this [BiomeInjection] should add.
     */
    fun feature(index: GenerationStep.Decoration, feature: Holder<PlacedFeature>): BiomeInjectionBuilder {
        features[index.ordinal].add(feature)
        return this
    }
    
    /**
     * Adds a [PlacedFeature] to the list of features via its [ResourceLocation] to the list of features this [BiomeInjection]
     * should add. If the [PlacedFeature] is not yet registered, an empty [Holder] will be created and the [PlacedFeature]
     * will be set later by the [PlacedFeature Registry][Registries.PLACED_FEATURE].
     */
    fun feature(index: GenerationStep.Decoration, featureId: ResourceLocation): BiomeInjectionBuilder {
        features[index.ordinal].add(VanillaRegistries.PLACED_FEATURE.getOrCreateHolder(featureId))
        return this
    }
    
    /**
     * Adds a [PlacedFeature] to the list of features via its [ResourceKey] to the list of features this [BiomeInjection]
     * should add. If the [PlacedFeature] is not yet registered, an empty [Holder] will be created and the [PlacedFeature]
     * will be set later by the [PlacedFeature Registry][Registries.PLACED_FEATURE].
     */
    fun feature(index: GenerationStep.Decoration, featureKey: ResourceKey<PlacedFeature>): BiomeInjectionBuilder {
        return feature(index, featureKey.location())
    }
    
    /**
     * Adds multiple [PlacedFeature]s at the specified [GenerationStep.Decoration] to the list of features this [BiomeInjection]
     * should add.
     */
    fun features(index: GenerationStep.Decoration, vararg placedFeatures: PlacedFeature): BiomeInjectionBuilder {
        features[index.ordinal].addAll(placedFeatures.map { Holder.direct(it) })
        return this
    }
    
    /**
     * Adds multiple [PlacedFeature]s via [Holders][Holder] that either already contain a [PlacedFeature],
     * or are set later by the [PlacedFeature Registry][Registries.PLACED_FEATURE] at the specified
     * [GenerationStep.Decoration] to the list of features this [BiomeInjection] should add.
     *
     * For more information on features, check out their [docs page](https://xenondevs.xyz/docs/nova/addon/worldgen/features/features/).
     */
    fun features(index: GenerationStep.Decoration, vararg placedFeatures: Holder<PlacedFeature>): BiomeInjectionBuilder {
        features[index.ordinal].addAll(placedFeatures)
        return this
    }
    
    /**
     * Adds multiple [PlacedFeature]s to the list of features via their [ResourceLocation] to the list of features this
     * [BiomeInjection] should add. If a [PlacedFeature] is not yet registered, an empty [Holder] will be created and the
     * [PlacedFeature] will be set later by the [PlacedFeature Registry][Registries.PLACED_FEATURE].
     *
     * For more information on features, check out their [docs page](https://xenondevs.xyz/docs/nova/addon/worldgen/features/features/).
     */
    fun features(index: GenerationStep.Decoration, vararg placedFeatureIds: ResourceLocation): BiomeInjectionBuilder {
        features[index.ordinal].addAll(placedFeatureIds.map { VanillaRegistries.PLACED_FEATURE.getOrCreateHolder(it) })
        return this
    }
    
    /**
     * Adds multiple [PlacedFeature]s to the list of features via their [ResourceKey] to the list of features this
     * [BiomeInjection] should add. If a [PlacedFeature] is not yet registered, an empty [Holder] will be created and the
     * [PlacedFeature] will be set later by the [PlacedFeature Registry][Registries.PLACED_FEATURE].
     *
     * For more information on features, check out their [docs page](https://xenondevs.xyz/docs/nova/addon/worldgen/features/features/).
     */
    fun features(index: GenerationStep.Decoration, vararg placedFeatureKeys: ResourceKey<PlacedFeature>): BiomeInjectionBuilder {
        placedFeatureKeys.forEach { feature(index, it.location()) }
        return this
    }
    
    /**
     * Builds a [BiomeInjection] instance from the current state of this builder.
     */
    override fun build(): BiomeInjection {
        return BiomeInjection(
            if (biomes.size == 1) Either.right(biomes[0]) else Either.left(biomes),
            features.map { HolderSet.direct(it) }
        )
    }
    
}