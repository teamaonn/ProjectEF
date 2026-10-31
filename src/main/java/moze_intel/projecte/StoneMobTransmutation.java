package moze_intel.projecte;

import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;

/** Server-ticked particle projectiles; no client entity or client-chosen target is trusted. */
public final class StoneMobTransmutation {
 public static final int COST=384;
 private static final List<EntityType<?>> PASSIVE=List.of(EntityTypes.CHICKEN,EntityTypes.COW,EntityTypes.PIG,EntityTypes.SHEEP,EntityTypes.RABBIT,EntityTypes.VILLAGER);
 private static final List<EntityType<?>> NEUTRAL=List.of(EntityTypes.WOLF,EntityTypes.ENDERMAN,EntityTypes.BEE,EntityTypes.POLAR_BEAR,EntityTypes.ZOMBIFIED_PIGLIN);
 private static final List<EntityType<?>> HOSTILE=List.of(EntityTypes.CREEPER,EntityTypes.SKELETON,EntityTypes.ZOMBIE,EntityTypes.SLIME,EntityTypes.HUSK,EntityTypes.STRAY);
 private static final List<EntityType<?>> ARTHROPOD=List.of(EntityTypes.SPIDER,EntityTypes.CAVE_SPIDER,EntityTypes.SILVERFISH,EntityTypes.ENDERMITE);
 private static final Set<String> FUELS=Set.of("minecraft:coal","minecraft:charcoal","minecraft:redstone","minecraft:glowstone_dust","minecraft:gunpowder","minecraft:coal_block","minecraft:redstone_block","minecraft:glowstone","projecte:alchemical_coal","projecte:mobius_fuel","projecte:aeternalis_fuel","projecte:alchemical_coal_block","projecte:mobius_fuel_block","projecte:aeternalis_fuel_block");
 private static final List<Shot> SHOTS=new ArrayList<>();
 private static final class Shot {
  final ServerPlayer owner;final ItemStack stone;final ServerLevel level;final Vec3 velocity;Vec3 pos;int age;
  Shot(ServerPlayer p,ItemStack s){owner=p;stone=s;level=(ServerLevel)p.level();pos=p.getEyePosition();velocity=p.getLookAngle().scale(1.5);}
 }
 private StoneMobTransmutation(){}
 public static void register(){
  ServerTickEvents.END_SERVER_TICK.register(StoneMobTransmutation::tick);
  ServerLifecycleEvents.SERVER_STOPPED.register(server->SHOTS.removeIf(s->s.level.getServer()==server));
 }
 public static void shoot(ServerPlayer p,ItemStack stone){
  if(plan(p,stone)==null){p.sendSystemMessage(Component.literal("Mob transmutation needs 384 EMC of inventory fuel"),true);return;}
  SHOTS.add(new Shot(p,stone));
 }
 private static void tick(MinecraftServer server){
  for(var it=SHOTS.iterator();it.hasNext();){Shot s=it.next();if(s.level.getServer()!=server)continue;
   if(++s.age>30||!s.owner.isAlive()||s.owner.hasDisconnected()||s.owner.level()!=s.level||!held(s.owner,s.stone)){it.remove();continue;}
   Vec3 end=s.pos.add(s.velocity);if(!s.level.isLoaded(BlockPos.containing(end))){it.remove();continue;}
   BlockHitResult block=s.level.clip(new ClipContext(s.pos,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,s.owner));
   Vec3 stop=block.getType()==HitResult.Type.BLOCK?block.getLocation():end;
   Mob target=null;double nearest=s.pos.distanceToSqr(stop)+0.000001;
   for(Mob mob:s.level.getEntitiesOfClass(Mob.class,new AABB(s.pos,stop).inflate(1),e->e.isAlive()&&e.isPickable())){
    var hit=mob.getBoundingBox().inflate(.15).clip(s.pos,stop);
    if(hit.isPresent()){double d=s.pos.distanceToSqr(hit.get());if(d<nearest){nearest=d;target=mob;}}
   }
   for(int i=0;i<4;i++){Vec3 at=s.pos.lerp(stop,i/4.0);s.level.sendParticles(ParticleTypes.PORTAL,at.x,at.y,at.z,2,.02,.02,.02,0);}
   if(target!=null){tryTransmute(s.owner,s.stone,target);it.remove();}
   else if(block.getType()==HitResult.Type.BLOCK)it.remove();else s.pos=end;
  }
 }
 private static boolean held(ServerPlayer p,ItemStack stone){return (p.getMainHandItem()==stone||p.getOffhandItem()==stone)&&stone.getItem() instanceof PhilosophersStoneItem;}
 public static List<EntityType<?>> outcomes(Mob mob){
  if(mob instanceof TamableAnimal pet&&pet.isTame()||mob instanceof Leashable leash&&leash.isLeashed()||mob.isPassenger()||mob.isVehicle())return List.of();
  List<EntityType<?>> pool;
  if(mob instanceof Spider||ARTHROPOD.contains(mob.getType()))pool=ARTHROPOD;
  else if(mob instanceof NeutralMob)pool=NEUTRAL;
  else if(mob instanceof Enemy)pool=HOSTILE;
  else if(mob instanceof Animal||mob instanceof AbstractVillager)pool=PASSIVE;
  else return List.of();
  return pool.stream().filter(t->t!=mob.getType()).toList();
 }
 public static boolean tryTransmute(ServerPlayer player,ItemStack stone,Mob old){
  if(!held(player,stone)||PhilosophersStoneItem.charge(stone)==0||!player.mayBuild()||player.isSpectator()||!old.isAlive()||old.level()!=player.level()||old.distanceToSqr(player)>4096)return false;
  ServerLevel level=(ServerLevel)old.level();if(!level.mayInteract(player,old.blockPosition()))return false;
  List<EntityType<?>> choices=outcomes(old);FuelPlan fuel=plan(player,stone);if(choices.isEmpty()||fuel==null)return false;
  Entity entity=choices.get(level.getRandom().nextInt(choices.size())).create(level,EntitySpawnReason.CONVERSION);
  if(!(entity instanceof Mob replacement))return false;
  replacement.snapTo(old.position(),old.getYRot(),old.getXRot());
  replacement.finalizeSpawn(level,level.getCurrentDifficultyAt(old.blockPosition()),EntitySpawnReason.CONVERSION,null);
  if(old instanceof AgeableMob a&&replacement instanceof AgeableMob b)b.setAge(a.getAge());
  if(old.hasCustomName()){replacement.setCustomName(old.getCustomName());replacement.setCustomNameVisible(old.isCustomNameVisible());replacement.setPersistenceRequired();}
  if(!level.getWorldBorder().isWithinBounds(replacement.blockPosition())||!level.noBlockCollision(replacement,replacement.getBoundingBox()))return false;
  if(!level.addFreshEntity(replacement))return false;
  fuel.consume(player,stone);old.discard();
  level.sendParticles(ParticleTypes.PORTAL,replacement.getX(),replacement.getY()+.5,replacement.getZ(),24,.4,.5,.4,.1);return true;
 }
 public static long fuelBuffer(ItemStack stone){return Math.clamp(stone.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getLongOr("StoneFuel",0),0,1_000_000);}
 private record FuelPlan(Map<Integer,Integer> slots,long remainder,boolean creative){
  void consume(ServerPlayer p,ItemStack stone){if(creative)return;slots.forEach((slot,count)->p.getInventory().getItem(slot).shrink(count));var tag=stone.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putLong("StoneFuel",remainder);stone.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));p.getInventory().setChanged();}
 }
 private static FuelPlan plan(ServerPlayer p,ItemStack stone){
  if(p.hasInfiniteMaterials())return new FuelPlan(Map.of(),0,true);
  long total=fuelBuffer(stone);Map<Integer,Integer> slots=new LinkedHashMap<>();
  for(int i=0;i<p.getInventory().getContainerSize()&&total<COST;i++){
   ItemStack stack=p.getInventory().getItem(i);String id=BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();if(stack.isEmpty()||!FUELS.contains(id))continue;
   long value=PEEmcOverrides.forPlayer(p).value(stack);if(value<=0||value>1_000_000)continue;
   int count=(int)Math.min(stack.getCount(),(COST-total+value-1)/value);slots.put(i,count);total+=count*value;
  }
  return total<COST?null:new FuelPlan(slots,total-COST,false);
 }
}
