/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class Overgrowth extends Armor.Glyph {
	
	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		
		if ( Random.Int( 20 ) == 0) {

			Plant p = ((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED)).couch(defender.pos, null);
			
			//momentarily revoke warden benefits, otherwise this curse would be incredibly powerful
			if (defender instanceof Hero && ((Hero) defender).subClass == HeroSubClass.WARDEN){
				((Hero) defender).subClass = HeroSubClass.NONE;
				p.activate( defender );
				((Hero) defender).subClass = HeroSubClass.WARDEN;
			} else {
				p.activate( defender );
			}

			if (hero.heroClass == HeroClass.HERETIC){
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					float pow = 5f + Random.NormalFloat(armor.buffedLvl()*0.5f, armor.buffedLvl()*1.5f);
					if (mob.alignment != Char.Alignment.ALLY
							&& mob.alignment != Char.Alignment.NEUTRAL
							&& Dungeon.level.heroFOV[mob.pos]) {
						Buff.affect(mob, Roots.class, pow);
						CellEmitter.get( mob.pos ).burst( LeafParticle.LEVEL_SPECIFIC, 10 );
					}
				}
			}
			
			CellEmitter.get( defender.pos ).burst( LeafParticle.LEVEL_SPECIFIC, 10 );
			
		}
		
		return damage;
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
	
	@Override
	public boolean curse() {
		return true;
	}
}
