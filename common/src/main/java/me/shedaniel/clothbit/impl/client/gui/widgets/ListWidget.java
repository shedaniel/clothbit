/*
 * This file is part of Clothbit.
 * Copyright (C) 2021 shedaniel
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

package me.shedaniel.clothbit.impl.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class ListWidget<E extends ListWidget.Entry<E>> extends WidgetWithBounds {
    protected static final int DRAG_OUTSIDE = -2;
    public static final float BACKGROUND_TILE_SIZE = 32.0F;
    private final Entries entries = new Entries();
    public int width;
    public int height;
    public int top;
    public int bottom;
    public int right;
    public int left;
    public Rectangle bounds;
    protected boolean verticallyCenter = true;
    protected int yDrag = -2;
    protected boolean selectionVisible = true;
    protected boolean renderSelection;
    protected int headerHeight;
    protected double scroll;
    protected boolean scrolling;
    @Nullable
    protected E hoveredItem;
    protected E selectedItem;
    protected ResourceLocation backgroundLocation;
    
    public ListWidget(int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
        this.backgroundLocation = backgroundLocation;
        this.updateBounds();
    }
    
    protected void updateBounds() {
        this.bounds = new Rectangle(this.left, this.top, this.right - this.left, this.bottom - this.top);
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    public void setRenderSelection(boolean selectionVisible) {
        this.selectionVisible = selectionVisible;
    }
    
    protected void setRenderHeader(boolean renderSelection, int headerHeight) {
        this.renderSelection = renderSelection;
        this.headerHeight = headerHeight;
        if (!renderSelection)
            this.headerHeight = 0;
    }
    
    public int getItemWidth() {
        return this.width - 12;
    }
    
    public E getSelectedItem() {
        return this.selectedItem;
    }
    
    public void selectItem(E item) {
        this.selectedItem = item;
    }
    
    public E getFocused() {
        return (E) super.getFocused();
    }
    
    public final List<E> children() {
        return this.entries;
    }
    
    protected final void clearItems() {
        this.entries.clear();
    }
    
    protected E getItem(int index) {
        return this.children().get(index);
    }
    
    public int addItem(E item) {
        this.entries.add(item);
        return this.entries.size() - 1;
    }
    
    protected int getItemCount() {
        return this.children().size();
    }
    
    protected boolean isSelected(int index) {
        return Objects.equals(this.getSelectedItem(), this.children().get(index));
    }
    
    protected final E getItemAtPosition(double mouseX, double mouseY) {
        int listMiddleX = this.left + this.width / 2;
        int minX = listMiddleX - this.getItemWidth() / 2;
        int maxX = listMiddleX + this.getItemWidth() / 2;
        int currentY = Mth.floor(mouseY - (double) this.top) - this.headerHeight + (int) this.getScroll() - 4;
        int itemY = 0;
        int itemIndex = -1;
        for (int i = 0; i < entries.size(); i++) {
            E item = getItem(i);
            itemY += item.getItemHeight();
            if (itemY > currentY) {
                itemIndex = i;
                break;
            }
        }
        return mouseX < (double) this.getScrollBarX() && mouseX >= minX && mouseX <= maxX && itemIndex >= 0 && currentY >= 0 && itemIndex < this.getItemCount() ? this.children().get(itemIndex) : null;
    }
    
    public void updateSize(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
        this.updateBounds();
    }
    
    public void setLeftPos(int left) {
        this.left = left;
        this.right = left + this.width;
        this.updateBounds();
    }
    
    protected int getMaxScrollPosition() {
        List<Integer> list = new ArrayList<>();
        int i = headerHeight;
        for (E entry : entries) {
            i += entry.getItemHeight();
            if (entry.getMorePossibleHeight() >= 0) {
                list.add(i + entry.getMorePossibleHeight());
            }
        }
        list.add(i);
        return Math.max(list.stream().max(Integer::compare).orElse(0), 1);
    }
    
    protected void clickedHeader(int x, int y) {
    }
    
    protected void renderHeader(PoseStack matrices, int rowLeft, int startY, Tesselator tesselator) {
    }
    
    protected void drawBackground() {
    }
    
    protected void renderDecorations(PoseStack matrices, int mouseX, int mouseY) {
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.drawBackground();
        int scrollBarX1 = this.getScrollBarX();
        int scrollBarX2 = scrollBarX1 + 6;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        renderHoleBackground(matrices, left, top, right, bottom, 32, 255, 255);
        int rowLeft = this.getRowLeft();
        int startY = this.top + 4 - (int) this.getScroll();
        if (this.renderSelection)
            this.renderHeader(matrices, rowLeft, startY, tesselator);
        this.renderList(matrices, rowLeft, startY, mouseX, mouseY, delta);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        this.renderHoleBackground(matrices, this.left, 0, this.left + this.width, this.top, 64, 255, 255);
        this.renderHoleBackground(matrices, this.left, this.bottom, this.left + this.width, this.height, 64, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        Matrix4f matrix = matrices.last().pose();
        buffer.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(matrix, this.left, this.top + 4, 0.0F).uv(0, 1).color(0, 0, 0, 0).endVertex();
        buffer.vertex(matrix, this.right, this.top + 4, 0.0F).uv(1, 1).color(0, 0, 0, 0).endVertex();
        buffer.vertex(matrix, this.right, this.top, 0.0F).uv(1, 0).color(0, 0, 0, 255).endVertex();
        buffer.vertex(matrix, this.left, this.top, 0.0F).uv(0, 0).color(0, 0, 0, 255).endVertex();
        buffer.vertex(matrix, this.left, this.bottom, 0.0F).uv(0, 1).color(0, 0, 0, 255).endVertex();
        buffer.vertex(matrix, this.right, this.bottom, 0.0F).uv(1, 1).color(0, 0, 0, 255).endVertex();
        buffer.vertex(matrix, this.right, this.bottom - 4, 0.0F).uv(1, 0).color(0, 0, 0, 0).endVertex();
        buffer.vertex(matrix, this.left, this.bottom - 4, 0.0F).uv(0, 0).color(0, 0, 0, 0).endVertex();
        tesselator.end();
        int maxScroll = this.getMaxScroll();
        renderScrollBar(matrices, tesselator, buffer, maxScroll, scrollBarX1, scrollBarX2);
        
        this.renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    
    protected void renderScrollBar(PoseStack matrices, Tesselator tesselator, BufferBuilder buffer, int maxScroll, int scrollBarX1, int scrollBarX2) {
        if (maxScroll > 0) {
            int scrollBarHeight = ((this.bottom - this.top) * (this.bottom - this.top)) / this.getMaxScrollPosition();
            scrollBarHeight = Mth.clamp(scrollBarHeight, 32, this.bottom - this.top - 8);
            int scrollBarY = (int) this.getScroll() * (this.bottom - this.top - scrollBarHeight) / maxScroll + this.top;
            if (scrollBarY < this.top) {
                scrollBarY = this.top;
            }
            
            Matrix4f matrix = matrices.last().pose();
            buffer.begin(7, DefaultVertexFormat.POSITION_COLOR);
            buffer.vertex(matrix, scrollBarX1, this.bottom, 0.0F).color(0, 0, 0, 255).endVertex();
            buffer.vertex(matrix, scrollBarX2, this.bottom, 0.0F).color(0, 0, 0, 255).endVertex();
            buffer.vertex(matrix, scrollBarX2, this.top, 0.0F).color(0, 0, 0, 255).endVertex();
            buffer.vertex(matrix, scrollBarX1, this.top, 0.0F).color(0, 0, 0, 255).endVertex();
            buffer.vertex(matrix, scrollBarX1, scrollBarY + scrollBarHeight, 0.0F).color(128, 128, 128, 255).endVertex();
            buffer.vertex(matrix, scrollBarX2, scrollBarY + scrollBarHeight, 0.0F).color(128, 128, 128, 255).endVertex();
            buffer.vertex(matrix, scrollBarX2, scrollBarY, 0.0F).color(128, 128, 128, 255).endVertex();
            buffer.vertex(matrix, scrollBarX1, scrollBarY, 0.0F).color(128, 128, 128, 255).endVertex();
            buffer.vertex(scrollBarX1, (scrollBarY + scrollBarHeight - 1), 0.0F).color(192, 192, 192, 255).endVertex();
            buffer.vertex(scrollBarX2 - 1, (scrollBarY + scrollBarHeight - 1), 0.0F).color(192, 192, 192, 255).endVertex();
            buffer.vertex(scrollBarX2 - 1, scrollBarY, 0.0F).color(192, 192, 192, 255).endVertex();
            buffer.vertex(scrollBarX1, scrollBarY, 0.0F).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }
    }
    
    protected void centerScrollOn(E item) {
        double d = (this.bottom - this.top) / -2d;
        for (int i = 0; i < this.children().indexOf(item) && i < this.getItemCount(); i++)
            d += getItem(i).getItemHeight();
        this.capYPosition(d);
    }
    
    protected void ensureVisible(E item) {
        int rowTop = this.getRowTop(this.children().indexOf(item));
        int int_2 = rowTop - this.top - 4 - item.getItemHeight();
        if (int_2 < 0)
            this.scroll(int_2);
        int int_3 = this.bottom - rowTop - item.getItemHeight() * 2;
        if (int_3 < 0)
            this.scroll(-int_3);
    }
    
    protected void scroll(int offset) {
        this.capYPosition(this.getScroll() + (double) offset);
        this.yDrag = -2;
    }
    
    public double getScroll() {
        return this.scroll;
    }
    
    public void capYPosition(double double_1) {
        this.scroll = Mth.clamp(double_1, 0.0F, this.getMaxScroll());
    }
    
    protected int getMaxScroll() {
        return Math.max(0, this.getMaxScrollPosition() - (this.bottom - this.top - 4));
    }
    
    public int getScrollBottom() {
        return (int) this.getScroll() - this.height - this.headerHeight;
    }
    
    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= this.getScrollBarX() && mouseX < this.getScrollBarX() + 6;
    }
    
    protected int getScrollBarX() {
        return this.right - 6;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            E item = this.getItemAtPosition(mouseX, mouseY);
            if (item != null) {
                if (item.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused(item);
                    this.setDragging(true);
                    return true;
                }
            } else if (button == 0) {
                this.clickedHeader((int) (mouseX - (double) (this.left + this.width / 2 - this.getItemWidth() / 2)), (int) (mouseY - (double) this.top) + (int) this.getScroll() - 4);
                return true;
            }
            
            return this.scrolling;
        }
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(mouseX, mouseY, button);
        }
        
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        } else if (button == 0 && this.scrolling) {
            if (mouseY < (double) this.top) {
                this.capYPosition(0.0F);
            } else if (mouseY > (double) this.bottom) {
                this.capYPosition(this.getMaxScroll());
            } else {
                double double_5 = Math.max(1, this.getMaxScroll());
                int height = this.bottom - this.top;
                int int_3 = Mth.clamp((int) ((float) (height * height) / (float) this.getMaxScrollPosition()), 32, height - 8);
                double double_6 = Math.max(1.0D, double_5 / (double) (height - int_3));
                this.capYPosition(this.getScroll() + deltaY * double_6);
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (E entry : entries) {
            if (entry.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        this.capYPosition(this.getScroll() - amount * (double) (getMaxScroll() / getItemCount()) / 2.0D);
        return true;
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (super.keyPressed(int_1, int_2, int_3)) {
            return true;
        } else if (int_1 == 264) {
            this.moveSelection(1);
            return true;
        } else if (int_1 == 265) {
            this.moveSelection(-1);
            return true;
        } else {
            return false;
        }
    }
    
    protected void moveSelection(int amount) {
        if (!this.children().isEmpty()) {
            int selectedIndex = this.children().indexOf(this.getSelectedItem());
            selectedIndex = Mth.clamp(selectedIndex + amount, 0, this.getItemCount() - 1);
            E newSelected = this.children().get(selectedIndex);
            this.selectItem(newSelected);
            this.ensureVisible(newSelected);
        }
        
    }
    
    protected void renderList(PoseStack matrices, int startX, int startY, int mouseX, int mouseY, float delta) {
        this.hoveredItem = this.isMouseOver(mouseX, mouseY) ? this.getItemAtPosition(mouseX, mouseY) : null;
        int itemCount = this.getItemCount();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        
        for (int renderIndex = 0; renderIndex < itemCount; ++renderIndex) {
            E item = this.getItem(renderIndex);
            int itemY = startY + headerHeight;
            for (int i = 0; i < entries.size() && i < renderIndex; i++)
                itemY += entries.get(i).getItemHeight();
            int itemHeight = item.getItemHeight() - 4;
            int itemWidth = this.getItemWidth();
            int itemMinX, itemMaxX;
            if (this.selectionVisible && this.isSelected(renderIndex)) {
                itemMinX = this.left + this.width / 2 - itemWidth / 2;
                itemMaxX = itemMinX + itemWidth;
                RenderSystem.disableTexture();
                float outlineColor = this.isFocused() ? 1.0F : 0.5F;
                Matrix4f matrix = matrices.last().pose();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                buffer.begin(7, DefaultVertexFormat.POSITION_COLOR);
                buffer.vertex(matrix, itemMinX, itemY + itemHeight + 2, 0.0F).color(outlineColor, outlineColor, outlineColor, 1.0F).endVertex();
                buffer.vertex(matrix, itemMaxX, itemY + itemHeight + 2, 0.0F).color(outlineColor, outlineColor, outlineColor, 1.0F).endVertex();
                buffer.vertex(matrix, itemMaxX, itemY - 2, 0.0F).color(outlineColor, outlineColor, outlineColor, 1.0F).endVertex();
                buffer.vertex(matrix, itemMinX, itemY - 2, 0.0F).color(outlineColor, outlineColor, outlineColor, 1.0F).endVertex();
                buffer.vertex(matrix, itemMinX + 1, itemY + itemHeight + 1, 0.0F).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.vertex(matrix, itemMaxX - 1, itemY + itemHeight + 1, 0.0F).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.vertex(matrix, itemMaxX - 1, itemY - 1, 0.0F).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.vertex(matrix, itemMinX + 1, itemY - 1, 0.0F).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                tesselator.end();
                RenderSystem.enableTexture();
            }
            
            int y = this.getRowTop(renderIndex);
            int x = this.getRowLeft();
            renderItem(matrices, item, renderIndex, y, x, itemWidth, itemHeight, mouseX, mouseY, Objects.equals(hoveredItem, item), delta);
        }
        
    }
    
    protected void renderItem(PoseStack matrices, E item, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        item.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
    }
    
    protected int getRowLeft() {
        return this.left + this.width / 2 - this.getItemWidth() / 2 + 2;
    }
    
    protected int getRowTop(int index) {
        int integer = top + 4 - (int) this.getScroll() + headerHeight;
        for (int i = 0; i < entries.size() && i < index; i++)
            integer += entries.get(i).getItemHeight();
        return integer;
    }
    
    protected boolean isFocused() {
        return false;
    }
    
    protected void renderHoleBackground(PoseStack matrices, int x1, int y1, int x2, int y2, int color, int alpha1, int alpha2) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        bindTexture(backgroundLocation);
        Matrix4f matrix = matrices.last().pose();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(matrix, x1, y2, 0.0F).uv(x1 / BACKGROUND_TILE_SIZE, y2 / BACKGROUND_TILE_SIZE).color(color, color, color, alpha2).endVertex();
        buffer.vertex(matrix, x2, y2, 0.0F).uv(x2 / BACKGROUND_TILE_SIZE, y2 / BACKGROUND_TILE_SIZE).color(color, color, color, alpha2).endVertex();
        buffer.vertex(matrix, x2, y1, 0.0F).uv(x2 / BACKGROUND_TILE_SIZE, y1 / BACKGROUND_TILE_SIZE).color(color, color, color, alpha1).endVertex();
        buffer.vertex(matrix, x1, y1, 0.0F).uv(x1 / BACKGROUND_TILE_SIZE, y1 / BACKGROUND_TILE_SIZE).color(color, color, color, alpha1).endVertex();
        tesselator.end();
    }
    
    protected E remove(int index) {
        E item = this.entries.get(index);
        return this.removeEntry(item) ? item : null;
    }
    
    protected boolean removeEntry(E item) {
        boolean removed = this.entries.remove(item);
        if (removed && item == this.getSelectedItem()) {
            this.selectItem(null);
        }
        
        return removed;
    }
    
    public static final class SmoothScrollingSettings {
        public static final double CLAMP_EXTENSION = 200;
        
        private SmoothScrollingSettings() {}
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry<E extends Entry<E>> extends Widget implements GuiEventListener {
        private ListWidget<E> parent;
        
        public Entry() {
        }
        
        public abstract void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta);
        
        @Override
        public boolean containsMouse(double mouseX, double mouseY) {
            return Objects.equals(this.parent.getItemAtPosition(mouseX, mouseY), this);
        }
        
        public ListWidget<E> getParent() {
            return parent;
        }
        
        public void setParent(ListWidget<E> parent) {
            this.parent = parent;
        }
        
        public abstract int getItemHeight();
        
        @Deprecated
        public int getMorePossibleHeight() {
            return -1;
        }
        
        @Override
        public final void render(PoseStack poseStack, int i, int j, float f) {
            throw new UnsupportedOperationException();
        }
    }
    
    @Environment(EnvType.CLIENT)
    class Entries extends AbstractList<E> {
        private final List<E> items = new ArrayList<>();
        
        @Override
        public void clear() {
            items.clear();
        }
        
        @Override
        public E get(int index) {
            return this.items.get(index);
        }
        
        @Override
        public int size() {
            return this.items.size();
        }
        
        @Override
        public E set(int index, E element) {
            element.setParent(ListWidget.this);
            return this.items.set(index, element);
        }
        
        @Override
        public void add(int index, E element) {
            element.setParent(ListWidget.this);
            this.items.add(index, element);
        }
        
        @Override
        public E remove(int index) {
            return this.items.remove(index);
        }
    }
}
