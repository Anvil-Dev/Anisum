package dev.anvilcraft.resource.anisum.annotations;

import dev.anvilcraft.resource.anisum.utils.SideDist;

public @interface Side {
    SideDist value() default SideDist.BOTH;
}
