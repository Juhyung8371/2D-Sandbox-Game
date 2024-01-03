
package dev.game.entities.creatures;

import static dev.game.sounds.Sound.ENTITY_PERSON_DIE;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import dev.game.Game;
import dev.game.Handler;
import dev.game.ai.AIWander;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.gfx.Animation;
import dev.game.gfx.Assets;
import dev.game.gfx.Text;
import dev.game.sounds.Sound;
import dev.game.tiles.Tile;
import dev.game.ui.UILabel;
import dev.game.utils.Utils;

/**
 * EntityPerson.java - description here...
 *
 * @author Juhyung Kim
 */
public class EntityPerson extends Creature {

	/**
	 * For label when talking
	 */
	private int maxWidth, maxHeight;

	private boolean isShowingFact;
	private int counter;
	private static final int PERIOD = Game.FPS * 2;

	private UILabel label = null;
	private int factCounter;
	private static final int FACT_PERIOD = Game.FPS * 5;

	/**
	 * Start of sentence.
	 */
	private static final String PETER_SAYS = "Peter says,\n";
	/**
	 * The reservoir of disturbing random facts.
	 */
	private static final String[] FACTS = { "Peter is\nhappy to see you!",
			"Right now you're\nusing your eye hole!",
			"The Romans used crushed\nmouse brains as toothpaste.",
			"16% of cell phones\nhave poop on them",
			"There are\naround 200 corpses\nin this game...\nFind them.",
			"The food here can be\nre-hydrated with urine!",
			"If you're eating non-organic food,\nyou're probably eating Zyklon B,"
					+ "\nthe chemical used during WWII.",
			"Tape worm can grow up to\n30 feets in your body, have fun!" };

	private Random random;

	public EntityPerson(Handler handler, float x, float y) {

		super(handler, EntityId.PERSON, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE,
				Player.PLAYER_ATTACK_DAMAGE * 2, EntityName.PERSON);

		setBounds(12, 12, Tile.TILE_SIZE - 24, Tile.TILE_SIZE - 24);
		setSpeed(DEFAULT_SPEED - 2);
		runnableCreature = false;
		initAnimation();
		setUpAI();

		isShowingFact = false;
		counter = 0;
		factCounter = 0;
		maxWidth = 0;
		maxHeight = 0;
		random = new Random();

	}

	@Override
	public void setUpAI() {

		addTask(new AIWander(1, handler, this));

		/*
		 * addActiveTask(new AIFindTarget(2, handler, this, EntityId.WOLF, 6));
		 * addActiveTask(new AIFindPathToTarget(3, handler, this)); addActiveTask(new
		 * AIChase(4, handler, this));
		 */
	}

	@Override
	public void initAnimation() {
		anim_walk_down = new Animation(1000, Assets.person_walk_down);
		anim_walk_up = new Animation(1000, Assets.person_walk_up);
		anim_walk_right = new Animation(1000, Assets.person_walk_right);
		anim_walk_left = new Animation(1000, Assets.person_walk_left);
		anim_die = new Animation(1000, Assets.person_die, true);
	}

	@Override
	protected void checkShouldDie() {

		if (!anim_die.isAlive())
			shouldDie = true;

	}

	@Override
	public void tick() {

		if (isDying)
			return;

		checkBurning();
		executeTasks();
		updateAnimationTick();
		move();
		checkDeath();
		beAnnoying();

	}

	@Override
	public void render(Graphics gfx) {

		float camX = camera.getXOffset();
		float camY = camera.getYOffset();
		BufferedImage image = getCurrentAnimationFrame();

		if (isDying) {

			anim_die.tick();

			gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width, height,
					null);

			checkShouldDie();

			return;

		}

		renderAnimation(gfx, image, camX, camY);

		setIsMoving(false);

		showFireEffect(gfx);

		Text.drawString(gfx, "Peter", (int) (x - camX) + 32, (int) (y - camY) - 6,
				Color.WHITE, Assets.mediumFont, true);

		renderFact(gfx, camX, camY);

		renderHealthBar(gfx, camX, camY);

	}

	@Override
	public void die() {
		ENTITY_PERSON_DIE.play();
	}

	/**
	 * Render the animation
	 *
	 * @param gfx
	 * @param image
	 * @param camX
	 * @param camY
	 */
	private void renderAnimation(Graphics gfx, BufferedImage image, float camX,
			float camY) {

		gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width, height,
				null);

	}

	/**
	 * Update the animation tick
	 */
	private void updateAnimationTick() {

		// animation
		switch (currentDirection) {

		case Creature.DOWN:
			anim_walk_down.tick();
			break;

		case Creature.UP:
			anim_walk_up.tick();
			break;

		case Creature.RIGHT:
			anim_walk_right.tick();
			break;

		case Creature.LEFT:
			anim_walk_left.tick();
			break;

		}
	}

	/**
	 * To update the animation
	 *
	 * @return
	 */
	private BufferedImage getCurrentAnimationFrame() {

		if (isDying)
			return anim_die.getCurrentFrame();

		switch (this.getDirection()) {

		case Creature.DOWN:
			return anim_walk_down.getCurrentFrame();

		case Creature.UP:
			return anim_walk_up.getCurrentFrame();

		case Creature.RIGHT:
			return anim_walk_right.getCurrentFrame();

		case Creature.LEFT:
			return anim_walk_left.getCurrentFrame();

		default:
			return anim_walk_down.getCurrentFrame();

		}

	}

	/**
	 * Spit out random facts every so often
	 */
	private void beAnnoying() {

		if (isShowingFact)
			return;

		if (counter >= PERIOD)
			isShowingFact = true;
		else
			counter++;

	}

	/**
	 * Render the text for random facts
	 *
	 * @param gfx
	 * @param camX
	 * @param camY
	 */
	private void renderFact(Graphics gfx, float camX, float camY) {

		if (!isShowingFact)
			return;

		if (label == null) {

			FontMetrics matrics = gfx.getFontMetrics(Assets.font);

			int index = random.nextInt(FACTS.length);

			String[] strings = FACTS[index].split("\n");

			int length = strings.length;

			maxWidth = 0;
			maxHeight = 0;

			for (int i = 0; i < length; i++) {

				if (matrics.stringWidth(strings[i]) > maxWidth)
					maxWidth = matrics.stringWidth(strings[i]);

				maxHeight += matrics.getHeight();

			}

			// there's another line added later
			maxHeight += matrics.getHeight();

			label = new UILabel(0, 0, maxWidth + 12, maxHeight + 16,
					Assets.quitDialog);

			label.setText(PETER_SAYS + FACTS[index]);

			Sound.ENTITY_PERSON_TALK.play();

		}

		if (factCounter >= FACT_PERIOD) {

			label = null;

			factCounter = 0;
			isShowingFact = false;
			counter = 0;

			return;

		}

		label.setX(Utils.quickFloor(x - camX) - label.getWidth() / 2 + width / 2);
		label.setY(Utils.quickFloor(y - camY) - label.getHeight() - 4);

		label.render(gfx);

		factCounter++;

	}

}
