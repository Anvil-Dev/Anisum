package dev.anvilcraft.resource.anisum.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.LinkedHashSet;
import java.util.Set;

public interface ByteBufCodecsUtil {
    static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Set<V>> set() {
        return (streamCodec) -> ByteBufCodecs.collection(LinkedHashSet::new, streamCodec);
    }
}
