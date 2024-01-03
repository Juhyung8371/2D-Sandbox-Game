
package dev.game.gfx;

import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 * Assets.java - All the resources for the game
 *
 * @author j.kim3
 */
public class Assets {

	// fonts
	public static Font font, fontPressed, itemCountFont, titleFont, mediumFont,
			bigFont, bigFontPressed, cordFont;

	// static entities
	public static BufferedImage tree, tallGrass, crafting_table,
			advanced_crafting_table, cave, campfire;
	public static BufferedImage[] fire, fence_wood, fence_stone, torch;

	// creature
	public static BufferedImage[] wolf_die, wolf_idle_down, wolf_walk_down,
			wolf_walk_up, wolf_idle_up, wolf_walk_right, wolf_idle_right,
			wolf_walk_left, wolf_idle_left, wolf_run_right, wolf_run_left,
			wolf_attack_right, wolf_attack_left, wolf_attack_down;

	public static BufferedImage[] treant_walk_up, treant_walk_down, treant_walk_left,
			treant_walk_right, treant_attack_up, treant_attack_down,
			treant_attack_left, treant_attack_right, treant_die;

	public static BufferedImage[] golem_walk_up, golem_walk_down, golem_walk_left,
			golem_walk_right, golem_attack_up, golem_attack_down, golem_attack_left,
			golem_attack_right, golem_die;

	public static BufferedImage[] person_walk_up, person_walk_down, person_walk_left,
			person_walk_right, person_die;

	// player
	public static BufferedImage panda_ghost;

	public static BufferedImage[] panda_idle_down, panda_walk_down, panda_idle_up,
			panda_walk_up, panda_idle_right, panda_walk_right, panda_idle_left,
			panda_walk_left, panda_attack_up, panda_attack_down, panda_attack_right,
			panda_attack_left;

	// tiles
	public static BufferedImage airTile, stoneTile, grassTile, sandTile, dirtTile,
			grassyDirtTile, topsoilTile, gravelTile, grassyGravelTile, ocherTile,
			mudTile, grassyMudTile, snowTile;

	public static BufferedImage[] water;

	// water_UpDownLeftRight (O for open to water, C for closed)
	public static BufferedImage water_cccc, water_ccco, water_ccoo, water_ccoc,
			water_cocc, water_oocc, water_occc, water_coco, water_cooo, water_cooc,
			water_ooco, water_oooc, water_occo, water_ocoo, water_ococ;

	// items
	public static BufferedImage itemWood, itemStone, itemFence_wood, itemFence_stone,
			itemApple, itemTorch, itemHammer, itemShovel;

	// particles
	public static BufferedImage[] crackParticle, pandaAttackParticle_up,
			pandaAttackParticle_down, pandaAttackParticle_left,
			pandaAttackParticle_right;

	// UI graphics
	public static BufferedImage background;
	public static BufferedImage inventory, quickSlot, inventory_selected;

	public static BufferedImage[] inv_del_btn, inv_quit_btn;

	public static BufferedImage[] progressBar;
	public static BufferedImage[] btn_start;

	public static BufferedImage quitDialog;
	public static BufferedImage[] btn_quitDialog;

	public static BufferedImage crafting;

	public static BufferedImage[] crafting_category;

	public static BufferedImage scroll_up_unpressed, scroll_down_unpressed,
			scroll_up_pressed, scroll_down_pressed, scroll_body, scroll_thumb;

	public static BufferedImage menuState_bg;
	public static BufferedImage[] menuState_mapBtn, menuState_backBtn, menuState_btn;

	public static BufferedImage[] edit_text;

	// paths /////
	private static String fontPath = "/res/fonts/slkscr.ttf";

	private static String itemPath = "/res/textures/item/items.png";

	private static String terrainParentPath = "/res/textures/terrian/";
	private static String terrainPath = terrainParentPath + "terrain.png";
	private static String waterTilePath = terrainParentPath + "water.png";

	private static String UIParentPath = "/res/textures/ui/";
	private static String craftingPath = UIParentPath + "crafting.png";
	private static String editTextPath = UIParentPath + "edit_text.png";
	private static String inventoryPath = UIParentPath + "inventory.png";
	private static String lobbyStatePath = UIParentPath + "lobby_state.png";
	private static String menuStatePath = UIParentPath + "menu_state.png";
	private static String progressBarPath = UIParentPath + "progress_bar.png";
	private static String quitDialogPath = UIParentPath + "quit_dialog.png";
	private static String scrollBarPath = UIParentPath + "scroll_bar.png";

