
package dev.game.entities.statics.placeables;

import java.awt.Graphics;

import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.gfx.Assets;
import dev.game.utils.Utils;

/**
 * EntityWoodenFence.java - Fence
 *
 * @author Juhyung Kim
 */
public class EntityWoodenFence extends EntityFence {

	public EntityWoodenFence(Handler handler, float x, float y) {

		super(handler, EntityId.WOODEN_FENCE, x, y, 50, EntityName.WOODEN_FENCE);

		texture = Assets.fence_wood[UP_DOWN]; // default
	}

	@Override
	public void render(Graphics gfx) {

		texture = (alignment == UP_DOWN) ? Assets.fence_wood[UP_DOWN]
				: Assets.fence_wood[LEFT_RIGHT];

		if (isCarried)
			texture = Utils.setAlpha(texture, 150);

		gfx.drawImage(texture, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

}
