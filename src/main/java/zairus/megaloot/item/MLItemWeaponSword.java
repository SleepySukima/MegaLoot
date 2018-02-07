package zairus.megaloot.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zairus.megaloot.MegaLoot;
import zairus.megaloot.loot.LootItemHelper;

public class MLItemWeaponSword extends ItemSword
{
	protected MLItemWeaponSword()
	{
		super(ToolMaterial.DIAMOND);
		
		this.setCreativeTab(MegaLoot.creativeTabMain);
		
		this.addPropertyOverride(new ResourceLocation("model"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
			{
				float model = 1.0F;
				
				model = (float)LootItemHelper.getLootIntValue(stack, MLItem.LOOT_TAG_MODEL);
				
				return model;
			}
		});
	}
	
	@Override
	public int getMaxDamage(ItemStack stack)
	{
		return LootItemHelper.getMaxDamage(stack);
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		boolean hit = super.hitEntity(stack, target, attacker);
		
		MLItem.handleEffectsAfterHit(stack, target, attacker);
		
		return hit;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		
		int attackDamage = LootItemHelper.getLootIntValue(stack, MLItem.LOOT_TAG_DAMAGE);
		float attackSpeed = LootItemHelper.getLootFloatValue(stack, MLItem.LOOT_TAG_SPEED);
		
		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
			
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)attackSpeed, 0));
		}
		
		return multimap;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		int attackDamage = LootItemHelper.getLootIntValue(stack, MLItem.LOOT_TAG_DAMAGE);
		float speedDisplay = LootItemHelper.getLootFloatValue(stack, MLItem.LOOT_TAG_SPEED);
		double sp1 = (double)speedDisplay;
		
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		if (player != null)
		{
			sp1 += player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
		}
		
		attackDamage += EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
		
		tooltip.add("");
		tooltip.add(TextFormatting.GRAY + "" + attackDamage + " Damage | " + ItemStack.DECIMALFORMAT.format(sp1) + " Atack Speed");
		tooltip.add(TextFormatting.WHITE + ItemStack.DECIMALFORMAT.format(((float)attackDamage * sp1)) + " DPS");
		tooltip.add("");
		
		LootItemHelper.addInformation(stack, tooltip);
	}
}