	private static String entityParentPath = "/res/textures/entity/";
	private static String entityFencePath = entityParentPath + "entity_fence.png";
	private static String entityPandaPath = entityParentPath + "entity_panda.png";
	private static String entityWolfPath = entityParentPath + "entity_wolf.png";
	private static String entityTreantPath = entityParentPath + "entity_treant.png";
	private static String entityGolemPath = entityParentPath + "entity_golem.png";
	private static String entityPersonPath = entityParentPath + "entity_person.png";

	private static String entityStaticPath = entityParentPath + "entity_static.png";

	private static String particleParentPath = "/res/textures/particle/";
	private static String particlePath = particleParentPath + "particles.png";

	/**
	 * Load all the images used in the game.
	 */
	public static void init() {

		loadFontFiles();
		loadItemImages();
		loadTerrainImages();
		loadEntityImages();
		loadParticleImages();
		loadUIImages();

	}

	/**
	 * Load all the fonts.
	 */
	private static void loadFontFiles() {

		font = FontLoader.loadFont(fontPath, 24);
		fontPressed = FontLoader.loadFont(fontPath, 22);
		itemCountFont = FontLoader.loadFont(fontPath, 16);
		titleFont = FontLoader.loadFont(fontPath, 80);

		mediumFont = FontLoader.loadFont(fontPath, 40);

		bigFont = FontLoader.loadFont(fontPath, 60);
		bigFontPressed = FontLoader.loadFont(fontPath, 58);

		cordFont = FontLoader.loadFont(fontPath, 10);

	}

	/**
	 * Load all the items images.
	 */
	private static void loadItemImages() {

		SpriteSheet itemSprite = new SpriteSheet(ImageLoader.load(itemPath));

		itemWood = itemSprite.cropItem(1, 0);
		itemStone = itemSprite.cropItem(0, 0);
		itemFence_wood = itemSprite.cropItem(2, 0);
		itemFence_stone = itemSprite.cropItem(3, 0);
		itemApple = itemSprite.cropItem(4, 0);
		itemTorch = itemSprite.cropItem(5, 0);
		itemHammer = itemSprite.cropItem(0, 1);
		itemShovel = itemSprite.cropItem(1, 1);
	}

	/**
	 * Load all the block images.
	 */
	private static void loadTerrainImages() {

		// water
		SpriteSheet waterSprite = new SpriteSheet(ImageLoader.load(waterTilePath));

		water = new BufferedImage[6];
		water[0] = waterSprite.crop(1, 2);
		water[1] = waterSprite.crop(0, 4);
		water[2] = waterSprite.crop(1, 4);
		water[3] = waterSprite.crop(2, 4);
		water[4] = water[2];
		water[5] = water[1];
		water_cccc = waterSprite.crop(3, 0);
		water_ccco = waterSprite.crop(0, 0);
		water_ccoo = waterSprite.crop(1, 0);
		water_ccoc = waterSprite.crop(2, 0);
		water_cocc = waterSprite.crop(3, 1);
		water_oocc = waterSprite.crop(3, 2);
		water_occc = waterSprite.crop(3, 3);
		water_coco = waterSprite.crop(0, 1);
		water_cooo = waterSprite.crop(1, 1);
		water_cooc = waterSprite.crop(2, 1);
		water_ooco = waterSprite.crop(0, 2);
		water_oooc = waterSprite.crop(2, 2);
		water_occo = waterSprite.crop(0, 3);
		water_ocoo = waterSprite.crop(1, 3);
		water_ococ = waterSprite.crop(2, 3);

		// tiles
		SpriteSheet tileSheet = new SpriteSheet(ImageLoader.load(terrainPath));

		airTile = tileSheet.crop(0, 0);
		stoneTile = tileSheet.crop(1, 0);
		grassTile = tileSheet.crop(2, 0);
		sandTile = waterSprite.crop(3, 4);
		dirtTile = tileSheet.crop(3, 0);
		grassyDirtTile = tileSheet.crop(4, 0);
		topsoilTile = tileSheet.crop(5, 0);
		gravelTile = tileSheet.crop(0, 1);
		grassyGravelTile = tileSheet.crop(1, 1);
		ocherTile = tileSheet.crop(2, 1);
		mudTile = tileSheet.crop(3, 1);
		grassyMudTile = tileSheet.crop(4, 1);
		snowTile = tileSheet.crop(5, 1);

	}

