package xyz.xenondevs.nova.data.serialization.cbf

import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.StreamTagVisitor
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagType
import net.minecraft.nbt.TagVisitor
import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.io.ByteReader
import java.io.DataInput
import java.io.DataOutput

internal object CBFCompoundTagType : TagType.VariableSize<CBFCompoundTag> {
    
    override fun load(input: DataInput, i: Int, accounter: NbtAccounter?): CBFCompoundTag {
        val reader = ByteReader.fromDataInput(input)
        reader.skip(4) // skip length int
        return CBFCompoundTag(CBF.read(reader)!!)
    }
    
    override fun skip(input: DataInput){
        val reader = ByteReader.fromDataInput(input)
        reader.skip(4) // skip length int
        if (reader.readBoolean()) {
            reader.skip(reader.readVarInt())
        }
    }
    
    override fun getName(): String {
        return "CBFCompound"
    }
    
    override fun getPrettyName(): String {
        return "TAG_CBF_COMPOUND"
    }
    
    override fun parse(input: DataInput, visitor: StreamTagVisitor): StreamTagVisitor.ValueResult {
        throw UnsupportedOperationException()
    }
    
}

internal class CBFCompoundTag(val compound: NamespacedCompound) : Tag {
    
    constructor() : this(NamespacedCompound())
    
    override fun getId(): Byte = 7 // byte array
    override fun getType(): TagType<CBFCompoundTag> = CBFCompoundTagType
    
    override fun copy(): Tag {
        return CBFCompoundTag(compound.copy())
    }
    
    override fun write(out: DataOutput) {
        val bytes = CBF.write(compound)
        out.writeInt(bytes.size)
        out.write(bytes)
    }
    
    // also accessed via reflection
    fun compoundToByteArray(): ByteArray {
        return CBF.write(compound)
    }
    
    override fun accept(visitor: StreamTagVisitor): StreamTagVisitor.ValueResult {
        return visitor.visit(compound.toString())
    }
    
    override fun accept(visitor: TagVisitor) {
        visitor.visitString(StringTag.valueOf(compound.toString())) // TODO
    }
    
    override fun equals(other: Any?): Boolean {
        if (other === this)
            return true
        
        return other is CBFCompoundTag && compoundToByteArray().contentEquals(other.compoundToByteArray())
    }
    
    override fun hashCode(): Int =
        compoundToByteArray().contentHashCode()
    
    override fun sizeInBytes(): Int {
        throw UnsupportedOperationException()
    }
    
}