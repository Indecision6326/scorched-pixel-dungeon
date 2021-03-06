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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class Displacing extends Weapon.Enchantment {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {

		if (Random.Int(12) == 0 && !defender.properties().contains(Char.Property.IMMOVABLE)){
			int count = 10;
			int newPos;
			do {
				newPos = Dungeon.level.randomRespawnCell( defender );
				if (count-- <= 0) {
					break;
				}
			} while (newPos == -1);

			if (newPos != -1 && !Dungeon.bossLevel()) {

				if (Dungeon.level.heroFOV[defender.pos]) {
					CellEmitter.get( defender.pos ).start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
				}

				defender.pos = newPos;
				if (defender instanceof Mob && ((Mob) defender).state == ((Mob) defender).HUNTING){
					((Mob) defender).state = ((Mob) defender).WANDERING;
				}
				defender.sprite.place( defender.pos );
				defender.sprite.visible = Dungeon.level.heroFOV[defender.pos];

				if (hero.heroClass == HeroClass.HERETIC){
					float pow = 5f + Random.NormalFloat(weapon.buffedLvl()*0.5f, weapon.buffedLvl()*1.5f);
					Buff.affect(defender, Blindness.class, pow);
					Buff.append(Dungeon.hero, TalismanOfForesight.CharAwareness.class, pow).charID = defender.id();
				}

				return 0;

			}
		}

		return damage;
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

}