	/**
	 * Load all the UI images.
	 */
	private static void loadUIImages() {

		SpriteSheet lobbyStateSheet = new SpriteSheet(
				ImageLoader.load(lobbyStatePath));

		background = lobbyStateSheet.cropInPixel(0, 0, 600, 400);

		btn_start = new BufferedImage[2];
		btn_start[0] = lobbyStateSheet.cropInPixel(600, 0, 160, 60);
		btn_start[1] = lobbyStateSheet.cropInPixel(600, 60, 160, 60);

		SpriteSheet bamboo = new SpriteSheet(ImageLoader.load(progressBarPath));
		progressBar = new BufferedImage[2];
		progressBar[0] = bamboo.cropInScale(0, 0, 360, 68);
		progressBar[1] = bamboo.cropInScale(0, 1, 360, 68);

		SpriteSheet invSheet = new SpriteSheet(ImageLoader.load(inventoryPath));

		inventory = invSheet.cropInPixel(0, 0, 600, 400);
		quickSlot = invSheet.cropInPixel(164, 321, 336, 70);
		inventory_selected = invSheet.cropInPixel(600, 0, 70, 70);

		inv_del_btn = new BufferedImage[2];
		inv_del_btn[0] = invSheet.cropInPixel(600, 70, 100, 50);
		inv_del_btn[1] = invSheet.cropInPixel(600, 120, 100, 50);

		inv_quit_btn = new BufferedImage[2];
		inv_quit_btn[0] = invSheet.cropInPixel(600, 170, 48, 48);
		inv_quit_btn[1] = invSheet.cropInPixel(648, 170, 48, 48);

		SpriteSheet quitDialogSheet = new SpriteSheet(
				ImageLoader.load(quitDialogPath));

		quitDialog = quitDialogSheet.cropInPixel(0, 0, 200, 100);

		btn_quitDialog = new BufferedImage[2];
		btn_quitDialog[0] = quitDialogSheet.cropInPixel(201, 0, 80, 30);
		btn_quitDialog[1] = quitDialogSheet.cropInPixel(201, 30, 80, 30);

		SpriteSheet craftingSheet = new SpriteSheet(ImageLoader.load(craftingPath));

		crafting = craftingSheet.cropInPixel(0, 0, 600, 400);

		crafting_category = new BufferedImage[2];
		crafting_category[0] = craftingSheet.cropInPixel(600, 0, 140, 64);
		crafting_category[1] = craftingSheet.cropInPixel(600, 64, 140, 64);

		SpriteSheet scrollSheet = new SpriteSheet(ImageLoader.load(scrollBarPath));

		scroll_up_unpressed = scrollSheet.cropInPixel(0, 0, 10, 10);
		scroll_down_unpressed = scrollSheet.cropInPixel(0, 110, 10, 10);
		scroll_up_pressed = scrollSheet.cropInPixel(10, 0, 10, 10);
		scroll_down_pressed = scrollSheet.cropInPixel(10, 10, 10, 10);
		scroll_body = scrollSheet.cropInPixel(0, 10, 10, 100);
		scroll_thumb = scrollSheet.cropInPixel(10, 20, 10, 100);

		SpriteSheet menuStateSheet = new SpriteSheet(
				ImageLoader.load(menuStatePath));

		menuState_bg = menuStateSheet.cropInPixel(0, 0, 799, 600);

		menuState_mapBtn = new BufferedImage[2];
		menuState_backBtn = new BufferedImage[2];
		menuState_btn = new BufferedImage[2];

		menuState_mapBtn[0] = menuStateSheet.cropInPixel(0, 600, 529, 204);
		menuState_mapBtn[1] = menuStateSheet.cropInPixel(0, 803, 529, 204);

		menuState_btn[0] = menuStateSheet.cropInPixel(529, 600, 191, 72);
		menuState_btn[1] = menuStateSheet.cropInPixel(529, 672, 191, 72);

		menuState_backBtn[0] = menuStateSheet.cropInPixel(720, 600, 95, 35);
		menuState_backBtn[1] = menuStateSheet.cropInPixel(720, 635, 95, 35);

		SpriteSheet editTextSheet = new SpriteSheet(ImageLoader.load(editTextPath));

		edit_text = new BufferedImage[2];
		edit_text[0] = editTextSheet.cropInPixel(0, 0, 400, 40);
		edit_text[1] = editTextSheet.cropInPixel(0, 40, 400, 40);

	}

	/**
	 * Load all the particle images
	 */
	private static void loadParticleImages() {

		SpriteSheet particleSheet = new SpriteSheet(ImageLoader.load(particlePath));

		pandaAttackParticle_up = new BufferedImage[1];
		pandaAttackParticle_down = new BufferedImage[1];
		pandaAttackParticle_left = new BufferedImage[1];
		pandaAttackParticle_right = new BufferedImage[1];

		pandaAttackParticle_left[0] = particleSheet.crop(0, 0);
		pandaAttackParticle_right[0] = SpriteSheet
				.horizontalFlip(pandaAttackParticle_left[0]);
		pandaAttackParticle_down[0] = particleSheet.crop(1, 0);
		pandaAttackParticle_up[0] = SpriteSheet
				.verticalFlip(pandaAttackParticle_down[0]);

		crackParticle = new BufferedImage[4];
		crackParticle[0] = particleSheet.crop(0, 1);
		crackParticle[1] = crackParticle[0];
		crackParticle[2] = particleSheet.crop(1, 1);
		crackParticle[3] = particleSheet.crop(2, 1);

	}

