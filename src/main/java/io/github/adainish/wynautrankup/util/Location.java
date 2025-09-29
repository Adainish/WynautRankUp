package io.github.adainish.wynautrankup.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a (configurable) location in the game. Allowing for location tracking, manipulation, and teleportation
 * to and from locations.
 */
public class Location
{
    /**
     * The world the location is in, uses minecrafts resouce id system. ie. "minecraft:overworld"
     */
    public String world = "";
    /**
     * The x, y, and z coordinates of the location.
     */
    public double x = 0;
    public double y = 0;
    public double z = 0;
    /**
     * The yaw and pitch of the location.
     */
    public float yaw = 0;
    public float pitch = 0;

    /**
     * Creates a new location with the given world, x, y, and z coordinates.
     * @param world The world the location is in.
     * @param x The x coordinate of the location.
     * @param y The y coordinate of the location.
     * @param z The z coordinate of the location.
     * @param yaw The yaw of the location.
     * @param pitch The pitch of the location.
     */
    public Location(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }


    /**
     * returns the server level of the location, if it exists otherwise returns null
     * @return the server level of the location
     */
    @Nullable
    public ServerLevel getServerLevel() {
        if (ResourceLocationHelper.getWorld(this.world).isPresent())
            return ResourceLocationHelper.getWorld(this.world).get();
        else return null;
    }

    /**
     * Gets the world of this location. Should be an identifier for the world.
     * @return The world of this location.
     */
    public String getWorld() {
        return world;
    }

    public ServerLevel getServerWorld()
    {
        if (ResourceLocationHelper.getWorld(this.world).isPresent())
            return ResourceLocationHelper.getWorld(this.world).get();
        else return null;
    }

    /**
     * Gets the x coordinate of this location.
     * @return The x coordinate of this location.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y coordinate of this location.
     * @return The y coordinate of this location.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the z coordinate of this location.
     * @return The z coordinate of this location.
     */
    public double getZ() {
        return z;
    }

    /**
     * Gets the yaw of this location.
     * @return The yaw of this location.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Gets the pitch of this location.
     * @return The pitch of this location.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the world of this location. Should be an identifier for the world.
     * @param world The world to set.
     */
    public void setWorld(String world) {
        this.world = world;
    }

    /** Sets the x coordinate of this location.
     * @param x The x coordinate to set.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate of this location.
     * @param y The y coordinate to set.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Sets the z coordinate of this location.
     * @param z The z coordinate to set.
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Sets the yaw of this location.
     * @param yaw The yaw to set.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Sets the pitch of this location.
     * @param pitch The pitch to set.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Sets the x, y, and z coordinates of this location.
     * @param loc The location to set.
     */
    public void set(Location loc) {
        this.world = loc.getWorld();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    /**
     * Adds the x, y, and z coordinates to this location.
     * @param x The x coordinate to add.
     * @param y The y coordinate to add.
     * @param z The z coordinate to add.
     */
    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    /**
     * Adds the x, y, and z coordinates of the given location to this location.
     * @param loc The location to add.
     */
    public void add(Location loc) {
        this.x += loc.getX();
        this.y += loc.getY();
        this.z += loc.getZ();
    }

    /**
     * Subtracts the x, y, and z coordinates from this location.
     * @param x The x coordinate to subtract.
     * @param y The y coordinate to subtract.
     * @param z The z coordinate to subtract.
     */
    public void subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    /**
     * Subtracts the x, y, and z coordinates of the given location from this location.
     * @param loc The location to subtract.
     */
    public void subtract(Location loc) {
        this.x -= loc.getX();
        this.y -= loc.getY();
        this.z -= loc.getZ();
    }

    /**
     * Returns the distance between this location and another location.
     * @param loc The location to compare to.
     * @return The distance between this location and the other location.
     */
    public double distance(Location loc) {
        return Math.sqrt(Math.pow(loc.getX() - x, 2) + Math.pow(loc.getY() - y, 2) + Math.pow(loc.getZ() - z, 2));
    }

    /**
     * Returns the distance squared between this location and another location.
     * @param loc The location to compare to.
     * @return The distance squared between this location and the other location.
     */
    public double distanceSquared(Location loc) {
        return Math.pow(loc.getX() - x, 2) + Math.pow(loc.getY() - y, 2) + Math.pow(loc.getZ() - z, 2);
    }

    /**
     * Teleports the given player to this location.
     * @param player The player to teleport.
     */
    public void teleport(ServerPlayer player)
    {

        if (player != null)
        {
            if (this.getServerWorld() == null)
            {
                player.sendSystemMessage(Component.literal("The world for this area was not accessible, please contact a staff member if this issue persists!").withStyle(ChatFormatting.GRAY));
                return;
            }

            ServerLevel world = this.getServerWorld();
            player.teleportTo(world, this.x, this.y, this.z, player.getXRot(), player.getYHeadRot());
            player.sendSystemMessage(Component.literal("Teleported you to your destination!").withStyle(ChatFormatting.GREEN));
        }
    }

    /**
     * Gets a list of players near this location within a 15 block radius on the x and z axis, and a 20 block radius on the y axis.
     * @return A list of players near this location.
     */
    public List<ServerPlayer> getPlayersNearLocation()
    {
        List<ServerPlayer> entities = new ArrayList<>();

        ServerLevel world = this.getServerWorld();
        if (world != null) {
            Vec3 vec1 = new net.minecraft.world.phys.Vec3(this.x - 15, this.y - 20, this.z - 30);
            Vec3 vec2 = new net.minecraft.world.phys.Vec3(this.x + 15, this.y + 20, this.z + 30);
            AABB isWithinAABB = new AABB(vec1, vec2);
            List<ServerPlayer> playerList = new ArrayList<>(world.getEntitiesOfClass(ServerPlayer.class, isWithinAABB));
            entities.addAll(playerList);
        }
        return entities;
    }

    /**
     * Checks if there are no players near this location within a 15 block radius on the x and z axis, and a 20 block radius on the y axis.
     * @return True if there are no players near this location, false otherwise.
     */
    public boolean isAvailable()
    {
        return this.getPlayersNearLocation().isEmpty();
    }
}
