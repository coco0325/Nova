package xyz.xenondevs.nova.transformer.patch.item

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation
import net.minecraft.world.item.ItemStack
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodInsnNode
import xyz.xenondevs.bytebase.asm.buildInsnList
import xyz.xenondevs.bytebase.util.calls
import xyz.xenondevs.bytebase.util.replaceFirst
import xyz.xenondevs.nova.transformer.MethodTransformer
import xyz.xenondevs.nova.util.item.novaMaterial
import xyz.xenondevs.nova.util.reflection.ReflectionRegistry
import xyz.xenondevs.nova.util.reflection.ReflectionUtils

internal object AttributePatch : MethodTransformer(ReflectionRegistry.ITEM_STACK_GET_ATTRIBUTE_MODIFIERS_METHOD, computeFrames = true) {
    
    /**
     * Patches the ItemStack#getAttributeModifiers to return to correct modifiers for Nova's items.
     */
    override fun transform() {
        methodNode.replaceFirst(2, 0, buildInsnList {
            aLoad(1)
            invokeStatic(ReflectionUtils.getMethodByName(AttributePatch::class.java, false, "getDefaultAttributeModifiers"))
        }) { it.opcode == Opcodes.INVOKEVIRTUAL && (it as MethodInsnNode).calls(ReflectionRegistry.ITEM_GET_DEFAULT_ATTRIBUTE_MODIFIERS_METHOD) }
    }
    
    @JvmStatic
    fun getDefaultAttributeModifiers(itemStack: ItemStack, slot: EquipmentSlot): Multimap<Attribute, AttributeModifier> {
        val novaMaterial = itemStack.novaMaterial
        if (novaMaterial != null) {
            val attributeModifiers = novaMaterial.itemLogic.attributeModifiers[slot]?.takeUnless(List<*>::isEmpty)
            val novaModifiers = Multimaps.newListMultimap<Attribute, AttributeModifier>(HashMap(), ::ArrayList)
            attributeModifiers?.forEach {
                novaModifiers.put(
                    it.attribute,
                    AttributeModifier(it.uuid, it.name, it.value, Operation.fromValue(it.operation.ordinal))
                )
            }
            
            return novaModifiers
        }
        
        return itemStack.item.getDefaultAttributeModifiers(slot)
    }
    
}