	/**
	 * Load all the entity images.
	 */
	private static void loadEntityImages() {

		loadEntityPandaImages();
		loadEntityFenceImages();
		loadEntityWolfImages();
		loadEntityTreantImages();
		loadEntityGolemImages();
		loadEntityPersonImages();

		loadStaticEntityImages();

	}

	/**
	 * Load all the static entity images.
	 */
	private static void loadStaticEntityImages() {

		SpriteSheet staticEntSheet = new SpriteSheet(
				ImageLoader.load(entityStaticPath));

		torch = new BufferedImage[2];
		torch[0] = staticEntSheet.cropInPixel(76, 2, 8, 29);
		torch[1] = staticEntSheet.cropInPixel(76, 34, 8, 29);

		fire = new BufferedImage[4];
		fire[0] = staticEntSheet.crop(0, 0);
		fire[1] = staticEntSheet.crop(1, 0);
		fire[2] = SpriteSheet.horizontalFlip(fire[0]);
		fire[3] = SpriteSheet.horizontalFlip(fire[1]);

		tree = staticEntSheet.crop(0, 1, 1, 2);
		tallGrass = staticEntSheet.crop(1, 1);
		cave = staticEntSheet.crop(3, 0);
		campfire = staticEntSheet.crop(3, 1);

		crafting_table = staticEntSheet.crop(1, 2, 1, 1);
		advanced_crafting_table = staticEntSheet.crop(2, 2, 1, 1);

	}

	/**
	 * Load all the panda images (player).
	 */
	private static void loadEntityPandaImages() {

		SpriteSheet pandaSheet = new SpriteSheet(ImageLoader.load(entityPandaPath));

		panda_ghost = pandaSheet.cropInPixel(0, 289, 32, 38);

		panda_idle_down = new BufferedImage[2];
		panda_walk_down = new BufferedImage[2];
		panda_idle_up = new BufferedImage[2];
		panda_walk_up = new BufferedImage[2];
		panda_walk_right = new BufferedImage[2];
		panda_idle_right = new BufferedImage[2];
		panda_walk_left = new BufferedImage[2];
		panda_idle_left = new BufferedImage[2];
		panda_attack_down = new BufferedImage[2];
		panda_attack_up = new BufferedImage[2];
		panda_attack_left = new BufferedImage[2];
		panda_attack_right = new BufferedImage[2];
		panda_idle_down[0] = pandaSheet.crop(0, 0);
		panda_idle_down[1] = pandaSheet.crop(1, 0);
		panda_walk_down[0] = pandaSheet.crop(0, 1);
		panda_walk_down[1] = pandaSheet.crop(1, 1);
		panda_idle_up[0] = pandaSheet.crop(0, 2);
		panda_idle_up[1] = pandaSheet.crop(1, 2);
		panda_walk_up[0] = pandaSheet.crop(0, 3);
		panda_walk_up[1] = pandaSheet.crop(1, 3);
		panda_idle_right[0] = pandaSheet.crop(0, 5);
		panda_idle_right[1] = pandaSheet.crop(1, 5);
		panda_walk_right[0] = pandaSheet.crop(0, 4);
		panda_walk_right[1] = pandaSheet.crop(1, 4);
		panda_idle_left[0] = SpriteSheet.horizontalFlip(panda_idle_right[0]);
		panda_idle_left[1] = SpriteSheet.horizontalFlip(panda_idle_right[1]);
		panda_walk_left[0] = SpriteSheet.horizontalFlip(panda_walk_right[0]);
		panda_walk_left[1] = SpriteSheet.horizontalFlip(panda_walk_right[1]);
		panda_attack_up[0] = pandaSheet.cropInScale(1, 7, 38, 32);
		panda_attack_up[1] = pandaSheet.cropInScale(0, 7, 38, 32);
		panda_attack_down[0] = pandaSheet.cropInScale(1, 6, 38, 32);
		panda_attack_down[1] = pandaSheet.cropInScale(0, 6, 38, 32);
		panda_attack_right[0] = pandaSheet.crop(1, 8);
		panda_attack_right[1] = pandaSheet.crop(0, 8);
		panda_attack_left[0] = SpriteSheet.horizontalFlip(panda_attack_right[0]);
		panda_attack_left[1] = SpriteSheet.horizontalFlip(panda_attack_right[1]);

	}

