package dev.anvilcraft.resource.anisum.event;

import dev.anvilcraft.resource.anisum.item.AnisumCreativeModeTab;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.neoforged.bus.api.Event;

import java.util.List;

@AllArgsConstructor
@Getter
public class AnisumTabLoadedEvent extends Event {
    private final List<AnisumCreativeModeTab> creativeModeTabs;
}
