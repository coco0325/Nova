package xyz.xenondevs.nova.world.fakeentity.metadata.impl

import net.minecraft.core.Rotations
import net.minecraft.network.syncher.EntityDataSerializers

private val DEFAULT_ROTATION = Rotations(0f, 0f, 0f)

class ArmorStandMetadata : LivingEntityMetadata() {
    
    private val sharedFlags = sharedFlags(15)
    
    var isSmall: Boolean by sharedFlags[0]
    var hasArms: Boolean by sharedFlags[2]
    var hasNoBasePlate: Boolean by sharedFlags[3]
    var isMarker: Boolean by sharedFlags[4]
    var headRotation: Rotations by entry(16, EntityDataSerializers.ROTATIONS, DEFAULT_ROTATION)
    var bodyRotation: Rotations by entry(17, EntityDataSerializers.ROTATIONS, DEFAULT_ROTATION)
    var leftArmRotation: Rotations by entry(18, EntityDataSerializers.ROTATIONS, DEFAULT_ROTATION)
    var rightArmRotation: Rotations by entry(19, EntityDataSerializers.ROTATIONS, DEFAULT_ROTATION)
    var leftLegRotation: Rotations by entry(20, EntityDataSerializers.ROTATIONS, DEFAULT_ROTATION)
    var rightLegRotation: Rotations by entry(21, EntityDataSerializers.ROTATIONS, DEFAULT_ROTATION)
    
}