	/**
	 * Load all the Fence images.
	 */
	private static void loadEntityFenceImages() {

		SpriteSheet fenceSheet = new SpriteSheet(ImageLoader.load(entityFencePath));

		fence_wood = new BufferedImage[2];
		fence_wood[0] = fenceSheet.crop(3, 0, 1, 3); // up down
		fence_wood[1] = fenceSheet.crop(0, 0, 3, 1); // left right

		fence_stone = new BufferedImage[2];
		fence_stone[0] = fenceSheet.crop(4, 0, 1, 3);
		fence_stone[1] = fenceSheet.crop(0, 1, 3, 1);
	}

	/**
	 * Load all images for EntityTreant.
	 */
	private static void loadEntityTreantImages() {

		SpriteSheet treantSheet = new SpriteSheet(
				ImageLoader.load(entityTreantPath));

		treant_walk_up = new BufferedImage[4];
		treant_walk_down = new BufferedImage[4];
		treant_walk_left = new BufferedImage[4];
		treant_walk_right = new BufferedImage[4];
		treant_attack_up = new BufferedImage[3];
		treant_attack_down = new BufferedImage[3];
		treant_attack_left = new BufferedImage[3];
		treant_attack_right = new BufferedImage[3];
		treant_die = new BufferedImage[4];

		int walk_width = 30;
		int walk_height = 48;
		int attack_up_width = 38;
		int attack_up_height = 66;
		int attack_down_width = 36;
		int attack_down_height = 64;
		int attack_side_width = 50;
		int attack_side_height = 48;

		treant_walk_up[0] = treantSheet.cropInPixel(walk_width + 3, walk_height + 3,
				walk_width, walk_height);
		treant_walk_up[1] = treantSheet.cropInPixel(1, walk_height + 3, walk_width,
				walk_height);
		treant_walk_up[2] = treant_walk_up[0];
		treant_walk_up[3] = treantSheet.cropInPixel(walk_width * 2 + 5,
				walk_height + 3, walk_width, walk_height);

		treant_walk_down[0] = treantSheet.cropInPixel(walk_width + 3, 1, walk_width,
				walk_height);
		treant_walk_down[1] = treantSheet.cropInPixel(1, 1, walk_width, walk_height);
		treant_walk_down[2] = treant_walk_down[0];
		treant_walk_down[3] = treantSheet.cropInPixel(walk_width * 2 + 5, 1,
				walk_width, walk_height);

		treant_walk_left[0] = treantSheet.cropInPixel(walk_width + 3,
				walk_height * 2 + 5, walk_width, walk_height);
		treant_walk_left[1] = treantSheet.cropInPixel(1, walk_height * 2 + 5,
				walk_width, walk_height);
		treant_walk_left[2] = treant_walk_left[0];
		treant_walk_left[3] = treantSheet.cropInPixel(walk_width * 2 + 5,
				walk_height * 2 + 5, walk_width, walk_height);

		treant_walk_right[0] = SpriteSheet.horizontalFlip(treant_walk_left[0]);
		treant_walk_right[1] = SpriteSheet.horizontalFlip(treant_walk_left[1]);
		treant_walk_right[2] = treant_walk_right[0];
		treant_walk_right[3] = SpriteSheet.horizontalFlip(treant_walk_left[3]);

		treant_attack_up[0] = treantSheet.cropInPixel(97, 76, attack_up_width,
				attack_up_height);
		treant_attack_up[1] = treantSheet.cropInPixel(99 + attack_up_width, 76,
				attack_up_width, attack_up_height);
		treant_attack_up[2] = treantSheet.cropInPixel(101 + attack_up_width * 2, 76,
				attack_up_width, attack_up_height);

		treant_attack_down[0] = treantSheet.cropInPixel(97, 1, attack_down_width,
				attack_down_height);
		treant_attack_down[1] = treantSheet.cropInPixel(99 + attack_down_width, 1,
				attack_down_width, attack_down_height);
		treant_attack_down[2] = treantSheet.cropInPixel(101 + attack_down_width * 2,
				1, attack_down_width, attack_down_height);

		treant_attack_left[0] = treantSheet.cropInPixel(1, 151, attack_side_width,
				attack_side_height);
		treant_attack_left[1] = treantSheet.cropInPixel(3 + attack_side_width, 151,
				attack_side_width, attack_side_height);
		treant_attack_left[2] = treantSheet.cropInPixel(5 + attack_side_width * 2,
				151, attack_side_width, attack_side_height);

		treant_attack_right[0] = SpriteSheet.horizontalFlip(treant_attack_left[0]);
		treant_attack_right[1] = SpriteSheet.horizontalFlip(treant_attack_left[1]);
		treant_attack_right[2] = SpriteSheet.horizontalFlip(treant_attack_left[2]);

		treant_die[0] = treantSheet.cropInPixel(210, 0, walk_width, walk_height);
		treant_die[1] = treantSheet.cropInPixel(210 + walk_width, 0, walk_width,
				walk_height);
		treant_die[2] = treantSheet.cropInPixel(210 + walk_width * 2, 0, walk_width,
				walk_height);
		treant_die[3] = treantSheet.cropInPixel(210 + walk_width * 3, 0, walk_width,
				walk_height);

	}

