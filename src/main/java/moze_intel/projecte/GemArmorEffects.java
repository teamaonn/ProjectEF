package moze_intel.projecte;

import java.util.ArrayList;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class GemArmorEffects {
    private static final String OFFENSE = "GemOffense";
    private static final String BOOT_MOVEMENT = "GemBootMovement";
    private static final double REPEL_RADIUS = 5;

    private GemArmorEffects() { }

    static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
                !(entity instanceof Player player
                        && (source.is(DamageTypeTags.IS_FIRE) && wears(player, EquipmentSlot.CHEST, "gem_chestplate")
                        || source.is(DamageTypeTags.IS_FALL) && hasFlight(player)
                        || source.is(DamageTypes.STARVE) && hasFullSet(player))));
    }

    public static boolean hasFlight(Player player) {
        return wears(player, EquipmentSlot.FEET, "gem_boots");
    }

    public static boolean hasHelmet(Player player) {
        return wears(player, EquipmentSlot.HEAD, "gem_helmet");
    }

    private static boolean wears(Player player, EquipmentSlot slot, String id) {
        return player.getItemBySlot(slot).is(BuiltInRegistries.ITEM.getValue(PECore.id(id)));
    }

    private static boolean hasGreaves(Player player) {
        return wears(player, EquipmentSlot.LEGS, "gem_leggings");
    }

    private static boolean hasFullSet(Player player) {
        return hasHelmet(player) && wears(player, EquipmentSlot.CHEST, "gem_chestplate")
                && hasGreaves(player) && hasFlight(player);
    }

    private static boolean enabled(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBooleanOr(key, false);
    }

    static void tick(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator()) return;
        if (player.tickCount % 20 == 0) {
            if (hasHelmet(player)) player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false));
            if (hasFlight(player)) player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, true, false));
            if (wears(player, EquipmentSlot.CHEST, "gem_chestplate")) player.clearFire();
        }
        if (player.getLastClientInput().shift() && greavesArmed(player)) {
            repel(player, player.tickCount % 5 == 0);
        }
    }

    private static boolean greavesArmed(Player player) {
        return hasGreaves(player) && enabled(player.getItemBySlot(EquipmentSlot.LEGS), OFFENSE);
    }

    private static void repel(ServerPlayer player, boolean pushMobs) {
        for (var entity : player.level().getEntities(player, player.getBoundingBox().inflate(REPEL_RADIUS),
                target -> target.isAlive() && (pushMobs && target instanceof Mob
                        || target instanceof Projectile shot && shot.getOwner() != player))) {
            Vec3 away = entity.position().subtract(player.position());
            if (away.lengthSqr() > REPEL_RADIUS * REPEL_RADIUS || away.lengthSqr() < 1.0E-6) continue;

            if (entity instanceof Mob mob) {
                Vec3 outward = new Vec3(away.x, 0, away.z).normalize();
                double impulse = Math.max(0, .55 - mob.getDeltaMovement().dot(outward));
                mob.push(outward.x * impulse, .12, outward.z * impulse);
                mob.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 15, 1, true, false));
            } else {
                Vec3 outward = away.normalize();
                double outwardSpeed = entity.getDeltaMovement().dot(outward);
                if (outwardSpeed < 0) {
                    Vec3 deflection = outward.scale(.45 - outwardSpeed);
                    entity.push(deflection.x, deflection.y, deflection.z);
                }
            }
        }
    }

    public static void move(Player player, boolean jump, boolean forward) {
        if (!player.isAlive() || player.isSpectator() || player.isFallFlying() || player.isPassenger()
                || player.isInWater() || player.isInLava()) return;

        boolean flying = player.getAbilities().flying;
        boolean sneaking = player.isShiftKeyDown();
        var velocity = player.getDeltaMovement();
        if (!player.onGround() && sneaking && (flying ? !jump : velocity.y < 0) && greavesArmed(player)) {
            if (velocity.y > -1.5) {
                player.setDeltaMovement(velocity.x, Math.max(-1.5, velocity.y - .22), velocity.z);
            }
            return;
        }
        if (flying || sneaking || !hasFlight(player)
                || !enabled(player.getItemBySlot(EquipmentSlot.FEET), BOOT_MOVEMENT)) return;
        if (forward && player.getFoodData().getFoodLevel() > 6 && !player.horizontalCollision) {
            player.setSprinting(true);
        }
        if (!player.onGround()) {
            double lift = jump ? Math.min(.55, Math.max(.12, velocity.y + .16)) : Math.max(-.12, velocity.y);
            double acceleration = forward && velocity.horizontalDistanceSqr() < .36 ? 1.035 : 1;
            player.setDeltaMovement(velocity.x * acceleration, lift, velocity.z * acceleration);
        }
    }

    public static boolean supportsControl(Player player, int command) {
        if (command == 4) return hasFlight(player);
        if (isProjectEItem(player.getMainHandItem()) || isProjectEItem(player.getOffhandItem())) return false;
        boolean helmet = hasHelmet(player);
        boolean chest = wears(player, EquipmentSlot.CHEST, "gem_chestplate");
        return command == 0 && (helmet || chest || hasGreaves(player))
                || command == 2 && helmet || command == 3 && chest;
    }

    private static boolean isProjectEItem(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(PECore.MODID);
    }

    static void action(ServerPlayer player, int command) {
        if (!supportsControl(player, command) || !ClassicRingItem.canUse(player)
                || player.containerMenu != player.inventoryMenu) return;
        if (command == 4) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            boolean active = !enabled(boots, BOOT_MOVEMENT);
            CustomData.update(DataComponents.CUSTOM_DATA, boots, tag -> tag.putBoolean(BOOT_MOVEMENT, active));
            PowerFuel.notice(player, active ? "Hurricane Boots movement on" : "Hurricane Boots movement off");
            return;
        }
        if (command == 0) {
            var pieces = new ArrayList<ItemStack>(3);
            if (hasHelmet(player)) pieces.add(player.getItemBySlot(EquipmentSlot.HEAD));
            if (wears(player, EquipmentSlot.CHEST, "gem_chestplate")) pieces.add(player.getItemBySlot(EquipmentSlot.CHEST));
            if (hasGreaves(player)) pieces.add(player.getItemBySlot(EquipmentSlot.LEGS));
            boolean armed = pieces.stream().anyMatch(stack -> !enabled(stack, OFFENSE));
            for (ItemStack piece : pieces) {
                CustomData.update(DataComponents.CUSTOM_DATA, piece, tag -> tag.putBoolean(OFFENSE, armed));
            }
            PowerFuel.notice(player, armed ? "Gem powers armed" : "Gem powers disarmed");
            return;
        }
        ItemStack armor = player.getItemBySlot(command == 3 ? EquipmentSlot.CHEST : EquipmentSlot.HEAD);
        if (!enabled(armor, OFFENSE) || player.getCooldowns().isOnCooldown(armor)) return;
        var level = (ServerLevel) player.level();
        if (command == 2) {
            Vec3 eye = player.getEyePosition();
            var hit = level.clip(new ClipContext(eye, eye.add(player.getLookAngle().scale(48)),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (hit.getType() != HitResult.Type.BLOCK
                    || !ClassicRingItem.canChange(level, player, armor, hit.getBlockPos())
                    || !PowerFuel.spend(player, 128)) return;
            var bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
            if (bolt != null) {
                bolt.snapTo(hit.getLocation());
                bolt.setCause(player);
                level.addFreshEntity(bolt);
            }
        } else {
            if (!level.getGameRules().get(GameRules.TNT_EXPLODES)
                    || !ClassicRingItem.canChange(level, player, armor, player.blockPosition())
                    || !PowerFuel.spend(player, 128)) return;
            var nova = new PrimedTnt(level, player.getX(), player.getY(), player.getZ(), player);
            nova.setBlockState(BuiltInRegistries.BLOCK.getValue(PECore.id("nova_catalyst")).defaultBlockState());
            NovaChargeBlock.detonate(nova);
        }
        player.getCooldowns().addCooldown(armor, 40);
    }
}
