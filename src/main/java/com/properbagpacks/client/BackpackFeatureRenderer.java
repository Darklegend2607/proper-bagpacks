package com.properbagpacks.client;

import com.properbagpacks.BackpackItem;
import com.properbagpacks.BackpackTier;
import com.properbagpacks.ProperBagPacks;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

/** Draws the backpack on the player's back when it is worn in the chest slot. */
public class BackpackFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(ProperBagPacks.id("backpack"), "main");
    private static final Identifier BODY_TEXTURE = ProperBagPacks.id("textures/entity/backpack/body.png");
    private static final Map<BackpackTier, Identifier> TRIM_TEXTURES = new EnumMap<>(BackpackTier.class);

    static {
        for (BackpackTier tier : BackpackTier.values()) {
            TRIM_TEXTURES.put(tier, ProperBagPacks.id("textures/entity/backpack/trim_" + tier.id() + ".png"));
        }
    }

    private final ModelPart body;
    private final ModelPart trim;

    public BackpackFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context,
                                   EntityModelLoader loader) {
        super(context);
        ModelPart root = loader.getModelPart(LAYER);
        this.body = root.getChild("body");
        this.trim = root.getChild("trim");
    }

    public static TexturedModelData createModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        root.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4f, 1f, 2f, 8f, 10f, 4f), ModelTransform.NONE);
        root.addChild("trim", ModelPartBuilder.create().uv(0, 0).cuboid(-4.5f, 3f, 1.8f, 9f, 1.5f, 4.4f), ModelTransform.NONE);
        return TexturedModelData.of(data, 64, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta,
                       float animationProgress, float headYaw, float headPitch) {
        if (player.isInvisible()) return;
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof BackpackItem item)) return;

        int color = DyedColorComponent.getColor(chest, 0xFF000000 | item.tier().bodyColor());

        matrices.push();
        this.getContextModel().body.rotate(matrices);
        body.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(BODY_TEXTURE)),
                light, OverlayTexture.DEFAULT_UV, color);
        trim.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TRIM_TEXTURES.get(item.tier()))),
                light, OverlayTexture.DEFAULT_UV, -1);
        matrices.pop();
    }
}