	/**
	 * Load all the wolf images.
	 */
	private static void loadEntityWolfImages() {

		SpriteSheet wolfSheet = new SpriteSheet(ImageLoader.load(entityWolfPath));
		wolf_idle_down = new BufferedImage[2];
		wolf_walk_down = new BufferedImage[6];
		wolf_walk_up = new BufferedImage[6];
		wolf_idle_up = new BufferedImage[2];
		wolf_walk_right = new BufferedImage[4];
		wolf_walk_left = new BufferedImage[4];
		wolf_run_right = new BufferedImage[4];
		wolf_run_left = new BufferedImage[4];
		wolf_attack_right = new BufferedImage[4];
		wolf_attack_left = new BufferedImage[4];
		wolf_attack_down = new BufferedImage[4];
		wolf_idle_right = new BufferedImage[2];
		wolf_idle_left = new BufferedImage[2];
		wolf_die = new BufferedImage[4];

		wolf_idle_down[0] = wolfSheet.crop(0, 0);
		wolf_idle_down[1] = wolfSheet.crop(1, 0);
		wolf_walk_down[0] = wolfSheet.crop(0, 1);
		wolf_walk_down[1] = wolfSheet.crop(1, 1);
		wolf_walk_down[2] = wolf_walk_down[0];
		wolf_walk_down[3] = SpriteSheet.horizontalFlip(wolf_walk_down[0]);
		wolf_walk_down[4] = SpriteSheet.horizontalFlip(wolf_walk_down[1]);
		wolf_walk_down[5] = wolf_walk_down[3];
		wolf_walk_up[0] = wolfSheet.crop(0, 2);
		wolf_walk_up[1] = wolfSheet.crop(1, 2);
		wolf_walk_up[2] = wolf_walk_up[0];
		wolf_walk_up[3] = SpriteSheet.horizontalFlip(wolf_walk_up[0]);
		wolf_walk_up[4] = SpriteSheet.horizontalFlip(wolf_walk_up[1]);
		wolf_walk_up[5] = wolf_walk_up[3];
		wolf_idle_up[0] = wolfSheet.crop(0, 3);
		wolf_idle_up[1] = SpriteSheet.horizontalFlip(wolf_idle_up[0]);

		int wid = 48, hei = 32;
		wolf_walk_right[0] = wolfSheet.cropInScale(0, 4, wid, hei);
		wolf_walk_right[1] = wolfSheet.cropInScale(1, 4, wid, hei);
		wolf_walk_right[2] = wolfSheet.cropInScale(2, 4, wid, hei);
		wolf_walk_right[3] = wolfSheet.cropInScale(3, 4, wid, hei);
		wolf_walk_left[0] = SpriteSheet.horizontalFlip(wolf_walk_right[0]);
		wolf_walk_left[1] = SpriteSheet.horizontalFlip(wolf_walk_right[1]);
		wolf_walk_left[2] = SpriteSheet.horizontalFlip(wolf_walk_right[2]);
		wolf_walk_left[3] = SpriteSheet.horizontalFlip(wolf_walk_right[3]);
		wolf_run_right[0] = wolfSheet.cropInScale(0, 5, wid, hei);
		wolf_run_right[1] = wolfSheet.cropInScale(1, 5, wid, hei);
		wolf_run_right[2] = wolfSheet.cropInScale(2, 5, wid, hei);
		wolf_run_right[3] = wolfSheet.cropInScale(3, 5, wid, hei);
		wolf_run_left[0] = SpriteSheet.horizontalFlip(wolf_run_right[0]);
		wolf_run_left[1] = SpriteSheet.horizontalFlip(wolf_run_right[1]);
		wolf_run_left[2] = SpriteSheet.horizontalFlip(wolf_run_right[2]);
		wolf_run_left[3] = SpriteSheet.horizontalFlip(wolf_run_right[3]);
		wolf_idle_right[0] = wolfSheet.cropInScale(0, 6, wid, hei);
		wolf_idle_right[1] = wolfSheet.cropInScale(1, 6, wid, hei);
		wolf_idle_left[0] = SpriteSheet.horizontalFlip(wolf_idle_right[0]);
		wolf_idle_left[1] = SpriteSheet.horizontalFlip(wolf_idle_right[1]);

		wolf_attack_right[0] = wolfSheet.cropInScale(0, 7, wid, hei);
		wolf_attack_right[1] = wolfSheet.cropInScale(1, 7, wid, hei);
		wolf_attack_right[2] = wolfSheet.cropInScale(2, 7, wid, hei);
		wolf_attack_right[3] = wolfSheet.cropInScale(3, 7, wid, hei);
		wolf_attack_left[0] = SpriteSheet.horizontalFlip(wolf_attack_right[0]);
		wolf_attack_left[1] = SpriteSheet.horizontalFlip(wolf_attack_right[1]);
		wolf_attack_left[2] = SpriteSheet.horizontalFlip(wolf_attack_right[2]);
		wolf_attack_left[3] = SpriteSheet.horizontalFlip(wolf_attack_right[3]);
		wolf_attack_down[0] = wolfSheet.crop(3, 0);
		wolf_attack_down[1] = wolfSheet.crop(4, 0);
		wolf_attack_down[2] = wolfSheet.crop(5, 0);
		wolf_attack_down[3] = wolfSheet.crop(6, 0);

		wolf_die[0] = wolfSheet.crop(3, 1);
		wolf_die[1] = wolfSheet.crop(4, 1);
		wolf_die[2] = wolfSheet.crop(5, 1);
		wolf_die[3] = wolfSheet.crop(6, 1);

	}

