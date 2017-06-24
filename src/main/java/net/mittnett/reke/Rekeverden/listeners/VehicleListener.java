package net.mittnett.reke.Rekeverden.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class VehicleListener implements Listener {

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onVehicleExit(VehicleExitEvent event) {
    // Only handle the return of a cart if the entity is Player.
    if (event.getExited() instanceof Player) {
      Player player = (Player) event.getExited();

      event.getVehicle().remove(); // Remove the cart.

      // In creative, the cart is never removed from the player in the first place, by
      // giving the player the minecart back, it ends up with two.
      if (player.getGameMode() != GameMode.CREATIVE) {
        PlayerInventory pinv = player.getInventory();
        ItemStack cart = new ItemStack(Material.MINECART, 1);

        // Is inventory full?
        if (pinv.firstEmpty() > 0) {
          // Add item to the player.
          pinv.addItem(cart);
        } else {
          // Drop item near player with random offset.
          player.getWorld().dropItemNaturally(player.getLocation(), cart);
        }
      }
    }
  }

}
