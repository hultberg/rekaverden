package net.mittnett.reke.Rekeverden.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityListener implements Listener {

  @org.bukkit.event.EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onEntityExplode(EntityExplodeEvent event) {
    if (event.getEntityType() == EntityType.CREEPER) {
      event.setCancelled(true);
    }
  }

}


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/listeners/EntityListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
