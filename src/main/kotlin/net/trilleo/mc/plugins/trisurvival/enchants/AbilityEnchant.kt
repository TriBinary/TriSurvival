package net.trilleo.mc.plugins.trisurvival.enchants

import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent

/**
 * An enchant that runs custom logic on game events. Override only the hooks you
 * need; all default to no-ops. The hooks are dispatched by
 * `EnchantAbilityListener`, which reads equipped enchants and resolves the level.
 *
 * Add further hooks (e.g. onShootBow, onFish) here as new triggers are needed —
 * the dispatcher and this base class are the only places that change.
 */
abstract class AbilityEnchant(id: String) : CustomEnchant(id) {

    /** Fired when the holder (main hand) melee-attacks an entity. */
    open fun onAttack(player: Player, victim: LivingEntity, level: Int, event: EntityDamageByEntityEvent) {}

    /** Fired when the holder (armor) takes damage. */
    open fun onDamaged(player: Player, source: Entity?, level: Int, event: EntityDamageByEntityEvent) {}

    /** Fired when the holder (tool in hand) breaks a block. */
    open fun onBlockBreak(player: Player, block: Block, level: Int, event: BlockBreakEvent) {}
}
