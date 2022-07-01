/*
 * This file is part of Clothbit.
 * Copyright (C) 2021, 2022 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.shedaniel.clothbit.impl.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothbit.api.client.gui.OptionsScreen;
import me.shedaniel.clothbit.api.options.OptionTypesContext;
import me.shedaniel.clothbit.api.options.OptionValue;
import me.shedaniel.clothbit.api.serializers.writer.RootValueWriter;
import me.shedaniel.clothbit.impl.client.gui.adapter.OptionEntryWriter;
import me.shedaniel.clothbit.impl.client.gui.entry.SplitatorListEntry;
import me.shedaniel.clothbit.impl.client.gui.widgets.ListWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class OptionsScreenImpl extends WidgetScreen implements OptionsScreen {
    private static final int TOP = 26;
    private static final int BOTTOM = 24;
    private final Screen parent;
    private final String id;
    private final OptionTypesContext ctx;
    private final ListWidget<?> widget = new ListWidget<>(0, 0, 0, 0, Gui.BACKGROUND_LOCATION);
    private final List<ListWidget.Entry<?>> entries = new ArrayList<>();
    
    public OptionsScreenImpl(Screen parent, String id, OptionTypesContext ctx) {
        super(getName(id + ".title"));
        this.parent = parent;
        this.id = id;
        this.ctx = ctx;
    }
    
    public static Component getName(String id) {
        return new TranslatableComponent("config." + id);
    }
    
    @Override
    public <T> void add(OptionValue<T> value) {
        RootValueWriter writer = new OptionEntryWriter.RootOptionValueWriter(id, entries::add, ctx);
        value.write(writer, ctx);
    }
    
    @Override
    protected void initWidgets() {
        for (int i = 0; i < entries.size(); i++) {
            ListWidget.Entry<?> entry = entries.get(i);
            if (i != entries.size() - 1) {
                entry = new SplitatorListEntry(entry);
            }
            ((ListWidget) widget).addItem(entry);
        }
        entries.clear();
        widget.updateSize(width, height, TOP, height - BOTTOM);
        addWidget(widget);
        
        int buttonWidths = Math.min(200, (width - 50 - 12) / 3);
        addButton(new Button(width / 2 - buttonWidths - 3, height - 22, buttonWidths, 20, TextComponent.EMPTY, button -> onClose()));
        addButton(new Button(width / 2 + 3, height - 22, buttonWidths, 20, TextComponent.EMPTY, button -> save()));
    }
    
    @Override
    public void onClose() {
        if (isEdited()) {
            minecraft.setScreen(new ConfirmScreen(this::acceptConfirm, new TranslatableComponent("text.clothbit.quit_config"),
                    new TranslatableComponent("text.clothbit.quit_config_sure"),
                    new TranslatableComponent("text.clothbit.quit_discard"),
                    new TranslatableComponent("gui.cancel")));
        } else {
            minecraft.setScreen(parent);
        }
    }
    
    private boolean isEdited() {
        return false;
    }
    
    private void acceptConfirm(boolean confirmed) {
        if (!confirmed) {
            minecraft.setScreen(this);
        } else {
            minecraft.setScreen(parent);
        }
    }
    
    private void save() {
    }
    
    @Override
    public void render(PoseStack poses, int mouseX, int mouseY, float delta) {
        super.render(poses, mouseX, mouseY, delta);
        drawCenteredString(poses, font, getTitle(), width / 2, 9, 0xFFFFFF);
    }
    
    @Override
    public Screen get() {
        return this;
    }
}
