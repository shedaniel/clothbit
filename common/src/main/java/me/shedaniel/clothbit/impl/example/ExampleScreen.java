package me.shedaniel.clothbit.impl.example;

import me.shedaniel.clothbit.api.compose.Compose;
import me.shedaniel.clothbit.api.compose.Store;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExampleScreen {
    @Store
    private int counter = 0;
    @Store
    private Data data;
    
    private static class Data {
        private long seconds() {
            return Util.getMillis() / 1000;
        }
    }
    
    @Compose
    public GuiEventListener Main() {
        return new AbstractContainerEventHandler() {
            @Override
            public List<? extends GuiEventListener> children() {
                return null;
            }
        };
    }
    
    @Compose
    public GuiEventListener CounterButton() {
        return new Button(0, 0, 0, 0, new TextComponent("Counter: " + counter), btn -> counter++);
    }
    
    @Compose
    public GuiEventListener SecondsButton() {
        return new Button(0, 0, 0, 0, new TextComponent("Second: " + data.seconds()), btn -> {});
    }
}