	private static void loadEntityPersonImages() {

		SpriteSheet personSheet = new SpriteSheet(
				ImageLoader.load(entityPersonPath));

		person_walk_up = new BufferedImage[4];
		person_walk_down = new BufferedImage[4];
		person_walk_left = new BufferedImage[4];
		person_walk_right = new BufferedImage[4];
		person_die = new BufferedImage[4];

		person_walk_up[0] = personSheet.crop(0, 0);
		person_walk_up[1] = personSheet.crop(1, 0);
		person_walk_up[2] = personSheet.crop(2, 0);
		person_walk_up[3] = personSheet.crop(3, 0);

		person_walk_down[0] = personSheet.crop(0, 2);
		person_walk_down[1] = personSheet.crop(1, 2);
		person_walk_down[2] = personSheet.crop(2, 2);
		person_walk_down[3] = personSheet.crop(3, 2);

		person_walk_left[0] = personSheet.crop(0, 1);
		person_walk_left[1] = personSheet.crop(1, 1);
		person_walk_left[2] = personSheet.crop(2, 1);
		person_walk_left[3] = personSheet.crop(3, 1);

		person_walk_right[0] = SpriteSheet.horizontalFlip(person_walk_left[0]);
		person_walk_right[1] = SpriteSheet.horizontalFlip(person_walk_left[1]);
		person_walk_right[2] = SpriteSheet.horizontalFlip(person_walk_left[2]);
		person_walk_right[3] = SpriteSheet.horizontalFlip(person_walk_left[3]);

		person_die[0] = personSheet.crop(0, 3);
		person_die[1] = personSheet.crop(1, 3);
		person_die[2] = personSheet.crop(2, 3);
		person_die[3] = personSheet.crop(3, 3);

	}

	/**
	 * Load all images for EntityGolem.
	 */
	private static void loadEntityGolemImages() {

		SpriteSheet golemSheet = new SpriteSheet(ImageLoader.load(entityGolemPath));

		golem_walk_up = new BufferedImage[7];
		golem_walk_down = new BufferedImage[7];
		golem_walk_left = new BufferedImage[7];
		golem_walk_right = new BufferedImage[7];
		golem_attack_up = new BufferedImage[7];
		golem_attack_down = new BufferedImage[7];
		golem_attack_left = new BufferedImage[7];
		golem_attack_right = new BufferedImage[7];
		golem_die = new BufferedImage[6];

		int normal_width = 64;
		int normal_height = 64;
		int attack_height = 96;
		int attackImageYStart = normal_height * 3;
		int deathImageYStart = attackImageYStart + attack_height * 3;

		golem_walk_up[0] = golemSheet.cropInScale(0, 0, normal_width, normal_height);
		golem_walk_up[1] = golemSheet.cropInScale(1, 0, normal_width, normal_height);
		golem_walk_up[2] = golemSheet.cropInScale(2, 0, normal_width, normal_height);
		golem_walk_up[3] = golemSheet.cropInScale(3, 0, normal_width, normal_height);
		golem_walk_up[4] = golemSheet.cropInScale(4, 0, normal_width, normal_height);
		golem_walk_up[5] = golemSheet.cropInScale(5, 0, normal_width, normal_height);
		golem_walk_up[6] = golemSheet.cropInScale(6, 0, normal_width, normal_height);

		golem_walk_down[0] = golemSheet.cropInScale(0, 2, normal_width,
				normal_height);
		golem_walk_down[1] = golemSheet.cropInScale(1, 2, normal_width,
				normal_height);
		golem_walk_down[2] = golemSheet.cropInScale(2, 2, normal_width,
				normal_height);
		golem_walk_down[3] = golemSheet.cropInScale(3, 2, normal_width,
				normal_height);
		golem_walk_down[4] = golemSheet.cropInScale(4, 2, normal_width,
				normal_height);
		golem_walk_down[5] = golemSheet.cropInScale(5, 2, normal_width,
				normal_height);
		golem_walk_down[6] = golemSheet.cropInScale(6, 2, normal_width,
				normal_height);

		golem_walk_left[0] = golemSheet.cropInScale(0, 1, normal_width,
				normal_height);
		golem_walk_left[1] = golemSheet.cropInScale(1, 1, normal_width,
				normal_height);
		golem_walk_left[2] = golemSheet.cropInScale(2, 1, normal_width,
				normal_height);
		golem_walk_left[3] = golemSheet.cropInScale(3, 1, normal_width,
				normal_height);
		golem_walk_left[4] = golemSheet.cropInScale(4, 1, normal_width,
				normal_height);
		golem_walk_left[5] = golemSheet.cropInScale(5, 1, normal_width,
				normal_height);
		golem_walk_left[6] = golemSheet.cropInScale(6, 1, normal_width,
				normal_height);

		golem_walk_right[0] = SpriteSheet.horizontalFlip(golem_walk_left[0]);
		golem_walk_right[1] = SpriteSheet.horizontalFlip(golem_walk_left[1]);
		golem_walk_right[2] = SpriteSheet.horizontalFlip(golem_walk_left[2]);
		golem_walk_right[3] = SpriteSheet.horizontalFlip(golem_walk_left[3]);
		golem_walk_right[4] = SpriteSheet.horizontalFlip(golem_walk_left[4]);
		golem_walk_right[5] = SpriteSheet.horizontalFlip(golem_walk_left[5]);
		golem_walk_right[6] = SpriteSheet.horizontalFlip(golem_walk_left[6]);

		golem_attack_up[0] = golemSheet.cropInScale(0, 0, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_up[1] = golemSheet.cropInScale(1, 0, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_up[2] = golemSheet.cropInScale(2, 0, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_up[3] = golemSheet.cropInScale(3, 0, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_up[4] = golemSheet.cropInScale(4, 0, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_up[5] = golemSheet.cropInScale(5, 0, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_up[6] = golemSheet.cropInScale(6, 0, normal_width,
				attack_height, 0, attackImageYStart);

		golem_attack_down[0] = golemSheet.cropInScale(0, 2, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_down[1] = golemSheet.cropInScale(1, 2, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_down[2] = golemSheet.cropInScale(2, 2, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_down[3] = golemSheet.cropInScale(3, 2, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_down[4] = golemSheet.cropInScale(4, 2, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_down[5] = golemSheet.cropInScale(5, 2, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_down[6] = golemSheet.cropInScale(6, 2, normal_width,
				attack_height, 0, attackImageYStart);

		golem_attack_left[0] = golemSheet.cropInScale(0, 1, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_left[1] = golemSheet.cropInScale(1, 1, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_left[2] = golemSheet.cropInScale(2, 1, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_left[3] = golemSheet.cropInScale(3, 1, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_left[4] = golemSheet.cropInScale(4, 1, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_left[5] = golemSheet.cropInScale(5, 1, normal_width,
				attack_height, 0, attackImageYStart);
		golem_attack_left[6] = golemSheet.cropInScale(6, 1, normal_width,
				attack_height, 0, attackImageYStart);

		golem_attack_right[0] = SpriteSheet.horizontalFlip(golem_attack_left[0]);
		golem_attack_right[1] = SpriteSheet.horizontalFlip(golem_attack_left[1]);
		golem_attack_right[2] = SpriteSheet.horizontalFlip(golem_attack_left[2]);
		golem_attack_right[3] = SpriteSheet.horizontalFlip(golem_attack_left[3]);
		golem_attack_right[4] = SpriteSheet.horizontalFlip(golem_attack_left[4]);
		golem_attack_right[5] = SpriteSheet.horizontalFlip(golem_attack_left[5]);
		golem_attack_right[6] = SpriteSheet.horizontalFlip(golem_attack_left[6]);

		golem_die[0] = golemSheet.cropInScale(0, 0, normal_width, normal_height, 0,
				deathImageYStart);
		golem_die[1] = golemSheet.cropInScale(1, 0, normal_width, normal_height, 0,
				deathImageYStart);
		golem_die[2] = golemSheet.cropInScale(2, 0, normal_width, normal_height, 0,
				deathImageYStart);
		golem_die[3] = golemSheet.cropInScale(3, 0, normal_width, normal_height, 0,
				deathImageYStart);
		golem_die[4] = golemSheet.cropInScale(4, 0, normal_width, normal_height, 0,
				deathImageYStart);
		golem_die[5] = golemSheet.cropInScale(5, 0, normal_width, normal_height, 0,
				deathImageYStart);

	}

}
